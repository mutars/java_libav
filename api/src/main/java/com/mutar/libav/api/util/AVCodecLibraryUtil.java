package com.mutar.libav.api.util;

import org.bridj.Pointer;
import org.bridj.StructObject;

import com.mutar.libav.bridge.avcodec.AVPacket;
import com.mutar.libav.bridge.avcodec.AvcodecLibrary;
import com.mutar.libav.service.LibraryManager;


public final class AVCodecLibraryUtil {

    private static final AvcodecLibrary avCodecLib;
    static {
        avCodecLib  = LibraryManager.getInstance().getAvCodec();
    }

    public static void free(AVPacket avpt) {
        avCodecLib.av_free_packet(getPointer(avpt));
    }

    private static <T extends StructObject> Pointer<T> getPointer(T obj) {
        return Pointer.getPointer(obj);
    }

}
