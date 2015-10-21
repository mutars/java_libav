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

import org.bridj.BridJ;
import org.bridj.Pointer;

import com.mutar.libav.api.IEncoder;
import com.mutar.libav.api.ITimestampGenerator;
import com.mutar.libav.api.data.IPacketConsumer;
import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.api.time.CopyTimestampGenerator;
import com.mutar.libav.api.util.AVCodecLibraryUtil;
import com.mutar.libav.api.util.AVRationalUtils;
import com.mutar.libav.bridge.avcodec.AVCodecContext;
import com.mutar.libav.bridge.avcodec.AVPacket;
import com.mutar.libav.bridge.avcodec.AVPicture;
import com.mutar.libav.bridge.avcodec.AvcodecLibrary;
import com.mutar.libav.bridge.avformat.AVFormatContext;
import com.mutar.libav.bridge.avformat.AVStream;
import com.mutar.libav.bridge.avformat.AvformatLibrary;
import com.mutar.libav.bridge.avutil.AVFrame;
import com.mutar.libav.bridge.avutil.AVRational;
import com.mutar.libav.bridge.avutil.AvutilLibrary;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVMediaType;

/**
 * Video frame encoder.
 * 
 * @author Ondrej Perutka
 */
public class VideoFrameEncoder implements IEncoder {
    
    private final AVStream stream;
    private final AVCodecContext cc;
    private boolean initialized;
    
    private boolean rawFormat;
    
    private AVPacket packet;
    private AVRational tsToCodecBase;
    private AVRational tsToStreamBase;
    private ITimestampGenerator timestampGenerator;
    
    private final Set<IPacketConsumer> consumers;
    
    /**
     * Create a new video frame encoder for the given video stream.
     * 
     * @param formatContext a format context
     * @param stream a video stream
     * @throws LibavException if the frame encoder cannot be crated (caused
     * by the Libav)
     */
    public VideoFrameEncoder(AVFormatContext formatContext, AVStream stream) throws LibavException {
        this.stream = stream;
        
        cc = stream.codec().get();
        if (cc.codec_type() != AVMediaType.AVMEDIA_TYPE_VIDEO)
            throw new IllegalArgumentException("not a video stream");

        
        initialized = false;
        
        rawFormat = (formatContext.oformat().get().flags() & AvformatLibrary.AVFMT_RAWPICTURE) != 0;
        
        packet = AVCodecLibraryUtil.alloc_packet();
        tsToCodecBase = null;
        tsToStreamBase = null;
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
        packet = null;
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
        
        AVPacket p;
        long pts;
        
        while ((pts = timestampGenerator.nextFrame(frame.pts())) >= 0) {
            p = encodeFrame(frame, pts);
            if (p != null)
                sendPacket(p);
        }
    }
    
    @Override
    public synchronized void flush() throws LibavException {
        if (isClosed())
            return;
        
        initEncoder();
        
        AVPacket p;
        while ((p = encodeFrame(null, timestampGenerator.getLastTimestamp())) != null)
            sendPacket(p);
    }
    
    private void initEncoder() throws LibavException {
        if (initialized)
            return;

        tsToCodecBase = AVRationalUtils.invert(AVRationalUtils.mul(cc.time_base(), 1000)); 
        
        // propper time base is set after avformat_write_header() call
        tsToStreamBase =AVRationalUtils.div(cc.time_base(), stream.time_base()); 
        initialized = true;
    }
    
    private AVPacket encodeFrame(AVFrame frame, long pts) throws LibavException {
    	AVCodecLibraryUtil.alloc(packet);
        
        if (rawFormat) {
            if (frame == null)
                return null;
            packet.flags(packet.flags() | AvcodecLibrary.AV_PKT_FLAG_KEY);
            packet.data(Pointer.getPointer(frame).as(Byte.class));
            packet.size((int)BridJ.sizeOf(AVPicture.class));
            packet.stream_index(stream.index());
        } else {
            packet.data(null);
            packet.size(0);

            boolean gotPacket;
            if (frame == null)
                gotPacket = AVCodecLibraryUtil.encodeVideoFrame(cc, null, packet);
            else {
                long oldPts = frame.pts();
                frame.pts(AVRationalUtils.longValue(AVRationalUtils.mul(tsToCodecBase, pts)));
                gotPacket = AVCodecLibraryUtil.encodeVideoFrame(cc, frame, packet);
                frame.pts(oldPts);
            }
            
            if (!gotPacket)
                return null;
            
            packet.stream_index(stream.index());
            AVFrame codedFrame = new AVFrame(cc.coded_frame());
            if (codedFrame.key_frame() == 1)
                packet.flags(packet.flags() | AvcodecLibrary.AV_PKT_FLAG_KEY);
            //System.out.printf("encoding video frame: pts = %d (pts_offset = %d, source_pts = %d)\n", pts, timestampGenerator.getOffset(), frame.getPts());
            if (packet.pts() != AvutilLibrary.AV_NOPTS_VALUE)
                packet.pts(AVRationalUtils.longValue(AVRationalUtils.mul(tsToStreamBase, packet.pts())));
            if (packet.dts() != AvutilLibrary.AV_NOPTS_VALUE)
                packet.dts(AVRationalUtils.longValue(AVRationalUtils.mul(tsToStreamBase, packet.dts())));
        }
        
        return packet;
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
