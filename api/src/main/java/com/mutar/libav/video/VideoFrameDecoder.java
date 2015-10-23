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
package com.mutar.libav.video;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bridj.Pointer;

import com.mutar.libav.api.IDecoder;
import com.mutar.libav.api.data.IFrameConsumer;
import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.api.util.AVCodecLibraryUtil;
import com.mutar.libav.api.util.AVUtilLibraryUtil;
import com.mutar.libav.api.util.Rational;
import com.mutar.libav.bridge.avcodec.AVCodecContext;
import com.mutar.libav.bridge.avcodec.AVPacket;
import com.mutar.libav.bridge.avformat.AVStream;
import com.mutar.libav.bridge.avutil.AVFrame;
import com.mutar.libav.bridge.avutil.AvutilLibrary;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVMediaType;

/**
 * Video frame decoder.
 *
 * @author Ondrej Perutka
 */
public class VideoFrameDecoder implements IDecoder {

    private final AVStream stream;
    private final AVCodecContext cc;

    private Rational sTimeBase;
    private long pts;
    private long frameDuration;

    private AVFrame frame;

    private final Set<IFrameConsumer> consumers;

    /**
     * Create a new video frame decoder for the given video stream.
     *
     * @param stream a video stream
     * @throws LibavException if the decoder cannot be created for some reason
     * (caused by the Libav)
     */
    public VideoFrameDecoder(AVStream stream) throws LibavException {
        this.stream = stream;

        cc = stream.codec().get();
        if (cc.codec_type() != AVMediaType.AVMEDIA_TYPE_VIDEO)
            throw new IllegalArgumentException("not a video stream");

        AVCodecLibraryUtil.open(cc, AVCodecLibraryUtil.findDecoder(cc.codec_id()));

        sTimeBase = new Rational(stream.time_base()).mul(1000L);
        pts = 0;

        frameDuration =  new Rational(cc.time_base()).mul(1000).longValue();

        frame = AVUtilLibraryUtil.allocateFrame();

        consumers = Collections.synchronizedSet(new HashSet<IFrameConsumer>());
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
    public synchronized void close() {
        if (frame != null)
            AVUtilLibraryUtil.free(frame);
        AVCodecLibraryUtil.close(cc);
    }

    @Override
    public boolean isClosed() {
        return cc == null;
    }

    @Override
    public synchronized void processPacket(Object producer, AVPacket packet) throws LibavException {
        if (isClosed() || packet.stream_index() != stream.index())
            return;

        //System.out.printf("VP: dts = %d\n", sTimeBase.mul(packet.getDts()).longValue());
        Pointer<Byte> tmp = packet.data();
        while (packet.size() > 0) {
            if (AVCodecLibraryUtil.decodeVideoFrame(cc, packet, frame))
                sendFrame(transformPts(frame));
        }
        packet.data(tmp);
    }

    @Override
    public synchronized void flush() throws LibavException {
        AVFrame fr;
        while ((fr = flushContext()) != null)
            sendFrame(transformPts(fr));
    }

    private synchronized AVFrame flushContext() throws LibavException {
        if (isClosed())
            return null;

        AVPacket packet = AVCodecLibraryUtil.alloc_packet();
        AVFrame result = null;

        packet.size(0);
        packet.data(null);
        if (AVCodecLibraryUtil.decodeVideoFrame(cc, packet, frame))
            result = frame;
        AVCodecLibraryUtil.free(packet);

        return result;
    }


    protected void sendFrame(AVFrame frame) throws LibavException {
        synchronized (consumers) {
            for (IFrameConsumer c : consumers)
                c.processFrame(this, frame);
        }
    }

    private AVFrame transformPts(AVFrame frame) {
        //System.out.printf("decoded frame: pts = %d, packet_pts = %d, packet_dts = %d, sTimeBase = %s\n", frame.getPts(), frame.getPacketPts(), frame.getPacketDts(), sTimeBase.toString());
        if (frame.pkt_dts() != AvutilLibrary.AV_NOPTS_VALUE) {
        	frame.pts(sTimeBase.mul(frame.pkt_dts()).longValue());
            //frame.pts(AVRationalUtils.longValue(AVRationalUtils.mul(sTimeBase, frame.pkt_dts())));
        } else {
            frame.pts(pts);
            pts += frameDuration;
        }

        return frame;
    }

    @Override
    public void addFrameConsumer(IFrameConsumer c) {
        consumers.add(c);
    }

    @Override
    public void removeFrameConsumer(IFrameConsumer c) {
        consumers.remove(c);
    }

}
