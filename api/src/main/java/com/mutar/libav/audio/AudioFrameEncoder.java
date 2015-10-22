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
package com.mutar.libav.audio;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bridj.IntValuedEnum;
import org.bridj.Pointer;

import com.mutar.libav.api.IEncoder;
import com.mutar.libav.api.ITimestampGenerator;
import com.mutar.libav.api.data.IPacketConsumer;
import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.api.time.CopyTimestampGenerator;
import com.mutar.libav.api.util.AVCodecLibraryUtil;
import com.mutar.libav.api.util.AVRationalUtils;
import com.mutar.libav.api.util.AVUtilLibraryUtil;
import com.mutar.libav.bridge.avcodec.AVCodec;
import com.mutar.libav.bridge.avcodec.AVCodecContext;
import com.mutar.libav.bridge.avcodec.AVPacket;
import com.mutar.libav.bridge.avcodec.AvcodecLibrary;
import com.mutar.libav.bridge.avformat.AVStream;
import com.mutar.libav.bridge.avutil.AVFrame;
import com.mutar.libav.bridge.avutil.AVRational;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVMediaType;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVSampleFormat;

public class AudioFrameEncoder implements IEncoder {

    private final AVStream stream;
    private final AVCodecContext cc;
    private boolean initialized;
    private boolean smallLastFrame;

    private AVFrame tmpFrame;
    private Pointer<Byte> buffer;
    private int bufferSize;
    private int bufferSampleCapacity;
    private Pointer<Pointer<Byte>> planes;
    private int planeCount;
    private int frameSize;
    private int frameSampleCount;
    private long frameDuration;
    private AVRational byteDuration;

    private AVPacket packet;

    private long flushFramePts;
    private int offset;
    private AVRational ptsTransformBase;
    private ITimestampGenerator timestampGenerator;

    private final Set<IPacketConsumer> consumers;

    /**
     * Create a new audio frame wncoder for the given audio stream.
     *
     * @param stream an audio stream
     * @throws LibavException if the encoder cannot be created for some reason
     * (caused by the Libav)
     */
    public AudioFrameEncoder(AVStream stream) throws LibavException {
        this.stream = stream;

        cc = stream.codec().get();
        if (cc.codec_type() != AVMediaType.AVMEDIA_TYPE_AUDIO)
            throw new IllegalArgumentException("not an audio stream");

        initialized = false;
        smallLastFrame = false;

        tmpFrame = AVUtilLibraryUtil.allocateFrame();
        bufferSize = AVCodecLibraryUtil.AVCODEC_MAX_AUDIO_FRAME_SIZE;
        buffer = AVUtilLibraryUtil.malloc(bufferSize + AvcodecLibrary.FF_INPUT_BUFFER_PADDING_SIZE);
        bufferSampleCapacity = 0;
        planes = null;
        planeCount = 0;
        frameSize = 0;
        frameSampleCount = 0;
        frameDuration = 0;
        byteDuration = null;

        packet = AVCodecLibraryUtil.alloc_packet();

        flushFramePts = 0;
        ptsTransformBase = null;
        timestampGenerator = new CopyTimestampGenerator();

        consumers = Collections.synchronizedSet(new HashSet<IPacketConsumer>());
    }

    @Override
    public AVCodecContext getCodecContext() {
        return cc;
    }

    @Override
    public AVStream getStream() {
        return stream;
    }

    @Override
    public ITimestampGenerator getTimestampGenerator() {
        return timestampGenerator;
    }

    @Override
    public void setTimestampGenerator(ITimestampGenerator timestampGenerator) {
        this.timestampGenerator = timestampGenerator;
    }

    @Override
    public synchronized void close() {
        AVCodecLibraryUtil.close(cc);
        if (packet != null)
            AVCodecLibraryUtil.free(packet);
        if (buffer != null)
            AVUtilLibraryUtil.av_free(buffer);
        if (tmpFrame != null)
            AVUtilLibraryUtil.free(tmpFrame);

        packet = null;
        buffer = null;
        tmpFrame = null;
        planes = null;
    }

    @Override
    public boolean isClosed() {
        return packet == null;
    }

    @Override
    public synchronized void processFrame(Object producer, AVFrame frame) throws LibavException {
        if (isClosed())
            return;

        initEncoder();

        long pts;
        while ((pts = timestampGenerator.nextFrame(frame.pts())) >= 0)
            encodeFrame(frame, pts);
    }

    @Override
    public synchronized void flush() throws LibavException {
        if (isClosed())
            return;

        initEncoder();

        boolean flush = true;
        while (flush)
            flush = flushFrame();
    }

