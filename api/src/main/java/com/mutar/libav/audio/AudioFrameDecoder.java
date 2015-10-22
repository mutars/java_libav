package com.mutar.libav.audio;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bridj.Pointer;

import com.mutar.libav.api.IDecoder;
import com.mutar.libav.api.data.IFrameConsumer;
import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.api.util.AVCodecLibraryUtil;
import com.mutar.libav.api.util.AVRationalUtils;
import com.mutar.libav.api.util.AVUtilLibraryUtil;
import com.mutar.libav.bridge.avcodec.AVCodecContext;
import com.mutar.libav.bridge.avcodec.AVPacket;
import com.mutar.libav.bridge.avcodec.AvcodecLibrary;
import com.mutar.libav.bridge.avformat.AVStream;
import com.mutar.libav.bridge.avutil.AVFrame;
import com.mutar.libav.bridge.avutil.AVRational;
import com.mutar.libav.bridge.avutil.AvutilLibrary;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVMediaType;

public class AudioFrameDecoder implements IDecoder {

    private final AVStream stream;
    private final AVCodecContext cc;

    private AVRational sTimeBase;
    private long pts;

    private AVFrame audioFrame;
    private Pointer<Byte> sampleBuffer;
    private int sampleBufferSize;

    private final Set<IFrameConsumer> consumers;

    /**
     * Create a new audio frame decoder for the given audio stream.
     *
     * @param stream an audio stream
     * @throws LibavException if the decoder cannot be created for some reason
     * (caused by the Libav)
     */
    public AudioFrameDecoder(AVStream stream) throws LibavException {
        this.stream = stream;

        cc = stream.codec().get();
        if (cc.codec_type() != AVMediaType.AVMEDIA_TYPE_AUDIO)
            throw new IllegalArgumentException("not an audio stream");
        AVCodecLibraryUtil.open(cc, AVCodecLibraryUtil.findDecoder(cc.codec_id()));

        sTimeBase = AVRationalUtils.mul(stream.time_base(), 1000L);
        pts = 0;

        audioFrame = AVUtilLibraryUtil.allocateFrame();
        sampleBufferSize = AVCodecLibraryUtil.AVCODEC_MAX_AUDIO_FRAME_SIZE;
        sampleBuffer = AVUtilLibraryUtil.malloc(AVCodecLibraryUtil.AVCODEC_MAX_AUDIO_FRAME_SIZE + AvcodecLibrary.FF_INPUT_BUFFER_PADDING_SIZE).as(Byte.class);
        audioFrame.data().set(0, sampleBuffer);
        audioFrame.linesize().set(0, sampleBufferSize);

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
        if (audioFrame != null)
            AVUtilLibraryUtil.free(audioFrame);
        if (sampleBuffer != null)
            AVUtilLibraryUtil.av_free(sampleBuffer);
        AVCodecLibraryUtil.close(cc);

        audioFrame = null;
        sampleBuffer = null;
    }

    @Override
    public boolean isClosed() {
        return cc == null;
    }

    @Override
    public synchronized void processPacket(Object producer, AVPacket packet) throws LibavException {
        if (isClosed() || packet.stream_index() != stream.index())
            return;

        //System.out.printf("AP: dts = %d\n", sTimeBase.mul(packet.getDts()).longValue());
        Pointer<Byte> tmp = packet.data();
        while (packet.size() > 0) {
            audioFrame.linesize().set(0, sampleBufferSize);
            if (AVCodecLibraryUtil.decodeAudioFrame(cc, packet, audioFrame))
                sendFrame(transformPts(audioFrame));
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
        audioFrame.linesize().set(0, sampleBufferSize);
        if (AVCodecLibraryUtil.decodeAudioFrame(cc, packet, audioFrame))
            result = audioFrame;
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
        if (frame.pkt_dts() != AvutilLibrary.AV_NOPTS_VALUE)
            frame.pts(AVRationalUtils.longValue(AVRationalUtils.mul(sTimeBase, frame.pkt_dts())));
        else {
            frame.pts(pts);
            pts += frame.linesize().get(0) * 1000 / (cc.channels() * cc.sample_rate() * AVUtilLibraryUtil.getBytesPerSample(cc.sample_fmt()));
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
