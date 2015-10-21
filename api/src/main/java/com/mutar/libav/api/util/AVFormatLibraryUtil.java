package com.mutar.libav.api.util;

import org.bridj.Pointer;
import org.bridj.StructObject;

import com.mutar.libav.bridge.avcodec.AVPacket;
import com.mutar.libav.bridge.avformat.AVFormatContext;
import com.mutar.libav.bridge.avformat.AvformatLibrary;
import com.mutar.libav.service.LibraryManager;


public final class AVFormatLibraryUtil {

    private static final AvformatLibrary formatLib;
    static {
        formatLib  = LibraryManager.getInstance().getAvFormat();
    }

    public static boolean  readNextPacket(AVFormatContext avfcx, AVPacket avPacket) {
        int result = formatLib.av_read_frame(getPointer(avfcx), getPointer(avPacket));
        return result >= 0;
    }

    private static <T extends StructObject> Pointer<T> getPointer(T obj) {
        return Pointer.getPointer(obj);
    }

}
