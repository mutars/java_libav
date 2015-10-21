package com.mutar.libav.api.util;

import java.nio.charset.Charset;

import org.bridj.Pointer;
import org.bridj.StructObject;

import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.bridge.avcodec.AVPacket;
import com.mutar.libav.bridge.avformat.AVFormatContext;
import com.mutar.libav.bridge.avformat.AVIOContext;
import com.mutar.libav.bridge.avformat.AVInputFormat;
import com.mutar.libav.bridge.avformat.AVOutputFormat;
import com.mutar.libav.bridge.avformat.AVStream;
import com.mutar.libav.bridge.avformat.AvformatLibrary;
import com.mutar.libav.bridge.avutil.AvutilLibrary;
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
    
    public static void play(AVFormatContext avfcx) {
        formatLib.av_read_play(getPointer(avfcx));
    }

    public static void pause(AVFormatContext avfcx) {
        formatLib.av_read_pause(getPointer(avfcx));
    }

    public static void writeHeader(AVFormatContext avfcx) throws LibavException {
        int result = formatLib.avformat_write_header(getPointer(avfcx), null);
        if (result < 0)
           throw new LibavException(result);
    }

    public static void writeTrailer(AVFormatContext avfcx) throws LibavException {
        int res = formatLib.av_write_trailer(getPointer(avfcx));
        if (res != 0)
            throw new LibavException(res);
    }

    public static void writePacket(AVFormatContext avfcx, AVPacket packet) throws LibavException {
        int res = formatLib.av_write_frame(getPointer(avfcx), getPointer(packet));
        if (res != 0)
            throw new LibavException(res);
    }

    public static void interleavedWritePacket(AVFormatContext avfcx, AVPacket packet) throws LibavException {
        int res = formatLib.av_interleaved_write_frame(getPointer(avfcx), getPointer(packet));
        if (res != 0)
            throw new LibavException(res);
    }

    public static void seekFile(AVFormatContext avfcx, long minTime, long time, long maxTime) throws LibavException {
        long tb = AvutilLibrary.AV_TIME_BASE / 1000;
        formatLib.avformat_seek_file(getPointer(avfcx), -1, minTime * tb, time * tb, maxTime * tb, 0);
    }
    
    public static AVFormatContext allocateContext() throws LibavException {
        Pointer<AVFormatContext> ptr = formatLib.avformat_alloc_context();
        if (ptr == null)
            throw new LibavException("unable to allocate a format context");
        
        return new AVFormatContext(ptr);
    }
    
    public static AVFormatContext openMedia(String url) throws LibavException {
        return openMedia(url, (AVInputFormat)null);
    }
    
    public static AVFormatContext openMedia(String url, String inputFormat) throws LibavException {
        AVInputFormat inpf = find(inputFormat);
        return openMedia(url, inpf);
    }
    
    public static AVFormatContext openMedia(String url, AVInputFormat inputFormat) throws LibavException {
        Pointer<Byte> purl = Pointer.pointerToString(url, Pointer.StringType.C, Charset.forName("UTF-8")).as(Byte.class);
        Pointer<AVInputFormat> pInputFormat = null;
        if (inputFormat != null)
            pInputFormat = getPointer(inputFormat);
        
        Pointer<Pointer<AVFormatContext>> avfcByRef = Pointer.allocatePointer(AVFormatContext.class);
        int result = formatLib.avformat_open_input(avfcByRef, purl, pInputFormat, null);
        if (result < 0)
            throw new LibavException(result);
        
        return new AVFormatContext(avfcByRef.get());
    }
    
    public static AVFormatContext createMedia(String url, String outputFormatName) throws LibavException {
        Pointer<Byte> purl = Pointer.pointerToString(url, Pointer.StringType.C, Charset.forName("UTF-8")).as(Byte.class);
        AVFormatContext result = allocateContext();

        AVOutputFormat of = guessFormat(outputFormatName, url, outputFormatName);
        if (of == null)
            throw new LibavException("unknown format: " + outputFormatName);
        result.oformat(getPointer(of));
        result.filename().setStringAtOffset(0, url, Pointer.StringType.C, Charset.forName("UTF-8"));
        
        Pointer<Pointer<AVIOContext>> avioc = Pointer.allocatePointer(AVIOContext.class);
        avioc.set(null);
        if ((result.oformat().get().flags() & AvformatLibrary.AVFMT_NOFILE) == 0) {
            result.pb(null);
            int res = formatLib.avio_open(avioc, purl, AvformatLibrary.AVIO_FLAG_WRITE);
            if (res < 0) {
                close(result, true);
                throw new LibavException(res);
            }
            result.pb(avioc.get() == null ? null : avioc.get());
        }
        
        return result;
    }
    
    public static AVInputFormat find(String shortName) {
        if (shortName == null)
            return null;
        
        Pointer<Byte> pShortName = Pointer.pointerToString(shortName, Pointer.StringType.C, Charset.forName("UTF-8")).as(Byte.class);
        Pointer<AVInputFormat> result = formatLib.av_find_input_format(pShortName);
        if (result == null)
            return null;
        
        return new AVInputFormat(result);
    }
    
    public static AVOutputFormat guessFormat(String shortName, String fileName, String mimeType) {
        Charset utf8 = Charset.forName("UTF-8");
        
        Pointer<Byte> pShortName = null;
        Pointer<Byte> pFileName = null;
        Pointer<Byte> pMimeType = null;
        
        if (shortName != null)
            pShortName = Pointer.pointerToString(shortName, Pointer.StringType.C, utf8).as(Byte.class);
        if (fileName != null)
            pFileName = Pointer.pointerToString(fileName, Pointer.StringType.C, utf8).as(Byte.class);
        if (mimeType != null)
            pMimeType = Pointer.pointerToString(mimeType, Pointer.StringType.C, utf8).as(Byte.class);
        
        Pointer<AVOutputFormat> result = formatLib.av_guess_format(pShortName, pFileName, pMimeType);
        if (result == null)
            return null;
        
        return new AVOutputFormat(result);
    }
    
    public static void close(AVFormatContext avfc , boolean outputContext) {
        if (outputContext) {
            if (avfc.pb() != null && (avfc.oformat().get().flags() & AvformatLibrary.AVFMT_NOFILE) == 0)
                formatLib.avio_close(avfc.pb());
            AVUtilLibraryUtil.av_free(avfc);
        } else {
            Pointer<Pointer<AVFormatContext>> ps = Pointer.allocatePointer(AVFormatContext.class);
            ps.set(getPointer(avfc));
            formatLib.avformat_close_input(ps);
        }
    }
    
    public static void findStreamInfo(AVFormatContext avfc) throws LibavException {
        int result = formatLib.avformat_find_stream_info(getPointer(avfc), null);
        if (result < 0)
            throw new LibavException(result);
    }

    public static AVStream[] getStreams(AVFormatContext avfc) {
    	int streamCount = getStreamCount(avfc);
        Pointer<Pointer<AVStream>> pStrms = avfc.streams();
        if (pStrms == null)
            return null;
        
        Pointer<AVStream> pStrm;
        AVStream[] streams = new AVStream[streamCount];
        for (int i = 0; i < streamCount; i++) {
            pStrm = pStrms.get(i);
            streams[i] = pStrm == null ? null : pStrm.get();
        }
        
        return streams;
    }

    public static int getStreamCount(AVFormatContext avfc) {
        return  avfc.nb_streams();
    }
    
    public static int sdpCreate(Pointer<Pointer<AVFormatContext > > ac, int n_files, Pointer<Byte > buf, int size) {
    	return formatLib.av_sdp_create(ac, n_files, buf, size);
    }
    
    public static AVStream newStream(AVFormatContext avfc) throws LibavException {
        Pointer<AVStream> pStream = formatLib.avformat_new_stream(getPointer(avfc), null);
        if (pStream == null)
            throw new LibavException("unable to create a new stream");
        return pStream.get();
    }

    private static <T extends StructObject> Pointer<T> getPointer(T obj) {
        return Pointer.getPointer(obj);
    }

}