    private void initEncoder() throws LibavException {
        if (initialized)
            return;

        AVCodec codec = AVCodecLibraryUtil.findEncoder(cc.codec_id());

        smallLastFrame = (codec.capabilities() & AvcodecLibrary.CODEC_CAP_SMALL_LAST_FRAME) == AvcodecLibrary.CODEC_CAP_SMALL_LAST_FRAME;
        frameSampleCount = cc.frame_size();
        if ((codec.capabilities() & AvcodecLibrary.CODEC_CAP_VARIABLE_FRAME_SIZE) == AvcodecLibrary.CODEC_CAP_VARIABLE_FRAME_SIZE)
            frameSampleCount = 8192;
        if (frameSampleCount <= 1) // keep compatibility with older PCM encoders
            frameSampleCount = 8192;

        IntValuedEnum<AVSampleFormat > sampleFormat = cc.sample_fmt();
        int bytesPerSample = AVUtilLibraryUtil.getBytesPerSample(sampleFormat);
        int channelCount = cc.channels();

        frameSize = frameSampleCount * bytesPerSample;
        if (AVUtilLibraryUtil.isPlanar(sampleFormat))
            planeCount = channelCount;
        else {
            frameSize *= channelCount;
            planeCount = 1;
        }

        planes = Pointer.allocatePointers(Byte.class, planeCount);
        int lineSize = bufferSize / planeCount;
        lineSize -= lineSize % bytesPerSample;
        for (int i = 0; i < planeCount; i++)
            planes.set(i, buffer.offset(i * lineSize));

        bufferSampleCapacity = lineSize * planeCount / (channelCount * bytesPerSample);

        frameDuration = 1000 * frameSampleCount / cc.sample_rate();
        byteDuration = AVRationalUtils.createNew(frameDuration, frameSize);
        offset = 0;

        // propper time base is set after avformat_write_header() call
        ptsTransformBase = AVRationalUtils.invert(AVRationalUtils.mul(stream.time_base(), 1000));

        initialized = true;
    }

    private boolean flushFrame() throws LibavException {
        AVCodecLibraryUtil.alloc(packet);
        packet.data(null);
        packet.size(0);

        int sampleCount = offset * planeCount / (cc.channels() * AVUtilLibraryUtil.getBytesPerSample(cc.sample_fmt()));
        if (sampleCount > 0) {
            if (sampleCount < frameSampleCount && !smallLastFrame) {
                sampleCount = frameSampleCount;
                for (int i = 0; i < planeCount; i++)
                    planes.get(i).clearBytesAtOffset(offset, frameSize - offset, (byte)0);
                offset = frameSize;
            }
            AVCodecLibraryUtil.fillAudioFrame(tmpFrame, sampleCount, cc.channels(), cc.sample_fmt(), buffer, bufferSize, bufferSampleCapacity);
        }
        offset = 0;

        boolean result;
        if (result = AVCodecLibraryUtil.encodeAudioFrame(cc, sampleCount == 0 ? null : tmpFrame, packet)) {
            packet.stream_index(stream.index());
            packet.pts(AVRationalUtils.longValue(AVRationalUtils.mul(ptsTransformBase,flushFramePts)));
            packet.dts(packet.pts());
            sendPacket(packet);
            flushFramePts += frameDuration;
        }

        return result;
    }

    private void encodeFrame(AVFrame frame, long pts) throws LibavException {
        int lineSize = frame.linesize().get(0);
        int size = lineSize;
        pts -= AVRationalUtils.longValue(AVRationalUtils.mul(byteDuration, offset));

        while (size > 0) {
            size -= appendSamples(frame, lineSize - size);

            if (offset == frameSize) {
                offset = 0;
                AVCodecLibraryUtil.fillAudioFrame(tmpFrame, frameSampleCount, cc.channels(), cc.sample_fmt(), buffer, bufferSize, bufferSampleCapacity);

                AVCodecLibraryUtil.alloc(packet);
                packet.data(null);
                packet.size(0);

                if (AVCodecLibraryUtil.encodeAudioFrame(cc, tmpFrame, packet)) {
                    //System.out.printf("encoding audio frame: pts = %d (pts_offset = %d, source_pts = %d)\n", pts, timestampGenerator.getOffset(), frame.getPts());
                    packet.stream_index(stream.index());
                    packet.pts(AVRationalUtils.longValue(AVRationalUtils.mul(ptsTransformBase, pts)));
                    packet.dts(packet.pts());
                    sendPacket(packet);
                    pts += frameDuration;
                    flushFramePts = pts;
                }
            }
        }
    }

    private int appendSamples(AVFrame frame, int frameOffset) {
        Pointer<Pointer<Byte>> data;
        if (planeCount > AVCodecLibraryUtil.AVCODEC_FRAME_DATA_LENGHT)
            data = frame.extended_data();
        else
            data = frame.data();

        int tmp = frameSize - offset;
        int lineSize = frame.linesize().get(0);
        int size = lineSize - frameOffset;

        if (size < tmp)
            tmp = size;

        Pointer<Byte> dataPlane;
        Pointer<Byte> plane;

        for (int i = 0; i < planeCount; i++) {
            dataPlane = data.get(i).offset(frameOffset);
            plane = planes.get(i).offset(offset);
            dataPlane.copyTo(plane, tmp);
        }

        offset += tmp;

        return tmp;
    }

    private void sendPacket(AVPacket packet) throws LibavException {
        synchronized (consumers) {
            for (IPacketConsumer c : consumers)
                c.processPacket(this, packet);
        }
    }

    @Override
    public void addPacketConsumer(IPacketConsumer c) {
        consumers.add(c);
    }

    @Override
    public void removePacketConsumer(IPacketConsumer c) {
        consumers.remove(c);
    }

}
