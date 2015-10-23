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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mutar.libav.api.IMediaReader;
import com.mutar.libav.api.data.BufferedPacketReader;
import com.mutar.libav.api.data.IPacketConsumer;
import com.mutar.libav.api.data.PacketPool.PooledPacket;
import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.api.util.AVFormatLibraryUtil;
import com.mutar.libav.api.util.AVRationalUtils;
import com.mutar.libav.api.util.Buffer;
import com.mutar.libav.bridge.avcodec.AVCodecContext;
import com.mutar.libav.bridge.avformat.AVFormatContext;
import com.mutar.libav.bridge.avformat.AVIOContext;
import com.mutar.libav.bridge.avformat.AVInputFormat;
import com.mutar.libav.bridge.avformat.AVStream;
import com.mutar.libav.bridge.avutil.AVRational;


/**
 * Default implementation of the media reader interface.
 *
 * @author Ondrej Perutka
 */
public class DefaultMediaReader implements IMediaReader {

    private AVFormatContext formatContext;

    private BufferedPacketReader packetReader;
    private List<Buffer<PooledPacket>> streamBuffers;
    private boolean[] bufferingEnabled;

    private AVStream[] streams;
    private int[] vStreams;
    private int[] aStreams;

    private List<Set<IPacketConsumer>> packetConsumers;

    private AVRational[] timeBases;
    private long position;

    /**
     * Open the given media URL.
     *
     * @param url a media URL
     * @throws LibavException if an error occurs while opening the media
     */
    public DefaultMediaReader(String url) throws LibavException {
        this(AVFormatLibraryUtil.openMedia(url));
    }

    /**
     * Open the given media URL forcing the given input format.
     *
     * @param url a media URL
     * @param inputFormat input format short name
     * @throws LibavException if an error occurs while opening the media
     */
    public DefaultMediaReader(String url, String inputFormat) throws LibavException {
        this(AVFormatLibraryUtil.openMedia(url, inputFormat));
    }

    /**
     * Open the given media URL forcing the given input format.
     *
     * @param url a media URL
     * @param inputFormat input format
     * @throws LibavException if an error occurs while opening the media
     */
    public DefaultMediaReader(String url, AVInputFormat inputFormat) throws LibavException {
        this(AVFormatLibraryUtil.openMedia(url, inputFormat));
    }

