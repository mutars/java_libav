package com.mutar.libav.bridge.avcodec;
import com.mutar.libav.bridge.avcodec.AvcodecLibrary.AVCodecDefault;
import com.mutar.libav.bridge.avcodec.AvcodecLibrary.AVCodecID;
import com.mutar.libav.bridge.avutil.AVClass;
import com.mutar.libav.bridge.avutil.AVFrame;
import com.mutar.libav.bridge.avutil.AVRational;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVMediaType;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVPixelFormat;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVSampleFormat;
import org.bridj.BridJ;
import org.bridj.Callback;
import org.bridj.IntValuedEnum;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
import org.bridj.ann.Ptr;
/**
 * <i>native declaration : ffmpeg_build/include/libavcodec/avcodec.h:955</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("avcodec") 
public class AVCodec extends StructObject {
	static {
		BridJ.register();
	}
	/** C type : const char* */
	@Field(0) 
	public Pointer<Byte > name() {
		return this.io.getPointerField(this, 0);
	}
	/** C type : const char* */
	@Field(0) 
	public AVCodec name(Pointer<Byte > name) {
		this.io.setPointerField(this, 0, name);
		return this;
	}
	/** C type : const char* */
	@Field(1) 
	public Pointer<Byte > long_name() {
		return this.io.getPointerField(this, 1);
	}
	/** C type : const char* */
	@Field(1) 
	public AVCodec long_name(Pointer<Byte > long_name) {
		this.io.setPointerField(this, 1, long_name);
		return this;
	}
	/** C type : AVMediaType */
	@Field(2) 
	public IntValuedEnum<AVMediaType > type() {
		return this.io.getEnumField(this, 2);
	}
	/** C type : AVMediaType */
	@Field(2) 
	public AVCodec type(IntValuedEnum<AVMediaType > type) {
		this.io.setEnumField(this, 2, type);
		return this;
	}
	/** C type : AVCodecID */
	@Field(3) 
	public IntValuedEnum<AVCodecID > id() {
		return this.io.getEnumField(this, 3);
	}
	/** C type : AVCodecID */
	@Field(3) 
	public AVCodec id(IntValuedEnum<AVCodecID > id) {
		this.io.setEnumField(this, 3, id);
		return this;
	}
	@Field(4) 
	public int capabilities() {
		return this.io.getIntField(this, 4);
	}
	@Field(4) 
	public AVCodec capabilities(int capabilities) {
		this.io.setIntField(this, 4, capabilities);
		return this;
	}
	/**
	 * < array of supported framerates, or NULL if any, array is terminated by {0,0}<br>
	 * C type : const AVRational*
	 */
	@Field(5) 
	public Pointer<AVRational > supported_framerates() {
		return this.io.getPointerField(this, 5);
	}
	/**
	 * < array of supported framerates, or NULL if any, array is terminated by {0,0}<br>
	 * C type : const AVRational*
	 */
	@Field(5) 
	public AVCodec supported_framerates(Pointer<AVRational > supported_framerates) {
		this.io.setPointerField(this, 5, supported_framerates);
		return this;
	}
	/**
	 * < array of supported pixel formats, or NULL if unknown, array is terminated by -1<br>
	 * C type : AVPixelFormat*
	 */
	@Field(6) 
	public Pointer<IntValuedEnum<AVPixelFormat > > pix_fmts() {
		return this.io.getPointerField(this, 6);
	}
	/**
	 * < array of supported pixel formats, or NULL if unknown, array is terminated by -1<br>
	 * C type : AVPixelFormat*
	 */
	@Field(6) 
	public AVCodec pix_fmts(Pointer<IntValuedEnum<AVPixelFormat > > pix_fmts) {
		this.io.setPointerField(this, 6, pix_fmts);
		return this;
	}
	/**
	 * < array of supported audio samplerates, or NULL if unknown, array is terminated by 0<br>
	 * C type : const int*
	 */
	@Field(7) 
	public Pointer<Integer > supported_samplerates() {
		return this.io.getPointerField(this, 7);
	}
	/**
	 * < array of supported audio samplerates, or NULL if unknown, array is terminated by 0<br>
	 * C type : const int*
	 */
	@Field(7) 
	public AVCodec supported_samplerates(Pointer<Integer > supported_samplerates) {
		this.io.setPointerField(this, 7, supported_samplerates);
		return this;
	}
	/**
	 * < array of supported sample formats, or NULL if unknown, array is terminated by -1<br>
	 * C type : AVSampleFormat*
	 */
	@Field(8) 
	public Pointer<IntValuedEnum<AVSampleFormat > > sample_fmts() {
		return this.io.getPointerField(this, 8);
	}
	/**
	 * < array of supported sample formats, or NULL if unknown, array is terminated by -1<br>
	 * C type : AVSampleFormat*
	 */
	@Field(8) 
	public AVCodec sample_fmts(Pointer<IntValuedEnum<AVSampleFormat > > sample_fmts) {
		this.io.setPointerField(this, 8, sample_fmts);
		return this;
	}
	/**
	 * < array of support channel layouts, or NULL if unknown. array is terminated by 0<br>
	 * C type : const uint64_t*
	 */
	@Field(9) 
	public Pointer<Long > channel_layouts() {
		return this.io.getPointerField(this, 9);
	}
	/**
	 * < array of support channel layouts, or NULL if unknown. array is terminated by 0<br>
	 * C type : const uint64_t*
	 */
	@Field(9) 
	public AVCodec channel_layouts(Pointer<Long > channel_layouts) {
		this.io.setPointerField(this, 9, channel_layouts);
		return this;
	}
	/** < maximum value for lowres supported by the decoder, no direct access, use av_codec_get_max_lowres() */
	@Field(10) 
	public byte max_lowres() {
		return this.io.getByteField(this, 10);
	}
	/** < maximum value for lowres supported by the decoder, no direct access, use av_codec_get_max_lowres() */
	@Field(10) 
	public AVCodec max_lowres(byte max_lowres) {
		this.io.setByteField(this, 10, max_lowres);
		return this;
	}
	/**
	 * < AVClass for the private context<br>
	 * C type : const AVClass*
	 */
	@Field(11) 
	public Pointer<AVClass > priv_class() {
		return this.io.getPointerField(this, 11);
	}
	/**
	 * < AVClass for the private context<br>
	 * C type : const AVClass*
	 */
	@Field(11) 
	public AVCodec priv_class(Pointer<AVClass > priv_class) {
		this.io.setPointerField(this, 11, priv_class);
		return this;
	}
	/**
	 * < array of recognized profiles, or NULL if unknown, array is terminated by {FF_PROFILE_UNKNOWN}<br>
	 * C type : const AVProfile*
	 */
	@Field(12) 
	public Pointer<AVProfile > profiles() {
		return this.io.getPointerField(this, 12);
	}
	/**
	 * < array of recognized profiles, or NULL if unknown, array is terminated by {FF_PROFILE_UNKNOWN}<br>
	 * C type : const AVProfile*
	 */
	@Field(12) 
	public AVCodec profiles(Pointer<AVProfile > profiles) {
		this.io.setPointerField(this, 12, profiles);
		return this;
	}
	@Field(13) 
	public int priv_data_size() {
		return this.io.getIntField(this, 13);
	}
	@Field(13) 
	public AVCodec priv_data_size(int priv_data_size) {
		this.io.setIntField(this, 13, priv_data_size);
		return this;
	}
	/** C type : AVCodec* */
	@Field(14) 
	public Pointer<AVCodec > next() {
		return this.io.getPointerField(this, 14);
	}
	/** C type : AVCodec* */
	@Field(14) 
	public AVCodec next(Pointer<AVCodec > next) {
		this.io.setPointerField(this, 14, next);
		return this;
	}
	/** C type : init_thread_copy_callback* */
	@Field(15) 
	public Pointer<AVCodec.init_thread_copy_callback > init_thread_copy() {
		return this.io.getPointerField(this, 15);
	}
	/** C type : init_thread_copy_callback* */
	@Field(15) 
	public AVCodec init_thread_copy(Pointer<AVCodec.init_thread_copy_callback > init_thread_copy) {
		this.io.setPointerField(this, 15, init_thread_copy);
		return this;
	}
	/** C type : update_thread_context_callback* */
	@Field(16) 
	public Pointer<AVCodec.update_thread_context_callback > update_thread_context() {
		return this.io.getPointerField(this, 16);
	}
	/** C type : update_thread_context_callback* */
	@Field(16) 
	public AVCodec update_thread_context(Pointer<AVCodec.update_thread_context_callback > update_thread_context) {
		this.io.setPointerField(this, 16, update_thread_context);
		return this;
	}
	/** C type : const AVCodecDefault* */
	@Field(17) 
	public Pointer<AVCodecDefault > defaults() {
		return this.io.getPointerField(this, 17);
	}
	/** C type : const AVCodecDefault* */
	@Field(17) 
	public AVCodec defaults(Pointer<AVCodecDefault > defaults) {
		this.io.setPointerField(this, 17, defaults);
		return this;
	}
	/** C type : init_static_data_callback* */
	@Field(18) 
	public Pointer<AVCodec.init_static_data_callback > init_static_data() {
		return this.io.getPointerField(this, 18);
	}
	/** C type : init_static_data_callback* */
	@Field(18) 
	public AVCodec init_static_data(Pointer<AVCodec.init_static_data_callback > init_static_data) {
		this.io.setPointerField(this, 18, init_static_data);
		return this;
	}
	/** C type : init_callback* */
	@Field(19) 
	public Pointer<com.mutar.libav.bridge.avfilter.AVFilter.init_callback > init() {
		return this.io.getPointerField(this, 19);
	}
	/** C type : init_callback* */
	@Field(19) 
	public AVCodec init(Pointer<com.mutar.libav.bridge.avfilter.AVFilter.init_callback > init) {
		this.io.setPointerField(this, 19, init);
		return this;
	}
	/** C type : encode_sub_callback* */
	@Field(20) 
	public Pointer<AVCodec.encode_sub_callback > encode_sub() {
		return this.io.getPointerField(this, 20);
	}
	/** C type : encode_sub_callback* */
	@Field(20) 
	public AVCodec encode_sub(Pointer<AVCodec.encode_sub_callback > encode_sub) {
		this.io.setPointerField(this, 20, encode_sub);
		return this;
	}
	/** C type : encode2_callback* */
	@Field(21) 
	public Pointer<AVCodec.encode2_callback > encode2() {
		return this.io.getPointerField(this, 21);
	}
	/** C type : encode2_callback* */
	@Field(21) 
	public AVCodec encode2(Pointer<AVCodec.encode2_callback > encode2) {
		this.io.setPointerField(this, 21, encode2);
		return this;
	}
	/** C type : decode_callback* */
	@Field(22) 
	public Pointer<AVCodec.decode_callback > decode() {
		return this.io.getPointerField(this, 22);
	}
	/** C type : decode_callback* */
	@Field(22) 
	public AVCodec decode(Pointer<AVCodec.decode_callback > decode) {
		this.io.setPointerField(this, 22, decode);
		return this;
	}
	/** C type : close_callback* */
	@Field(23) 
	public Pointer<com.mutar.libav.bridge.avcodec.AVBitStreamFilter.close_callback > close() {
		return this.io.getPointerField(this, 23);
	}
	/** C type : close_callback* */
	@Field(23) 
	public AVCodec close(Pointer<com.mutar.libav.bridge.avcodec.AVBitStreamFilter.close_callback > close) {
		this.io.setPointerField(this, 23, close);
		return this;
	}
	/** C type : flush_callback* */
	@Field(24) 
	public Pointer<AVCodec.flush_callback > flush() {
		return this.io.getPointerField(this, 24);
	}
	/** C type : flush_callback* */
	@Field(24) 
	public AVCodec flush(Pointer<AVCodec.flush_callback > flush) {
		this.io.setPointerField(this, 24, flush);
		return this;
	}
	@Field(25) 
	public int caps_internal() {
		return this.io.getIntField(this, 25);
	}
	@Field(25) 
	public AVCodec caps_internal(int caps_internal) {
		this.io.setIntField(this, 25, caps_internal);
		return this;
	}
	/** <i>native declaration : ffmpeg_build/include/libavcodec/avcodec.h:946</i> */
	public static abstract class init_thread_copy_callback extends Callback<init_thread_copy_callback > {
		public int apply(Pointer<AVCodecContext > AVCodecContextPtr1) {
			return apply(Pointer.getPeer(AVCodecContextPtr1));
		}
		public int apply(@Ptr long AVCodecContextPtr1) {
			return apply((Pointer)Pointer.pointerToAddress(AVCodecContextPtr1, AVCodecContext.class));
		}
	};
	/** <i>native declaration : ffmpeg_build/include/libavcodec/avcodec.h:947</i> */
	public static abstract class update_thread_context_callback extends Callback<update_thread_context_callback > {
		public int apply(Pointer<AVCodecContext > dst, Pointer<AVCodecContext > src) {
			return apply(Pointer.getPeer(dst), Pointer.getPeer(src));
		}
		public int apply(@Ptr long dst, @Ptr long src) {
			return apply((Pointer)Pointer.pointerToAddress(dst, AVCodecContext.class), (Pointer)Pointer.pointerToAddress(src, AVCodecContext.class));
		}
	};
	/** <i>native declaration : ffmpeg_build/include/libavcodec/avcodec.h:948</i> */
	public static abstract class init_static_data_callback extends Callback<init_static_data_callback > {
		public void apply(Pointer<AVCodec > codec) {
			apply(Pointer.getPeer(codec));
		}
		public void apply(@Ptr long codec) {
			apply((Pointer)Pointer.pointerToAddress(codec, AVCodec.class));
		}
	};
	/** <i>native declaration : ffmpeg_build/include/libavcodec/avcodec.h:949</i> */
	public static abstract class init_callback extends Callback<init_callback > {
		public int apply(Pointer<AVCodecContext > AVCodecContextPtr1) {
			return apply(Pointer.getPeer(AVCodecContextPtr1));
		}
		public int apply(@Ptr long AVCodecContextPtr1) {
			return apply((Pointer)Pointer.pointerToAddress(AVCodecContextPtr1, AVCodecContext.class));
		}
	};
	/** <i>native declaration : ffmpeg_build/include/libavcodec/avcodec.h:950</i> */
	public static abstract class encode_sub_callback extends Callback<encode_sub_callback > {
		public int apply(Pointer<AVCodecContext > AVCodecContextPtr1, Pointer<Byte > buf, int buf_size, Pointer<AVSubtitle > sub) {
			return apply(Pointer.getPeer(AVCodecContextPtr1), Pointer.getPeer(buf), buf_size, Pointer.getPeer(sub));
		}
		public int apply(@Ptr long AVCodecContextPtr1, @Ptr long buf, int buf_size, @Ptr long sub) {
			return apply((Pointer)Pointer.pointerToAddress(AVCodecContextPtr1, AVCodecContext.class), (Pointer)Pointer.pointerToAddress(buf, Byte.class), buf_size, (Pointer)Pointer.pointerToAddress(sub, AVSubtitle.class));
		}
	};
	/** <i>native declaration : ffmpeg_build/include/libavcodec/avcodec.h:951</i> */
	public static abstract class encode2_callback extends Callback<encode2_callback > {
		public int apply(Pointer<AVCodecContext > avctx, Pointer<AVPacket > avpkt, Pointer<AVFrame > frame, Pointer<Integer > got_packet_ptr) {
			return apply(Pointer.getPeer(avctx), Pointer.getPeer(avpkt), Pointer.getPeer(frame), Pointer.getPeer(got_packet_ptr));
		}
		public int apply(@Ptr long avctx, @Ptr long avpkt, @Ptr long frame, @Ptr long got_packet_ptr) {
			return apply((Pointer)Pointer.pointerToAddress(avctx, AVCodecContext.class), (Pointer)Pointer.pointerToAddress(avpkt, AVPacket.class), (Pointer)Pointer.pointerToAddress(frame, AVFrame.class), (Pointer)Pointer.pointerToAddress(got_packet_ptr, Integer.class));
		}
	};
	/** <i>native declaration : ffmpeg_build/include/libavcodec/avcodec.h:952</i> */
	public static abstract class decode_callback extends Callback<decode_callback > {
		public int apply(Pointer<AVCodecContext > AVCodecContextPtr1, Pointer<? > outdata, Pointer<Integer > outdata_size, Pointer<AVPacket > avpkt) {
			return apply(Pointer.getPeer(AVCodecContextPtr1), Pointer.getPeer(outdata), Pointer.getPeer(outdata_size), Pointer.getPeer(avpkt));
		}
		public int apply(@Ptr long AVCodecContextPtr1, @Ptr long outdata, @Ptr long outdata_size, @Ptr long avpkt) {
			return apply((Pointer)Pointer.pointerToAddress(AVCodecContextPtr1, AVCodecContext.class), Pointer.pointerToAddress(outdata), (Pointer)Pointer.pointerToAddress(outdata_size, Integer.class), (Pointer)Pointer.pointerToAddress(avpkt, AVPacket.class));
		}
	};
	/** <i>native declaration : ffmpeg_build/include/libavcodec/avcodec.h:953</i> */
	public static abstract class close_callback extends Callback<close_callback > {
		public int apply(Pointer<AVCodecContext > AVCodecContextPtr1) {
			return apply(Pointer.getPeer(AVCodecContextPtr1));
		}
		public int apply(@Ptr long AVCodecContextPtr1) {
			return apply((Pointer)Pointer.pointerToAddress(AVCodecContextPtr1, AVCodecContext.class));
		}
	};
	/** <i>native declaration : ffmpeg_build/include/libavcodec/avcodec.h:954</i> */
	public static abstract class flush_callback extends Callback<flush_callback > {
		public void apply(Pointer<AVCodecContext > AVCodecContextPtr1) {
			apply(Pointer.getPeer(AVCodecContextPtr1));
		}
		public void apply(@Ptr long AVCodecContextPtr1) {
			apply((Pointer)Pointer.pointerToAddress(AVCodecContextPtr1, AVCodecContext.class));
		}
	};
	public AVCodec() {
		super();
	}
	public AVCodec(Pointer pointer) {
		super(pointer);
	}
}
