package com.mutar.libav.service;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bridj.BridJ;

import com.mutar.libav.bridge.avcodec.AvcodecLibrary;
import com.mutar.libav.bridge.avdevice.AvdeviceLibrary;
import com.mutar.libav.bridge.avformat.AvformatLibrary;
import com.mutar.libav.bridge.avresample.AvresampleLibrary;
import com.mutar.libav.bridge.avutil.AvutilLibrary;
import com.mutar.libav.bridge.swscale.SwscaleLibrary;

/**
 * This singleton class loads the Libav dynamic libraries and holds refrences
 * to them.
 *
 * It searches for the native libraries in default system locations and inside
 * a folder specified by the "org.libav.libpath" system property. Default value
 * of this property is "./libav".
 *
 * @author Ondrej Perutka
 */
public class LibraryManager {

    private static final String PKEY_LIBPATH = "org.libav.libpath";
    private static final String DEFAULT_LIBPATH = "/home/sergey/ffmpeg_build/lib";

    private static LibraryManager instance = null;

    private final AvutilLibrary avUtil;
    private final AvcodecLibrary avCodec;
    private final AvformatLibrary avFormat;
    private final AvdeviceLibrary avDevice;
    private final SwscaleLibrary swScale;
    private final AvresampleLibrary avResample;

    private LibraryManager() throws IOException {
        BridJ.addLibraryPath(System.getProperty(PKEY_LIBPATH, DEFAULT_LIBPATH));

        /*addNativeLibraryAliases(AVCodecLibrary.LIB_NAME, AVCodecLibrary.MIN_MAJOR_VERSION, AVCodecLibrary.MAX_MAJOR_VERSION);
        addNativeLibraryAliases(AVFormatLibrary.LIB_NAME, AVFormatLibrary.MIN_MAJOR_VERSION, AVFormatLibrary.MAX_MAJOR_VERSION);
        addNativeLibraryAliases(AVUtilLibrary.LIB_NAME, AVUtilLibrary.MIN_MAJOR_VERSION, AVUtilLibrary.MAX_MAJOR_VERSION);
        addNativeLibraryAliases(AVDeviceLibrary.LIB_NAME, AVDeviceLibrary.MIN_MAJOR_VERSION, AVDeviceLibrary.MAX_MAJOR_VERSION);
        addNativeLibraryAliases(SWScaleLibrary.LIB_NAME, SWScaleLibrary.MIN_MAJOR_VERSION, SWScaleLibrary.MAX_MAJOR_VERSION);
        addNativeLibraryAliases(AVResampleLibrary.LIB_NAME, AVResampleLibrary.MIN_MAJOR_VERSION, AVResampleLibrary.MAX_MAJOR_VERSION);*/

        avUtil = new AvutilLibrary();
        avCodec = new AvcodecLibrary();
        avFormat = new AvformatLibrary();
        swScale = new SwscaleLibrary();
        avDevice = new AvdeviceLibrary();
        avResample = new AvresampleLibrary();

        avFormat.av_register_all();
        avFormat.avformat_network_init();
        avCodec.avcodec_register_all();
        avDevice.avdevice_register_all();
    }

    /**
     * Add aliases for the given native library name. (It is required on some
     * platforms.)
     *
     * @param libName standard library name
     * @param minMajorVersio min major version
     * @param maxMajorVersion max major version
     */
    /*private void addNativeLibraryAliases(String libName, int minMajorVersio, int maxMajorVersion) {
        BridJ.addNativeLibraryAlias(libName, libName);
        for (int i = minMajorVersio; i <= maxMajorVersion; i++)
            BridJ.addNativeLibraryAlias(libName, libName + "-" + i);
    } */



    /**
     * Return instance of the LibraryManager.
     *
     * @return instance of the LibraryManager
     */
    public static LibraryManager getInstance() {
        try {
            if (instance == null)
                instance = new LibraryManager();
        } catch (IOException ex) {
            Logger.getLogger(LibraryManager.class.getName()).log(Level.SEVERE, "unable to load native libraries", ex);
        }

        return instance;
    }

    public AvutilLibrary getAvUtil() {
        return avUtil;
    }

    public AvcodecLibrary getAvCodec() {
        return avCodec;
    }

    public AvformatLibrary getAvFormat() {
        return avFormat;
    }

    public AvdeviceLibrary getAvDevice() {
        return avDevice;
    }

    public SwscaleLibrary getSwScale() {
        return swScale;
    }

    public AvresampleLibrary getAvResample() {
        return avResample;
    }

}
