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
package com.mutar.libav;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bridj.IntValuedEnum;

import com.mutar.libav.api.IDecoder;
import com.mutar.libav.api.IEncoder;
import com.mutar.libav.api.IMediaDecoder;
import com.mutar.libav.api.IMediaEncoder;
import com.mutar.libav.api.IMediaReader;
import com.mutar.libav.api.IMediaWriter;
import com.mutar.libav.api.exception.LibavException;
import com.mutar.libav.api.util.AVCodecLibraryUtil;
import com.mutar.libav.bridge.avcodec.AVCodecContext;
import com.mutar.libav.bridge.avcodec.AvcodecLibrary.AVCodecID;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVPixelFormat;
import com.mutar.libav.video.FrameScaler;

public class TranscodeSample {

    public static void main(String[] args) {
        String srcUrl = "/home/sergey/Videos/test1.mp4"; // some source multimedia file/stream
        String dstUrl = "/home/sergey/bar.avi"; // destination file name
        String dstUrl2 = "/home/sergey/bar1.mkv"; // destination file name
        AVCodecID videoCodecId = AVCodecID.AV_CODEC_ID_MPEG4; // output video codec
        AVCodecID videoCodecId2 = AVCodecID.AV_CODEC_ID_H264; // output video codec
        AVCodecID audioCodecId = AVCodecID.AV_CODEC_ID_MP3; // output audio codec

        IMediaDecoder md = null;
        IMediaEncoder me = null;
        IMediaEncoder me2 = null;
        IMediaReader mr;
        IMediaWriter mw;
        IMediaWriter mw2;
        FrameScaler scaler = null;
        FrameScaler scaler2 = null;
        long startTime = System.currentTimeMillis();
        try {
            md = new DefaultMediaDecoder(srcUrl); // open input file/stream
            me = new DefaultMediaEncoder(dstUrl, null); // open output file
            me2 = new DefaultMediaEncoder(dstUrl2, null); // open output file
            mr = md.getMediaReader();
            mw = me.getMediaWriter();
            mw2 = me2.getMediaWriter();

            IDecoder dec;
            IEncoder enc,enc2;
            AVCodecContext cc1, cc2,cc3;
            int si, si2;

            // init video transcoding of the first video stream if there is at
            // least one video stream
            if (mr.getVideoStreamCount() > 0) {
                md.setVideoStreamDecodingEnabled(0, true);
                dec = md.getVideoStreamDecoder(0);
                cc1 = dec.getCodecContext();
                si = mw.addVideoStream(videoCodecId, cc1.width(), cc1.height());
                si2 = mw2.addVideoStream(videoCodecId2, cc1.width()/2, cc1.height()/2);
                enc = me.getVideoStreamEncoder(si);
                enc2 = me2.getVideoStreamEncoder(si2);
                cc2 = enc.getCodecContext();
                cc3 = enc2.getCodecContext();
                //cc2.pix_fmt(AVPixelFormat.AV_PIX_FMT_YUV420P);
                IntValuedEnum<AVPixelFormat> bestPixelFormat = AVCodecLibraryUtil.avcodecFindBestPixFmtOfList(cc2.codec().get().pix_fmts(), cc1.pix_fmt(), 0, null);
                IntValuedEnum<AVPixelFormat> bestPixelFormat2 = AVCodecLibraryUtil.avcodecFindBestPixFmtOfList(cc3.codec().get().pix_fmts(), cc1.pix_fmt(), 0, null);
                cc2.pix_fmt(bestPixelFormat);
                cc3.pix_fmt(bestPixelFormat2);
                scaler = new FrameScaler(cc1.width(), cc1.height(), cc1.pix_fmt(), cc2.width(), cc2.height(), cc2.pix_fmt());
                scaler2 = new FrameScaler(cc1.width(), cc1.height(), cc1.pix_fmt(), cc3.width(), cc3.height(), cc3.pix_fmt());
                scaler.addFrameConsumer(enc);
                scaler2.addFrameConsumer(enc2);
                dec.addFrameConsumer(scaler);
                dec.addFrameConsumer(scaler2);
            }

            // init audio transcoding of the first audio stream if there is at
            // least one audio stream
            /*if (mr.getAudioStreamCount() > 0) {
                md.setAudioStreamDecodingEnabled(0, true);
                dec = md.getAudioStreamDecoder(0);
                cc1 = dec.getCodecContext();
                si = mw.addAudioStream(audioCodecId, cc1.sample_rate(), cc1.sample_fmt(), cc1.channels());
                si2 = mw2.addAudioStream(audioCodecId, cc1.sample_rate(), cc1.sample_fmt(), cc1.channels());
                dec.addFrameConsumer(me.getAudioStreamEncoder(si));
                dec.addFrameConsumer(me2.getAudioStreamEncoder(si2));
            }*/

            mw.writeHeader(); // write file header
            mw2.writeHeader();
            boolean hasNext = true;
            while (hasNext) {
                try {
                    hasNext = mr.readNextPacket();
                } catch (LibavException ex) {
                    Logger.getLogger(TranscodeSample.class.getName()).log(Level.WARNING, "wrong packet", ex);
                }
            }
            md.flush();
            me.flush();
            me2.flush();
            mw.writeTrailer(); // write file trailer
            mw2.writeTrailer(); // write file trailer
        } catch (Exception ex) {
            Logger.getLogger(TranscodeSample.class.getName()).log(Level.SEVERE, "oooops", ex);
        } finally {
            try {
                if (md != null)
                    md.close();
                if (me != null)
                    me.close();
                if (me2 != null)
                    me2.close();
                if (scaler != null)
                    scaler.dispose();
                if (scaler2 != null)
                    scaler2.dispose();
            } catch (Exception ex) {
                Logger.getLogger(TranscodeSample.class.getName()).log(Level.SEVERE, "cannot close that", ex);
            }
        }
        System.out.println("total encoding time = " + (System.currentTimeMillis() - startTime)/1000);
    }


}
