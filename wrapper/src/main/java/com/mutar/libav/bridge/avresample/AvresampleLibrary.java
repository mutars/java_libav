package com.mutar.libav.bridge.avresample;
import com.mutar.libav.bridge.avutil.AVClass;
import com.mutar.libav.bridge.avutil.AVFrame;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVMatrixEncoding;
import java.util.Collections;
import java.util.Iterator;
import org.bridj.BridJ;
import org.bridj.CRuntime;
import org.bridj.FlagSet;
import org.bridj.IntValuedEnum;
import org.bridj.Pointer;
import org.bridj.ann.Library;
import org.bridj.ann.Name;
import org.bridj.ann.Ptr;
import org.bridj.ann.Runtime;
/**
 * Wrapper for library <b>avresample</b><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("avresample") 
@Runtime(CRuntime.class) 
public class AvresampleLibrary {
	static {
		BridJ.register();
	}
	/**
	 * Mixing Coefficient Types<br>
	 * enum values<br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:7</i>
	 */
	public enum AVMixCoeffType implements IntValuedEnum<AVMixCoeffType > {
		/** 16-bit 8.8 fixed-point */
		AV_MIX_COEFF_TYPE_Q8(0),
		/** 32-bit 17.15 fixed-point */
		AV_MIX_COEFF_TYPE_Q15(1),
		/** floating-point */
		AV_MIX_COEFF_TYPE_FLT(2),
		/** Number of coeff types. Not part of ABI */
		AV_MIX_COEFF_TYPE_NB(3);
		AVMixCoeffType(long value) {
			this.value = value;
		}
		public final long value;
		public long value() {
			return this.value;
		}
		public Iterator<AVMixCoeffType > iterator() {
			return Collections.singleton(this).iterator();
		}
		public static IntValuedEnum<AVMixCoeffType > fromValue(int value) {
			return FlagSet.fromValue(value, values());
		}
	};
	/**
	 * Resampling Filter Types<br>
	 * enum values<br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:13</i>
	 */
	public enum AVResampleFilterType implements IntValuedEnum<AVResampleFilterType > {
		/** < Cubic */
		AV_RESAMPLE_FILTER_TYPE_CUBIC(0),
		/** < Blackman Nuttall Windowed Sinc */
		AV_RESAMPLE_FILTER_TYPE_BLACKMAN_NUTTALL(1),
		/** < Kaiser Windowed Sinc */
		AV_RESAMPLE_FILTER_TYPE_KAISER(2);
		AVResampleFilterType(long value) {
			this.value = value;
		}
		public final long value;
		public long value() {
			return this.value;
		}
		public Iterator<AVResampleFilterType > iterator() {
			return Collections.singleton(this).iterator();
		}
		public static IntValuedEnum<AVResampleFilterType > fromValue(int value) {
			return FlagSet.fromValue(value, values());
		}
	};
	/**
	 * enum values<br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:21</i>
	 */
	public enum AVResampleDitherMethod implements IntValuedEnum<AVResampleDitherMethod > {
		/** < Do not use dithering */
		AV_RESAMPLE_DITHER_NONE(0),
		/** < Rectangular Dither */
		AV_RESAMPLE_DITHER_RECTANGULAR(1),
		/** < Triangular Dither */
		AV_RESAMPLE_DITHER_TRIANGULAR(2),
		/** < Triangular Dither with High Pass */
		AV_RESAMPLE_DITHER_TRIANGULAR_HP(3),
		/** < Triangular Dither with Noise Shaping */
		AV_RESAMPLE_DITHER_TRIANGULAR_NS(4),
		/** < Number of dither types. Not part of ABI. */
		AV_RESAMPLE_DITHER_NB(5);
		AVResampleDitherMethod(long value) {
			this.value = value;
		}
		public final long value;
		public long value() {
			return this.value;
		}
		public Iterator<AVResampleDitherMethod > iterator() {
			return Collections.singleton(this).iterator();
		}
		public static IntValuedEnum<AVResampleDitherMethod > fromValue(int value) {
			return FlagSet.fromValue(value, values());
		}
	};
	/**
	 * Conversion Error : a.num<br>
	 * SKIPPED:<br>
	 * <i>native declaration : ffmpeg_build/include/libavutil/rational.h:26</i><br>
	 * const int64_t tmp = a.num * (int64_t)b.den - b.num * (int64_t)a.den;
	 */
	/** <i>native declaration : ffmpeg_build/include/libavresample/version.h</i> */
	public static final int LIBAVRESAMPLE_VERSION_MAJOR = (int)3;
	/** <i>native declaration : ffmpeg_build/include/libavresample/version.h</i> */
	public static final int LIBAVRESAMPLE_VERSION_MINOR = (int)0;
	/** <i>native declaration : ffmpeg_build/include/libavresample/version.h</i> */
	public static final int LIBAVRESAMPLE_VERSION_MICRO = (int)0;
	/** <i>native declaration : ffmpeg_build/include/libavresample/version.h</i> */
	public static final int LIBAVRESAMPLE_VERSION_INT = (int)((3) << 16 | (0) << 8 | (0));
	/**
	 * define<br>
	 * Conversion Error : 3.0.<br>
	 * SKIPPED:<br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/version.h:0</i><br>
	 * 3.0.
	 */
	/** <i>native declaration : ffmpeg_build/include/libavresample/version.h</i> */
	public static final int LIBAVRESAMPLE_BUILD = (int)((3) << 16 | (0) << 8 | (0));
	/** <i>native declaration : ffmpeg_build/include/libavresample/version.h</i> */
	public static final String LIBAVRESAMPLE_IDENT = (String)"Lavr";
	/** <i>native declaration : ffmpeg_build/include/libavresample/avresample.h</i> */
	public static final int AVRESAMPLE_MAX_CHANNELS = (int)32;
	/**
	 * Return the LIBAVRESAMPLE_VERSION_INT constant.<br>
	 * Original signature : <code>int avresample_version()</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:26</i>
	 */
	public native int avresample_version();
	/**
	 * Return the libavresample build-time configuration.<br>
	 * @return  configure string<br>
	 * Original signature : <code>char* avresample_configuration()</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:32</i>
	 */
	public Pointer<Byte > avresample_configuration() {
		return (Pointer)Pointer.pointerToAddress(avresample_configuration$2(), Byte.class);
	}
	@Ptr 
	@Name("avresample_configuration") 
	protected native long avresample_configuration$2();
	/**
	 * Return the libavresample license.<br>
	 * Original signature : <code>char* avresample_license()</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:37</i>
	 */
	public Pointer<Byte > avresample_license() {
		return (Pointer)Pointer.pointerToAddress(avresample_license$2(), Byte.class);
	}
	@Ptr 
	@Name("avresample_license") 
	protected native long avresample_license$2();
	/**
	 * Get the AVClass for AVAudioResampleContext.<br>
	 * Can be used in combination with AV_OPT_SEARCH_FAKE_OBJ for examining options<br>
	 * without allocating a context.<br>
	 * @see av_opt_find().<br>
	 * @return AVClass for AVAudioResampleContext<br>
	 * Original signature : <code>AVClass* avresample_get_class()</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:46</i>
	 */
	public Pointer<AVClass > avresample_get_class() {
		return (Pointer)Pointer.pointerToAddress(avresample_get_class$2(), AVClass.class);
	}
	@Ptr 
	@Name("avresample_get_class") 
	protected native long avresample_get_class$2();
	/**
	 * Allocate AVAudioResampleContext and set options.<br>
	 * @return  allocated audio resample context, or NULL on failure<br>
	 * Original signature : <code>AVAudioResampleContext* avresample_alloc_context()</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:52</i>
	 */
	public Pointer<AvresampleLibrary.AVAudioResampleContext > avresample_alloc_context() {
		return (Pointer)Pointer.pointerToAddress(avresample_alloc_context$2(), AvresampleLibrary.AVAudioResampleContext.class);
	}
	@Ptr 
	@Name("avresample_alloc_context") 
	protected native long avresample_alloc_context$2();
	/**
	 * Initialize AVAudioResampleContext.<br>
	 * @note The context must be configured using the AVOption API.<br>
	 * @see av_opt_set_int()<br>
	 * @see av_opt_set_dict()<br>
	 * @param avr  audio resample context<br>
	 * @return     0 on success, negative AVERROR code on failure<br>
	 * Original signature : <code>int avresample_open(AVAudioResampleContext*)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:62</i>
	 */
	public int avresample_open(Pointer<AvresampleLibrary.AVAudioResampleContext > avr) {
		return avresample_open(Pointer.getPeer(avr));
	}
	protected native int avresample_open(@Ptr long avr);
	/**
	 * Check whether an AVAudioResampleContext is open or closed.<br>
	 * @param avr AVAudioResampleContext to check<br>
	 * @return 1 if avr is open, 0 if avr is closed.<br>
	 * Original signature : <code>int avresample_is_open(AVAudioResampleContext*)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:69</i>
	 */
	public int avresample_is_open(Pointer<AvresampleLibrary.AVAudioResampleContext > avr) {
		return avresample_is_open(Pointer.getPeer(avr));
	}
	protected native int avresample_is_open(@Ptr long avr);
	/**
	 * Close AVAudioResampleContext.<br>
	 * This closes the context, but it does not change the parameters. The context<br>
	 * can be reopened with avresample_open(). It does, however, clear the output<br>
	 * FIFO and any remaining leftover samples in the resampling delay buffer. If<br>
	 * there was a custom matrix being used, that is also cleared.<br>
	 * @see avresample_convert()<br>
	 * @see avresample_set_matrix()<br>
	 * @param avr  audio resample context<br>
	 * Original signature : <code>void avresample_close(AVAudioResampleContext*)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:81</i>
	 */
	public void avresample_close(Pointer<AvresampleLibrary.AVAudioResampleContext > avr) {
		avresample_close(Pointer.getPeer(avr));
	}
	protected native void avresample_close(@Ptr long avr);
	/**
	 * Free AVAudioResampleContext and associated AVOption values.<br>
	 * This also calls avresample_close() before freeing.<br>
	 * @param avr  audio resample context<br>
	 * Original signature : <code>void avresample_free(AVAudioResampleContext**)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:88</i>
	 */
	public void avresample_free(Pointer<Pointer<AvresampleLibrary.AVAudioResampleContext > > avr) {
		avresample_free(Pointer.getPeer(avr));
	}
	protected native void avresample_free(@Ptr long avr);
	/**
	 * Generate a channel mixing matrix.<br>
	 * This function is the one used internally by libavresample for building the<br>
	 * default mixing matrix. It is made public just as a utility function for<br>
	 * building custom matrices.<br>
	 * @param in_layout           input channel layout<br>
	 * @param out_layout          output channel layout<br>
	 * @param center_mix_level    mix level for the center channel<br>
	 * @param surround_mix_level  mix level for the surround channel(s)<br>
	 * @param lfe_mix_level       mix level for the low-frequency effects channel<br>
	 * @param normalize           if 1, coefficients will be normalized to prevent<br>
	 *                            overflow. if 0, coefficients will not be<br>
	 *                            normalized.<br>
	 * @param[out] matrix         mixing coefficients; matrix[i + stride * o] is<br>
	 *                            the weight of input channel i in output channel o.<br>
	 * @param stride              distance between adjacent input channels in the<br>
	 *                            matrix array<br>
	 * @param matrix_encoding     matrixed stereo downmix mode (e.g. dplii)<br>
	 * @return                    0 on success, negative AVERROR code on failure<br>
	 * Original signature : <code>int avresample_build_matrix(uint64_t, uint64_t, double, double, double, int, double*, int, AVMatrixEncoding)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:110</i>
	 */
	public int avresample_build_matrix(long in_layout, long out_layout, double center_mix_level, double surround_mix_level, double lfe_mix_level, int normalize, Pointer<Double > matrix, int stride, IntValuedEnum<AVMatrixEncoding > matrix_encoding) {
		return avresample_build_matrix(in_layout, out_layout, center_mix_level, surround_mix_level, lfe_mix_level, normalize, Pointer.getPeer(matrix), stride, (int)matrix_encoding.value());
	}
	protected native int avresample_build_matrix(long in_layout, long out_layout, double center_mix_level, double surround_mix_level, double lfe_mix_level, int normalize, @Ptr long matrix, int stride, int matrix_encoding);
	/**
	 * Get the current channel mixing matrix.<br>
	 * If no custom matrix has been previously set or the AVAudioResampleContext is<br>
	 * not open, an error is returned.<br>
	 * @param avr     audio resample context<br>
	 * @param matrix  mixing coefficients; matrix[i + stride * o] is the weight of<br>
	 *                input channel i in output channel o.<br>
	 * @param stride  distance between adjacent input channels in the matrix array<br>
	 * @return        0 on success, negative AVERROR code on failure<br>
	 * Original signature : <code>int avresample_get_matrix(AVAudioResampleContext*, double*, int)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:122</i>
	 */
	public int avresample_get_matrix(Pointer<AvresampleLibrary.AVAudioResampleContext > avr, Pointer<Double > matrix, int stride) {
		return avresample_get_matrix(Pointer.getPeer(avr), Pointer.getPeer(matrix), stride);
	}
	protected native int avresample_get_matrix(@Ptr long avr, @Ptr long matrix, int stride);
	/**
	 * Set channel mixing matrix.<br>
	 * Allows for setting a custom mixing matrix, overriding the default matrix<br>
	 * generated internally during avresample_open(). This function can be called<br>
	 * anytime on an allocated context, either before or after calling<br>
	 * avresample_open(), as long as the channel layouts have been set.<br>
	 * avresample_convert() always uses the current matrix.<br>
	 * Calling avresample_close() on the context will clear the current matrix.<br>
	 * @see avresample_close()<br>
	 * @param avr     audio resample context<br>
	 * @param matrix  mixing coefficients; matrix[i + stride * o] is the weight of<br>
	 *                input channel i in output channel o.<br>
	 * @param stride  distance between adjacent input channels in the matrix array<br>
	 * @return        0 on success, negative AVERROR code on failure<br>
	 * Original signature : <code>int avresample_set_matrix(AVAudioResampleContext*, const double*, int)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:139</i>
	 */
	public int avresample_set_matrix(Pointer<AvresampleLibrary.AVAudioResampleContext > avr, Pointer<Double > matrix, int stride) {
		return avresample_set_matrix(Pointer.getPeer(avr), Pointer.getPeer(matrix), stride);
	}
	protected native int avresample_set_matrix(@Ptr long avr, @Ptr long matrix, int stride);
	/**
	 * Set a customized input channel mapping.<br>
	 * This function can only be called when the allocated context is not open.<br>
	 * Also, the input channel layout must have already been set.<br>
	 * Calling avresample_close() on the context will clear the channel mapping.<br>
	 * The map for each input channel specifies the channel index in the source to<br>
	 * use for that particular channel, or -1 to mute the channel. Source channels<br>
	 * can be duplicated by using the same index for multiple input channels.<br>
	 * Examples:<br>
	 * Reordering 5.1 AAC order (C,L,R,Ls,Rs,LFE) to FFmpeg order (L,R,C,LFE,Ls,Rs):<br>
	 * { 1, 2, 0, 5, 3, 4 }<br>
	 * Muting the 3rd channel in 4-channel input:<br>
	 * { 0, 1, -1, 3 }<br>
	 * Duplicating the left channel of stereo input:<br>
	 * { 0, 0 }<br>
	 * @param avr         audio resample context<br>
	 * @param channel_map customized input channel mapping<br>
	 * @return            0 on success, negative AVERROR code on failure<br>
	 * Original signature : <code>int avresample_set_channel_mapping(AVAudioResampleContext*, const int*)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:160</i>
	 */
	public int avresample_set_channel_mapping(Pointer<AvresampleLibrary.AVAudioResampleContext > avr, Pointer<Integer > channel_map) {
		return avresample_set_channel_mapping(Pointer.getPeer(avr), Pointer.getPeer(channel_map));
	}
	protected native int avresample_set_channel_mapping(@Ptr long avr, @Ptr long channel_map);
	/**
	 * Set compensation for resampling.<br>
	 * This can be called anytime after avresample_open(). If resampling is not<br>
	 * automatically enabled because of a sample rate conversion, the<br>
	 * "force_resampling" option must have been set to 1 when opening the context<br>
	 * in order to use resampling compensation.<br>
	 * @param avr                    audio resample context<br>
	 * @param sample_delta           compensation delta, in samples<br>
	 * @param compensation_distance  compensation distance, in samples<br>
	 * @return                       0 on success, negative AVERROR code on failure<br>
	 * Original signature : <code>int avresample_set_compensation(AVAudioResampleContext*, int, int)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:173</i>
	 */
	public int avresample_set_compensation(Pointer<AvresampleLibrary.AVAudioResampleContext > avr, int sample_delta, int compensation_distance) {
		return avresample_set_compensation(Pointer.getPeer(avr), sample_delta, compensation_distance);
	}
	protected native int avresample_set_compensation(@Ptr long avr, int sample_delta, int compensation_distance);
	/**
	 * Provide the upper bound on the number of samples the configured<br>
	 * conversion would output.<br>
	 * @param avr           audio resample context<br>
	 * @param in_nb_samples number of input samples<br>
	 * @return              number of samples or AVERROR(EINVAL) if the value<br>
	 *                      would exceed INT_MAX<br>
	 * Original signature : <code>int avresample_get_out_samples(AVAudioResampleContext*, int)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:183</i>
	 */
	public int avresample_get_out_samples(Pointer<AvresampleLibrary.AVAudioResampleContext > avr, int in_nb_samples) {
		return avresample_get_out_samples(Pointer.getPeer(avr), in_nb_samples);
	}
	protected native int avresample_get_out_samples(@Ptr long avr, int in_nb_samples);
	/**
	 * Convert input samples and write them to the output FIFO.<br>
	 * The upper bound on the number of output samples can be obtained through<br>
	 * avresample_get_out_samples().<br>
	 * The output data can be NULL or have fewer allocated samples than required.<br>
	 * In this case, any remaining samples not written to the output will be added<br>
	 * to an internal FIFO buffer, to be returned at the next call to this function<br>
	 * or to avresample_read().<br>
	 * If converting sample rate, there may be data remaining in the internal<br>
	 * resampling delay buffer. avresample_get_delay() tells the number of remaining<br>
	 * samples. To get this data as output, call avresample_convert() with NULL<br>
	 * input.<br>
	 * At the end of the conversion process, there may be data remaining in the<br>
	 * internal FIFO buffer. avresample_available() tells the number of remaining<br>
	 * samples. To get this data as output, either call avresample_convert() with<br>
	 * NULL input or call avresample_read().<br>
	 * @see avresample_get_out_samples()<br>
	 * @see avresample_read()<br>
	 * @see avresample_get_delay()<br>
	 * @param avr             audio resample context<br>
	 * @param output          output data pointers<br>
	 * @param out_plane_size  output plane size, in bytes.<br>
	 *                        This can be 0 if unknown, but that will lead to<br>
	 *                        optimized functions not being used directly on the<br>
	 *                        output, which could slow down some conversions.<br>
	 * @param out_samples     maximum number of samples that the output buffer can hold<br>
	 * @param input           input data pointers<br>
	 * @param in_plane_size   input plane size, in bytes<br>
	 *                        This can be 0 if unknown, but that will lead to<br>
	 *                        optimized functions not being used directly on the<br>
	 *                        input, which could slow down some conversions.<br>
	 * @param in_samples      number of input samples to convert<br>
	 * @return                number of samples written to the output buffer,<br>
	 *                        not including converted samples added to the internal<br>
	 *                        output FIFO<br>
	 * Original signature : <code>int avresample_convert(AVAudioResampleContext*, uint8_t**, int, int, uint8_t**, int, int)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:221</i>
	 */
	public int avresample_convert(Pointer<AvresampleLibrary.AVAudioResampleContext > avr, Pointer<Pointer<Byte > > output, int out_plane_size, int out_samples, Pointer<Pointer<Byte > > input, int in_plane_size, int in_samples) {
		return avresample_convert(Pointer.getPeer(avr), Pointer.getPeer(output), out_plane_size, out_samples, Pointer.getPeer(input), in_plane_size, in_samples);
	}
	protected native int avresample_convert(@Ptr long avr, @Ptr long output, int out_plane_size, int out_samples, @Ptr long input, int in_plane_size, int in_samples);
	/**
	 * Return the number of samples currently in the resampling delay buffer.<br>
	 * When resampling, there may be a delay between the input and output. Any<br>
	 * unconverted samples in each call are stored internally in a delay buffer.<br>
	 * This function allows the user to determine the current number of samples in<br>
	 * the delay buffer, which can be useful for synchronization.<br>
	 * @see avresample_convert()<br>
	 * @param avr  audio resample context<br>
	 * @return     number of samples currently in the resampling delay buffer<br>
	 * Original signature : <code>int avresample_get_delay(AVAudioResampleContext*)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:233</i>
	 */
	public int avresample_get_delay(Pointer<AvresampleLibrary.AVAudioResampleContext > avr) {
		return avresample_get_delay(Pointer.getPeer(avr));
	}
	protected native int avresample_get_delay(@Ptr long avr);
	/**
	 * Return the number of available samples in the output FIFO.<br>
	 * During conversion, if the user does not specify an output buffer or<br>
	 * specifies an output buffer that is smaller than what is needed, remaining<br>
	 * samples that are not written to the output are stored to an internal FIFO<br>
	 * buffer. The samples in the FIFO can be read with avresample_read() or<br>
	 * avresample_convert().<br>
	 * @see avresample_read()<br>
	 * @see avresample_convert()<br>
	 * @param avr  audio resample context<br>
	 * @return     number of samples available for reading<br>
	 * Original signature : <code>int avresample_available(AVAudioResampleContext*)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:247</i>
	 */
	public int avresample_available(Pointer<AvresampleLibrary.AVAudioResampleContext > avr) {
		return avresample_available(Pointer.getPeer(avr));
	}
	protected native int avresample_available(@Ptr long avr);
	/**
	 * Read samples from the output FIFO.<br>
	 * During conversion, if the user does not specify an output buffer or<br>
	 * specifies an output buffer that is smaller than what is needed, remaining<br>
	 * samples that are not written to the output are stored to an internal FIFO<br>
	 * buffer. This function can be used to read samples from that internal FIFO.<br>
	 * @see avresample_available()<br>
	 * @see avresample_convert()<br>
	 * @param avr         audio resample context<br>
	 * @param output      output data pointers. May be NULL, in which case<br>
	 *                    nb_samples of data is discarded from output FIFO.<br>
	 * @param nb_samples  number of samples to read from the FIFO<br>
	 * @return            the number of samples written to output<br>
	 * Original signature : <code>int avresample_read(AVAudioResampleContext*, uint8_t**, int)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:263</i>
	 */
	public int avresample_read(Pointer<AvresampleLibrary.AVAudioResampleContext > avr, Pointer<Pointer<Byte > > output, int nb_samples) {
		return avresample_read(Pointer.getPeer(avr), Pointer.getPeer(output), nb_samples);
	}
	protected native int avresample_read(@Ptr long avr, @Ptr long output, int nb_samples);
	/**
	 * Convert the samples in the input AVFrame and write them to the output AVFrame.<br>
	 * Input and output AVFrames must have channel_layout, sample_rate and format set.<br>
	 * The upper bound on the number of output samples is obtained through<br>
	 * avresample_get_out_samples().<br>
	 * If the output AVFrame does not have the data pointers allocated the nb_samples<br>
	 * field will be set using avresample_get_out_samples() and av_frame_get_buffer()<br>
	 * is called to allocate the frame.<br>
	 * The output AVFrame can be NULL or have fewer allocated samples than required.<br>
	 * In this case, any remaining samples not written to the output will be added<br>
	 * to an internal FIFO buffer, to be returned at the next call to this function<br>
	 * or to avresample_convert() or to avresample_read().<br>
	 * If converting sample rate, there may be data remaining in the internal<br>
	 * resampling delay buffer. avresample_get_delay() tells the number of<br>
	 * remaining samples. To get this data as output, call this function or<br>
	 * avresample_convert() with NULL input.<br>
	 * At the end of the conversion process, there may be data remaining in the<br>
	 * internal FIFO buffer. avresample_available() tells the number of remaining<br>
	 * samples. To get this data as output, either call this function or<br>
	 * avresample_convert() with NULL input or call avresample_read().<br>
	 * If the AVAudioResampleContext configuration does not match the output and<br>
	 * input AVFrame settings the conversion does not take place and depending on<br>
	 * which AVFrame is not matching AVERROR_OUTPUT_CHANGED, AVERROR_INPUT_CHANGED<br>
	 * or AVERROR_OUTPUT_CHANGED|AVERROR_INPUT_CHANGED is returned.<br>
	 * @see avresample_get_out_samples()<br>
	 * @see avresample_available()<br>
	 * @see avresample_convert()<br>
	 * @see avresample_read()<br>
	 * @see avresample_get_delay()<br>
	 * @param avr             audio resample context<br>
	 * @param output          output AVFrame<br>
	 * @param input           input AVFrame<br>
	 * @return                0 on success, AVERROR on failure or nonmatching<br>
	 *                        configuration.<br>
	 * Original signature : <code>int avresample_convert_frame(AVAudioResampleContext*, AVFrame*, AVFrame*)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:300</i>
	 */
	public int avresample_convert_frame(Pointer<AvresampleLibrary.AVAudioResampleContext > avr, Pointer<AVFrame > output, Pointer<AVFrame > input) {
		return avresample_convert_frame(Pointer.getPeer(avr), Pointer.getPeer(output), Pointer.getPeer(input));
	}
	protected native int avresample_convert_frame(@Ptr long avr, @Ptr long output, @Ptr long input);
	/**
	 * Configure or reconfigure the AVAudioResampleContext using the information<br>
	 * provided by the AVFrames.<br>
	 * The original resampling context is reset even on failure.<br>
	 * The function calls avresample_close() internally if the context is open.<br>
	 * @see avresample_open();<br>
	 * @see avresample_close();<br>
	 * @param avr             audio resample context<br>
	 * @param output          output AVFrame<br>
	 * @param input           input AVFrame<br>
	 * @return                0 on success, AVERROR on failure.<br>
	 * Original signature : <code>int avresample_config(AVAudioResampleContext*, AVFrame*, AVFrame*)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavresample/avresample.h:314</i>
	 */
	public int avresample_config(Pointer<AvresampleLibrary.AVAudioResampleContext > avr, Pointer<AVFrame > out, Pointer<AVFrame > in) {
		return avresample_config(Pointer.getPeer(avr), Pointer.getPeer(out), Pointer.getPeer(in));
	}
	protected native int avresample_config(@Ptr long avr, @Ptr long out, @Ptr long in);
	/** Undefined type */
	public static interface AVAudioResampleContext {
		
	};
}