    private DefaultMediaReader(AVFormatContext formatContext) throws LibavException {
        this.formatContext = formatContext;

        packetReader = new BufferedPacketReader(formatContext, 50);

        AVFormatLibraryUtil.findStreamInfo(formatContext);
        streams = AVFormatLibraryUtil.getStreams(formatContext);
        AVCodecContext[] ccs = new AVCodecContext[streams.length];
        streamBuffers = new ArrayList<Buffer<PooledPacket>>();
        bufferingEnabled = new boolean[streams.length];
        packetConsumers = new ArrayList<Set<IPacketConsumer>>();
        timeBases = new AVRational[streams.length];
        int v = 0, a = 0;

        for (int i = 0; i < streams.length; i++) {
            ccs[i] = streams[i].codec().get();
            streamBuffers.add(new Buffer<PooledPacket>(20));
            bufferingEnabled[i] = false;
            packetConsumers.add(Collections.synchronizedSet(new HashSet<IPacketConsumer>()));
            switch (ccs[i].codec_type().iterator().next()) {
                case AVMEDIA_TYPE_VIDEO: v++; break;
                case AVMEDIA_TYPE_AUDIO: a++; break;
                default: break;
            }
            timeBases[i] = AVRationalUtils.mul(streams[i].time_base(),1000);
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

        position = 0;
    }

    @Override
    public AVFormatContext getFormatContext() {
        return formatContext;
    }

    @Override
    public int getStreamCount() {
        return streams.length;
    }

    @Override
    public void addPacketConsumer(int streamIndex, IPacketConsumer consumer) {
        packetConsumers.get(streamIndex).add(consumer);
    }

    @Override
    public void removePacketConsumer(int streamIndex, IPacketConsumer consumer) {
        packetConsumers.get(streamIndex).remove(consumer);
    }

    @Override
    public boolean containsPacketConsumer(int streamIndex, IPacketConsumer consumer) {
        return packetConsumers.get(streamIndex).contains(consumer);
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
    public AVStream getVideoStream(int videoStreamIndex) {
        return streams[vStreams[videoStreamIndex]];
    }

    @Override
    public void addVideoPacketConsumer(int videoStreamIndex, IPacketConsumer consumer) {
        addPacketConsumer(vStreams[videoStreamIndex], consumer);
    }

    @Override
    public void removeVideoPacketConsumer(int videoStreamIndex, IPacketConsumer consumer) {
        removePacketConsumer(vStreams[videoStreamIndex], consumer);
    }

    @Override
    public boolean containsVideoPacketConsumer(int videoStreamIndex, IPacketConsumer consumer) {
        return containsPacketConsumer(vStreams[videoStreamIndex], consumer);
    }

    @Override
    public int getAudioStreamCount() {
        return aStreams.length;
    }

    @Override
    public AVStream getAudioStream(int audioStremIndex) {
        return streams[aStreams[audioStremIndex]];
    }

    @Override
    public void addAudioPacketConsumer(int audioStreamIndex, IPacketConsumer consumer) {
        addPacketConsumer(aStreams[audioStreamIndex], consumer);
    }

    @Override
    public void removeAudioPacketConsumer(int audioStreamIndex, IPacketConsumer consumer) {
        removePacketConsumer(aStreams[audioStreamIndex], consumer);
    }

    @Override
    public boolean containsAudioPacketConsumer(int audioStreamIndex, IPacketConsumer consumer) {
        return containsPacketConsumer(aStreams[audioStreamIndex], consumer);
    }

    @Override
    public long getDuration() {
        return formatContext.duration();
    }

    @Override
    public long getStreamDuration(int streamIndex) {
        AVStream stream = getStream(streamIndex);
        AVRational tb = stream.time_base();

        return AVRationalUtils.longValue(AVRationalUtils.mul(AVRationalUtils.mul(tb, 1000), stream.duration()));
    }

    @Override
    public long getAudioStreamDuration(int audioStreamIndex) {
        return getStreamDuration(aStreams[audioStreamIndex]);
    }

    @Override
    public long getVideoStreamDuration(int videoStreamIndex) {
        return getStreamDuration(vStreams[videoStreamIndex]);
    }

    @Override
    public long getPosition() {
        return position;
    }

    @Override
    public boolean isSeekable() {
        if (isClosed())
            return false;

        AVIOContext ioc = new AVIOContext(formatContext.pb());
        return ioc == null ? false : ioc.seekable() != 0;
    }

    @Override
    public synchronized void seek(long time) throws LibavException {
        if (isClosed())
            return;

        dropAllBuffers();
        packetReader.resetEof();
        AVFormatLibraryUtil.seekFile(formatContext, time - 10000, time, time + 500);

        position = time;
    }

    @Override
    public synchronized void dropAllBuffers() {
        packetReader.dropBuffer();
        for (Buffer<PooledPacket> sb : streamBuffers) {
            while (sb.getItemCount() > 0)
                sb.get().free();
        }
    }

    @Override
    public boolean readNextPacket() throws LibavException {
        PooledPacket pw;

        synchronized (this) {
            if (isClosed())
                return false;
            pw = packetReader.nextPacket();
        }

        if (pw != null)
            sendPacket(pw);

        return pw != null;
    }

    @Override
    public boolean readNextPacket(int streamIndex) throws LibavException {
        PooledPacket pw;

        synchronized (this) {
            if (isClosed())
                return false;

            setStreamBufferingEnabled(streamIndex, true);
            if (streamBuffers.get(streamIndex).getItemCount() > 0)
                pw = streamBuffers.get(streamIndex).get();
            else {
                pw = packetReader.nextPacket();
                if (pw == null)
                    return false;
                else if (pw.getStreamIndex() != streamIndex) {
                    if (isStreamBufferingEnabled(pw.getStreamIndex()))
                        streamBuffers.get(pw.getStreamIndex()).put(pw);
                    else
                        pw.free();
                }
            }
        }

        if (pw.getStreamIndex() == streamIndex)
            sendPacket(pw);

        return true;
    }

    @Override
    public boolean readNextVideoPacket(int videoStreamIndex) throws LibavException {
        return readNextPacket(vStreams[videoStreamIndex]);
    }

    @Override
    public boolean readNextAudioPacket(int audioStreamIndex) throws LibavException {
        return readNextPacket(aStreams[audioStreamIndex]);
    }

    @Override
    public boolean isStreamBufferingEnabled(int streamIndex) {
        return bufferingEnabled[streamIndex];
    }

    @Override
    public void setStreamBufferingEnabled(int streamIndex, boolean enabled) {
        synchronized (this) {
            bufferingEnabled[streamIndex] = enabled;
            if (!enabled) {
                Buffer<PooledPacket> buf = streamBuffers.get(streamIndex);
                while (buf.getItemCount() > 0)
                    buf.get().free();
            }
        }
    }

    @Override
    public boolean isAudioStreamBufferingEnabled(int audioStreamIndex) {
        return isStreamBufferingEnabled(aStreams[audioStreamIndex]);
    }

    @Override
    public void setAudioStreamBufferingEnabled(int audioStreamIndex, boolean enabled) {
        setStreamBufferingEnabled(aStreams[audioStreamIndex], enabled);
    }

    @Override
    public boolean isVideoStreamBufferingEnabled(int videoStreamIndex) {
        return isStreamBufferingEnabled(vStreams[videoStreamIndex]);
    }

    @Override
    public void setVideoStreamBufferingEnabled(int videoStreamIndex, boolean enabled) {
        setStreamBufferingEnabled(vStreams[videoStreamIndex], enabled);
    }

    @Override
    public void close() throws LibavException {
        synchronized (this) {
            for (Buffer<PooledPacket> sb : streamBuffers) {
                while (sb.getItemCount() > 0)
                    sb.get().free();
            }

            packetReader.close();
            if (formatContext != null)
                AVFormatLibraryUtil.close(formatContext, false);

            formatContext = null;
        }
    }

    @Override
    public boolean isClosed() {
        return formatContext == null;
    }

    private void sendPacket(PooledPacket packet) throws LibavException {
        Set<IPacketConsumer> pc = packetConsumers.get(packet.getStreamIndex());

        if (packet.getDts() > 0)
            position = AVRationalUtils.longValue(AVRationalUtils.mul(timeBases[packet.getStreamIndex()], packet.getDts()));

        synchronized (pc) {
            for (IPacketConsumer c : pc)
                c.processPacket(this, packet.getDelegate());
        }
        packet.free();
    }

}
