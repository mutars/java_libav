package com.mutar.libav.api.util;

import org.bridj.IntValuedEnum;
import org.bridj.NativeObject;
import org.bridj.Pointer;

import com.mutar.libav.api.exception.LibavException;
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

    public static AVFrame allocatePicture(AVPixelFormat pixelFormat, int width, int height) throws LibavException {
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
    
    public static <T extends NativeObject> void av_free(Pointer<T> ptr) {
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
        switch (sample_fmt.toString()) {
            case "AV_SAMPLE_FMT_DBL":
            case "AV_SAMPLE_FMT_FLT":
            case "AV_SAMPLE_FMT_S16":
            case "AV_SAMPLE_FMT_S32":
            case "AV_SAMPLE_FMT_DBLP" :
            case "AV_SAMPLE_FMT_FLTP":
            case "AV_SAMPLE_FMT_S16P":
            case "AV_SAMPLE_FMT_S32P": return true;
            default: return false;
        }
    }

    public static boolean isUnsigned(IntValuedEnum<AVSampleFormat > sample_fmt) {
        switch (sample_fmt.toString()) {
            case "AV_SAMPLE_FMT_U8":
            case "AV_SAMPLE_FMT_U8P": return true;
            default: return false;
        }
    }

    public static boolean isReal(IntValuedEnum<AVSampleFormat > sample_fmt) {
        switch (sample_fmt.toString()) {
            case "AV_SAMPLE_FMT_DBL":
            case "AV_SAMPLE_FMT_FLT":
            case "AV_SAMPLE_FMT_DBLP":
            case "AV_SAMPLE_FMT_FLTP": return true;
            default: return false;
        }
    }
    
    public static long getDefaultChannelLayout(int nb_channels) {
    	return utilLib.av_get_default_channel_layout(nb_channels);
    }

    public static boolean isPlanar(IntValuedEnum<AVSampleFormat > sample_fmt) {
        return utilLib.av_sample_fmt_is_planar(sample_fmt) == 1;
    }

    private static <T extends NativeObject> Pointer<T> getPointer(T obj) {
        return Pointer.getPointer(obj);
    }


}
