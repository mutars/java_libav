package com.mutar.libav.api.util;

import java.nio.charset.Charset;

import org.bridj.IntValuedEnum;
import org.bridj.NativeObject;
import org.bridj.Pointer;

import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.bridge.avcodec.AVCodec;
import com.mutar.libav.bridge.avutil.AVFrame;
import com.mutar.libav.bridge.avutil.AvutilLibrary;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVPixelFormat;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVSampleFormat;
import com.mutar.libav.service.LibraryManager;


public final class AVUtilLibraryUtil {

    private static final AvutilLibrary utilLib;
    static {
        utilLib  = LibraryManager.getInstance().getAvUtil();
    }

    public static AVFrame allocateFrame() throws LibavException {
        Pointer<AVFrame> ptr = utilLib.av_frame_alloc();
        if (ptr == null)
            throw new LibavException("unable to allocate a new frame");

        return new AVFrame(ptr);
    }

    public static AVFrame allocatePicture(IntValuedEnum<AVPixelFormat> pixelFormat, int width, int height) throws LibavException {
        AVFrame result = allocateFrame();
        result.width(width);
        result.height(height);
        result.format((int)pixelFormat.value());

        Pointer<AVFrame> framePtr = getPointer(result);

        int err = utilLib.av_frame_get_buffer(framePtr, 32);
        if (err != 0)
            throw new LibavException(err);

        return result;
    }

    public static <T extends NativeObject> void av_free(T obj) {
        av_free(getPointer(obj));
    }

    public static <T> void av_free(Pointer<T> ptr) {
        utilLib.av_free(ptr);
    }

    public static void free(AVFrame frame) {
            if (frame == null)
                return;

            Pointer<Pointer<AVFrame>> tmp = Pointer.allocatePointer(AVFrame.class);
            tmp.set(getPointer(frame));
            utilLib.av_frame_free(tmp);
            frame = null;
    }

    public static void defaults(AVFrame frame) {
            if (frame == null)
                return;
            utilLib.av_frame_unref(getPointer(frame));
    }

    public static int getBytesPerSample(IntValuedEnum<AvutilLibrary.AVSampleFormat > sample_fmt) {
        return utilLib.av_get_bytes_per_sample(sample_fmt);
    }

    public static boolean isSigned(IntValuedEnum<AVSampleFormat > sample_fmt) {
        switch (sample_fmt.iterator().next()) {
            case AV_SAMPLE_FMT_DBL:
            case AV_SAMPLE_FMT_FLT:
            case AV_SAMPLE_FMT_S16:
            case AV_SAMPLE_FMT_S32:
            case AV_SAMPLE_FMT_DBLP :
            case AV_SAMPLE_FMT_FLTP:
            case AV_SAMPLE_FMT_S16P:
            case AV_SAMPLE_FMT_S32P: return true;
            default: return false;
        }
    }

    public static boolean isUnsigned(IntValuedEnum<AVSampleFormat > sample_fmt) {
        switch (sample_fmt.iterator().next()) {
            case AV_SAMPLE_FMT_U8:
            case AV_SAMPLE_FMT_U8P: return true;
            default: return false;
        }
    }

    public static boolean isReal(IntValuedEnum<AVSampleFormat > sample_fmt) {
        switch (sample_fmt.iterator().next()) {
            case AV_SAMPLE_FMT_DBL:
            case AV_SAMPLE_FMT_FLT:
            case AV_SAMPLE_FMT_DBLP:
            case AV_SAMPLE_FMT_FLTP: return true;
            default: return false;
        }
    }

    public static long getDefaultChannelLayout(int nb_channels) {
        return utilLib.av_get_default_channel_layout(nb_channels);
    }

    public static int getBitsPerSample(IntValuedEnum<AvutilLibrary.AVSampleFormat > sample_fmt) {
        return utilLib.av_get_bytes_per_sample(sample_fmt) * 8;
    }

    public static boolean isPlanar(IntValuedEnum<AVSampleFormat > sample_fmt) {
        return utilLib.av_sample_fmt_is_planar(sample_fmt) == 1;
    }

    public static Pointer<Byte> malloc(int size) {
        Pointer<Byte> ptr = utilLib.av_malloc(size).as(Byte.class);
        if (ptr == null)
            throw new OutOfMemoryError("not enough memory for the audio frame encoder");

        return ptr;
    }

    public static long getChannelLayout(String name) {
        Pointer<?> tmp = Pointer.pointerToString(name, Pointer.StringType.C, Charset.forName("UTF-8"));
        return utilLib.av_get_channel_layout(tmp.as(Byte.class));
    }

    public static int getChannelCount(long channelLayout) {
        return utilLib.av_get_channel_layout_nb_channels(channelLayout);
    }

    private static <T extends NativeObject> Pointer<T> getPointer(T obj) {
        return Pointer.getPointer(obj);
    }

    public  static <E extends Enum<E>> IntValuedEnum<E> getSupportedFormat(Pointer<IntValuedEnum<E>> sample_fmts, IntValuedEnum<E> sample_fmt) {
        for(int i=0;i<12; i++) {
            if(sample_fmts.get(i).value() == -1) break;
            if(sample_fmts.get(i).equals(sample_fmt)) return sample_fmt;
        }
        return sample_fmts.get(0);
    }

    public static int selectSampleRate(AVCodec codec, int sample_rate) {
        if (codec.supported_samplerates() == null) {
            return sample_rate;
        }
        int best_samplerate = 0;
        for (Integer sRate : codec.supported_samplerates()) {
            if (sRate == 0) break;
            if (sRate.equals(sample_rate) && sample_rate > 0) {
                return sample_rate;
            }
            best_samplerate = Math.max(sRate, best_samplerate);
        }
        return best_samplerate;
    }

}
