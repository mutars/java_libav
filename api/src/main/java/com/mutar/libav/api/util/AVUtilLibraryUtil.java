package com.mutar.libav.api.util;

import org.bridj.Pointer;
import org.bridj.StructObject;

import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.bridge.avutil.AVFrame;
import com.mutar.libav.bridge.avutil.AvutilLibrary;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVPixelFormat;
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

    public static void free(AVFrame frame) {
            if (frame == null)
                return;

            Pointer<Pointer<AVFrame>> tmp = Pointer.allocatePointer(AVFrame.class);
            tmp.set(getPointer(frame));
            utilLib.av_frame_free(tmp);
            frame = null;
    }

    private static <T extends StructObject> Pointer<T> getPointer(T obj) {
        return Pointer.getPointer(obj);
    }


}
