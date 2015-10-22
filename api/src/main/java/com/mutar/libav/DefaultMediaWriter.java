/*
 * Copyright (C) 2012 Ondrej Perutka
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.mutar.libav;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.bridj.IntValuedEnum;
import org.bridj.Pointer;

import com.mutar.libav.api.IMediaWriter;
import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.api.util.AVCodecLibraryUtil;
import com.mutar.libav.api.util.AVFormatLibraryUtil;
import com.mutar.libav.api.util.AVUtilLibraryUtil;
import com.mutar.libav.bridge.avcodec.AVCodec;
import com.mutar.libav.bridge.avcodec.AVCodecContext;
import com.mutar.libav.bridge.avcodec.AVPacket;
import com.mutar.libav.bridge.avcodec.AvcodecLibrary;
import com.mutar.libav.bridge.avcodec.AvcodecLibrary.AVCodecID;
import com.mutar.libav.bridge.avformat.AVFormatContext;
import com.mutar.libav.bridge.avformat.AVOutputFormat;
import com.mutar.libav.bridge.avformat.AVStream;
import com.mutar.libav.bridge.avformat.AvformatLibrary;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVSampleFormat;

public class DefaultMediaWriter implements IMediaWriter {

    private AVFormatContext formatContext;

    private AVStream[] streams;
    private int[] aStreams;
    private int[] vStreams;

    private boolean interleave;

    /**
     * Create a new media writer.
     *
     * @param url a destination URL
     * @param outputFormatName a name of the output format (if it is null, the
     * format is guessed from the given URL)
     * @throws LibavException if an error occurs while opening the output
     */
    public DefaultMediaWriter(String url, String outputFormatName) throws LibavException {
        formatContext = AVFormatLibraryUtil.createMedia(url, outputFormatName);

        streams = new AVStream[0];
        aStreams = new int[0];
        vStreams = new int[0];

        interleave = true;
    }

    @Override
    public boolean getInterleave() {
        return interleave;
    }

    @Override
    public void setInterleave(boolean interleave) {
        this.interleave = interleave;
    }

    @Override
    public AVFormatContext getFormatContext() {
        return formatContext;
    }

    private synchronized void reloadStreams() {
        if (isClosed())
            return;

        streams = AVFormatLibraryUtil.getStreams(formatContext);
        if (streams == null)
            streams = new AVStream[0];

        AVCodecContext[] ccs = new AVCodecContext[streams.length];
        int v = 0, a = 0;

        for (int i = 0; i < streams.length; i++) {
            ccs[i] = streams[i].codec().get();
            //XXX TEST IT
            switch (ccs[i].codec_type().iterator().next()) {
                case AVMEDIA_TYPE_VIDEO: v++; break;
                case AVMEDIA_TYPE_AUDIO: a++; break;
                default: break;
            }
        }

        vStreams = new int[v];
        aStreams = new int[a];

        v = a = 0;
        for (int i = 0; i < streams.length; i++) {
            switch (ccs[i].codec_type().iterator().next()) {
                case AVMEDIA_TYPE_VIDEO: vStreams[v++] = i; break;
                case AVMEDIA_TYPE_AUDIO: aStreams[a++] = i; break;
                default: break;
            }
        }
    }

    @Override
    public int getStreamCount() {
        return streams.length;
    }

    @Override
    public AVStream getStream(int streamIndex) {
        return streams[streamIndex];
    }

    @Override
    public int getVideoStreamCount() {
        return vStreams.length;
    }

    @Override
    public synchronized int addVideoStream(IntValuedEnum<AVCodecID> codecId, int width, int height) throws LibavException {
        if (isClosed())
            throw new IllegalStateException("the media stream has been closed");

        AVCodec codec = AVCodecLibraryUtil.findEncoder(codecId);
        AVStream stream = AVFormatLibraryUtil.newStream(formatContext);
        AVCodecContext cc = stream.codec().get();
        AVCodecLibraryUtil.getDefaults(cc, codec);

        cc.codec_id(codecId);
        cc.width(width);
        cc.height(height);
        cc.time_base().num(1);
        cc.time_base().den(25);

        AVOutputFormat ofw = formatContext.oformat().get();
        if ((ofw.flags() & AvformatLibrary.AVFMT_GLOBALHEADER) != 0)
            cc.flags(cc.flags() | AvcodecLibrary.CODEC_FLAG_GLOBAL_HEADER);

        reloadStreams();
        return vStreams.length - 1;
    }

    @Override
    public AVStream getVideoStream(int videoStreamIndex) {
        return getStream(vStreams[videoStreamIndex]);
    }

    @Override
    public int getAudioStreamCount() {
        return aStreams.length;
    }

    @Override
    public synchronized int addAudioStream(IntValuedEnum<AVCodecID> codecId, int sampleRate, IntValuedEnum<AVSampleFormat> sampleFormat, int channelCount) throws LibavException {
        if (isClosed())
            throw new IllegalStateException("the media stream has been closed");

        AVCodec codec = AVCodecLibraryUtil.findEncoder(codecId);
        AVStream stream = AVFormatLibraryUtil.newStream(formatContext);
        AVCodecContext cc = stream.codec().get();

        AVCodecLibraryUtil.getDefaults(cc, codec);

        cc.codec_id(codecId);
        cc.bit_rate(192000);
        cc.sample_rate(sampleRate);
        cc.sample_fmt(AVUtilLibraryUtil.getSupportedFormat(codec.sample_fmts(), sampleFormat));
        cc.channels(channelCount);
        cc.channel_layout(AVUtilLibraryUtil.getDefaultChannelLayout(channelCount));

        AVOutputFormat ofw = formatContext.oformat().get();
        if ((ofw.flags() & AvformatLibrary.AVFMT_GLOBALHEADER) != 0)
            cc.flags(cc.flags() | AvcodecLibrary.CODEC_FLAG_GLOBAL_HEADER);

        reloadStreams();
        return aStreams.length - 1;
    }

    @Override
    public AVStream getAudioStream(int audioStreamIndex) {
        return getStream(aStreams[audioStreamIndex]);
    }

    @Override
    public synchronized void writeHeader() throws LibavException {
        if (isClosed())
            return;
        AVFormatLibraryUtil.writeHeader(formatContext);
    }

    @Override
    public synchronized void writeTrailer() throws LibavException {
        if (isClosed())
            return;
        AVFormatLibraryUtil.writeTrailer(formatContext);
    }

    @Override
    public synchronized void processPacket(Object producer, AVPacket packet) throws LibavException {
        if (interleave)
            AVFormatLibraryUtil.interleavedWritePacket(formatContext, packet);
        else
            AVFormatLibraryUtil.writePacket(formatContext, packet);
    }

    @Override
    public synchronized String getSdp() throws LibavException {
        if (isClosed())
            throw new IllegalStateException("the media stream has been closed");

        return getSdp(new AVFormatContext[] { formatContext });
    }

    @Override
    public synchronized void createSdpFile(String fileName) throws LibavException, FileNotFoundException {
        if (isClosed())
            throw new IllegalStateException("the media stream has been closed");

        createSdpFile(fileName, new AVFormatContext[] { formatContext });
    }

    @Override
    public synchronized void close() throws LibavException {
        reloadStreams();
        for (int i = 0; i < streams.length; i++) {
            AVUtilLibraryUtil.av_free(streams[i].codec());
            streams[i].codec(null);
            AVUtilLibraryUtil.av_free(streams[i]);
            formatContext.streams().set(i, null);
        }
        AVFormatLibraryUtil.close(formatContext, true);

        formatContext = null;
        streams = new AVStream[0];
        aStreams = new int[0];
        vStreams = new int[0];
    }

    @Override
    public boolean isClosed() {
        return formatContext == null;
    }

    public static String getSdp(DefaultMediaWriter[] mediaWriter) throws LibavException {
        AVFormatContext[] contexts = new AVFormatContext[mediaWriter.length];
        for (int i = 0; i < mediaWriter.length; i++)
            contexts[i] = mediaWriter[i].getFormatContext();

        return getSdp(contexts);
    }

    public static String getSdp(AVFormatContext[] contexts) throws LibavException {
        Pointer<Byte> data;

        Pointer<Pointer<AVFormatContext>> fcs = Pointer.allocatePointers(AVFormatContext.class, contexts.length);
        for (int i = 0; i < contexts.length; i++) {
            if (contexts[i].priv_data() == null)
                throw new LibavException("all headers must be written before creating an SDP");
            fcs.set(i, Pointer.getPointer(contexts[i]));
        }

        data = Pointer.allocateBytes(4096);
        if (data == null)
            throw new LibavException("unable to allocate memory to create the SDP");
        if (AVFormatLibraryUtil.sdpCreate(fcs, contexts.length, data, 4096) != 0)
            throw new LibavException("unable to generate the SDP");

        return data.getStringAtOffset(0, Pointer.StringType.C, Charset.forName("UTF-8"));
    }

    public static void createSdpFile(String fileName, DefaultMediaWriter[] mediaWriter) throws LibavException, FileNotFoundException {
        AVFormatContext[] contexts = new AVFormatContext[mediaWriter.length];
        for (int i = 0; i < mediaWriter.length; i++)
            contexts[i] = mediaWriter[i].getFormatContext();

        createSdpFile(fileName, contexts);
    }

    public static void createSdpFile(String fileName, AVFormatContext[] contexts) throws LibavException, FileNotFoundException {
        PrintWriter pw = null;

        try {
            pw = new PrintWriter(fileName);
            pw.printf("SDP:\n%s\n", getSdp(contexts));
            pw.flush();
        } finally {
            if (pw != null)
                pw.close();
        }
    }

}
