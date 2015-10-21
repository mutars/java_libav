package com.mutar.libav.api.util;

import org.bridj.IntValuedEnum;
import org.bridj.Pointer;
import org.bridj.StructObject;

import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.bridge.avcodec.AVCodec;
import com.mutar.libav.bridge.avcodec.AVCodecContext;
import com.mutar.libav.bridge.avcodec.AVPacket;
import com.mutar.libav.bridge.avcodec.AvcodecLibrary;
import com.mutar.libav.bridge.avcodec.AvcodecLibrary.AVCodecID;
import com.mutar.libav.bridge.avutil.AVFrame;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVSampleFormat;
import com.mutar.libav.service.LibraryManager;


public final class AVCodecLibraryUtil {

    private static final AvcodecLibrary avCodecLib;
    private final static Pointer<Integer> intByRef;
    static {
        avCodecLib  = LibraryManager.getInstance().getAvCodec();
        intByRef = Pointer.allocateInt();
    }

    public static void free(AVPacket avpt) {
        avCodecLib.av_free_packet(getPointer(avpt));
    }

    public static void open(AVCodecContext avctx, AVCodec codec) throws LibavException {

        int result = avCodecLib.avcodec_open2(getPointer(avctx), getPointer(codec), null);
        if(result < 0)
            throw new LibavException(result);
    }

    public static void  alloc(AVPacket avp) {
        avCodecLib.av_init_packet(getPointer(avp));
    }

    public static AVPacket alloc_packet() {
        AVPacket avp = new AVPacket();
        avCodecLib.av_init_packet(getPointer(avp));
        return avp;
    }

    public static AVCodec findDecoder(IntValuedEnum<AVCodecID> codecId) throws LibavException {
        Pointer<AVCodec> ptr = avCodecLib.avcodec_find_decoder(codecId);
        if (ptr == null)
            throw new LibavException("unable to find decoder");

        return new AVCodec(ptr);
    }

    public static AVCodec findEncoder(IntValuedEnum<AVCodecID> codecId) throws LibavException {
        Pointer<AVCodec> ptr = avCodecLib.avcodec_find_encoder(codecId);
        if (ptr == null)
            throw new LibavException("unable to find encoder");

        return new AVCodec(ptr);
    }

    public static AVCodec findDecoderByName(String name) throws LibavException {
        Pointer<Byte> nm = Pointer.pointerToCString(name);
        Pointer<AVCodec> ptr = avCodecLib.avcodec_find_decoder_by_name(nm);
        if (ptr == null)
            throw new LibavException("unable to find decoder");

        return new AVCodec(ptr);
    }

    public static AVCodec findEncoderByName(String name) throws LibavException {
        Pointer<Byte> nm = Pointer.pointerToCString(name);
        Pointer<AVCodec> ptr = avCodecLib.avcodec_find_encoder_by_name(nm);
        if (ptr == null)
            throw new LibavException("unable to find encoder");
        return new AVCodec(ptr);
    }

    public static void close(AVCodecContext avctx) {
        avCodecLib.avcodec_close(getPointer(avctx));
    }

    public static boolean decodeVideoFrame(AVCodecContext avctx, AVPacket packet, AVFrame frame) throws LibavException {
        intByRef.setInt(0);

        int packetSize = packet.size();
        int len = avCodecLib.avcodec_decode_video2(getPointer(avctx), getPointer(frame), intByRef, getPointer(packet));
        if (len < 0)
            throw new LibavException(len);

        packetSize -= len;
        packet.size(packetSize);
        packet.data(packetSize <= 0 ? null : packet.data().offset(len));
        if (intByRef.getInt() != 0) {
            return true;
        }

        return false;
    }

    public static boolean encodeVideoFrame(AVCodecContext avctx, AVFrame frame, AVPacket packet) throws LibavException {
        intByRef.setInt(0);

        int len = avCodecLib.avcodec_encode_video2(getPointer(avctx), getPointer(packet), frame == null ? null : getPointer(frame), intByRef);
        if (len < 0)
            throw new LibavException(len);

        return intByRef.getInt() != 0;
    }

    public static boolean decodeAudioFrame(AVCodecContext avctx, AVPacket packet, AVFrame frame) throws LibavException {
        intByRef.setInt(0);

        AVUtilLibraryUtil.defaults(frame);
        int packetSize = packet.size();
        int len = avCodecLib.avcodec_decode_audio4(getPointer(avctx), getPointer(frame), intByRef, getPointer(packet));
        if (len < 0)
            throw new LibavException(len);

        packetSize -= len;
        packet.size(packetSize);
        packet.data(packetSize <= 0 ? null : packet.data().offset(len));
        if (intByRef.getInt() != 0) {
             IntValuedEnum<AVSampleFormat> sf = avctx.sample_fmt();
            int lineSize = frame.nb_samples() * AVUtilLibraryUtil.getBytesPerSample(sf);
            if (!AVUtilLibraryUtil.isPlanar(sf))
                lineSize *= avctx.channels();
            frame.linesize().set(0, lineSize);
            return true;
        }

        return false;
    }

    public boolean encodeAudioFrame(AVCodecContext avctx, AVFrame frame, AVPacket packet) throws LibavException {
        intByRef.setInt(0);

        int len = avCodecLib.avcodec_encode_audio2(getPointer(avctx), getPointer(packet), frame == null ? null : getPointer(frame), intByRef);
        if (len < 0)
            throw new LibavException(len);

        return intByRef.getInt() != 0;
    }


    private static <T extends StructObject> Pointer<T> getPointer(T obj) {
        return Pointer.getPointer(obj);
    }

}
