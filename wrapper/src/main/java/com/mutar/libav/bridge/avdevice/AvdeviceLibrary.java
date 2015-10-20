package com.mutar.libav.bridge.avdevice;
import com.mutar.libav.bridge.avformat.AVFormatContext;
import com.mutar.libav.bridge.avformat.AVInputFormat;
import com.mutar.libav.bridge.avformat.AVOutputFormat;
import com.mutar.libav.bridge.avutil.AVOption;
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
import org.bridj.util.DefaultParameterizedType;
/**
 * Wrapper for library <b>avdevice</b><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("avdevice") 
@Runtime(CRuntime.class) 
public class AvdeviceLibrary {
	static {
		BridJ.register();
	}
	/**
	 * Message types used by avdevice_app_to_dev_control_message().<br>
	 * enum values<br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:73</i>
	 */
	public enum AVAppToDevMessageType implements IntValuedEnum<AVAppToDevMessageType > {
		AV_APP_TO_DEV_NONE((('E') | (('N') << 8) | (('O') << 16) | ('N' << 24))),
		AV_APP_TO_DEV_WINDOW_SIZE((('M') | (('O') << 8) | (('E') << 16) | ('G' << 24))),
		AV_APP_TO_DEV_WINDOW_REPAINT((('A') | (('P') << 8) | (('E') << 16) | ('R' << 24))),
		AV_APP_TO_DEV_PAUSE(((' ') | (('U') << 8) | (('A') << 16) | ('P' << 24))),
		AV_APP_TO_DEV_PLAY((('Y') | (('A') << 8) | (('L') << 16) | ('P' << 24))),
		AV_APP_TO_DEV_TOGGLE_PAUSE((('T') | (('U') << 8) | (('A') << 16) | ('P' << 24))),
		AV_APP_TO_DEV_SET_VOLUME((('L') | (('O') << 8) | (('V') << 16) | ('S' << 24))),
		AV_APP_TO_DEV_MUTE((('T') | (('U') << 8) | (('M') << 16) | (' ' << 24))),
		AV_APP_TO_DEV_UNMUTE((('T') | (('U') << 8) | (('M') << 16) | ('U' << 24))),
		AV_APP_TO_DEV_TOGGLE_MUTE((('T') | (('U') << 8) | (('M') << 16) | ('T' << 24))),
		AV_APP_TO_DEV_GET_VOLUME((('L') | (('O') << 8) | (('V') << 16) | ('G' << 24))),
		AV_APP_TO_DEV_GET_MUTE((('T') | (('U') << 8) | (('M') << 16) | ('G' << 24)));
		AVAppToDevMessageType(long value) {
			this.value = value;
		}
		public final long value;
		public long value() {
			return this.value;
		}
		public Iterator<AVAppToDevMessageType > iterator() {
			return Collections.singleton(this).iterator();
		}
		public static IntValuedEnum<AVAppToDevMessageType > fromValue(int value) {
			return FlagSet.fromValue(value, values());
		}
	};
	/**
	 * Message types used by avdevice_dev_to_app_control_message().<br>
	 * enum values<br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:87</i>
	 */
	public enum AVDevToAppMessageType implements IntValuedEnum<AVDevToAppMessageType > {
		AV_DEV_TO_APP_NONE((('E') | (('N') << 8) | (('O') << 16) | ('N' << 24))),
		AV_DEV_TO_APP_CREATE_WINDOW_BUFFER((('E') | (('R') << 8) | (('C') << 16) | ('B' << 24))),
		AV_DEV_TO_APP_PREPARE_WINDOW_BUFFER((('E') | (('R') << 8) | (('P') << 16) | ('B' << 24))),
		AV_DEV_TO_APP_DISPLAY_WINDOW_BUFFER((('S') | (('I') << 8) | (('D') << 16) | ('B' << 24))),
		AV_DEV_TO_APP_DESTROY_WINDOW_BUFFER((('S') | (('E') << 8) | (('D') << 16) | ('B' << 24))),
		AV_DEV_TO_APP_BUFFER_OVERFLOW((('L') | (('F') << 8) | (('O') << 16) | ('B' << 24))),
		AV_DEV_TO_APP_BUFFER_UNDERFLOW((('L') | (('F') << 8) | (('U') << 16) | ('B' << 24))),
		AV_DEV_TO_APP_BUFFER_READABLE(((' ') | (('D') << 8) | (('R') << 16) | ('B' << 24))),
		AV_DEV_TO_APP_BUFFER_WRITABLE(((' ') | (('R') << 8) | (('W') << 16) | ('B' << 24))),
		AV_DEV_TO_APP_MUTE_STATE_CHANGED((('T') | (('U') << 8) | (('M') << 16) | ('C' << 24))),
		AV_DEV_TO_APP_VOLUME_LEVEL_CHANGED((('L') | (('O') << 8) | (('V') << 16) | ('C' << 24)));
		AVDevToAppMessageType(long value) {
			this.value = value;
		}
		public final long value;
		public long value() {
			return this.value;
		}
		public Iterator<AVDevToAppMessageType > iterator() {
			return Collections.singleton(this).iterator();
		}
		public static IntValuedEnum<AVDevToAppMessageType > fromValue(int value) {
			return FlagSet.fromValue(value, values());
		}
	};
	/**
	 * Conversion Error : a.num<br>
	 * SKIPPED:<br>
	 * <i>native declaration : ffmpeg_build/include/libavutil/rational.h:0</i><br>
	 * const int64_t tmp = a.num * (int64_t)b.den - b.num * (int64_t)a.den;
	 */
	/** <i>native declaration : ffmpeg_build/include/libavdevice/version.h</i> */
	public static final int LIBAVDEVICE_VERSION_MAJOR = (int)57;
	/** <i>native declaration : ffmpeg_build/include/libavdevice/version.h</i> */
	public static final int LIBAVDEVICE_VERSION_MINOR = (int)0;
	/** <i>native declaration : ffmpeg_build/include/libavdevice/version.h</i> */
	public static final int LIBAVDEVICE_VERSION_MICRO = (int)100;
	/** <i>native declaration : ffmpeg_build/include/libavdevice/version.h</i> */
	public static final int LIBAVDEVICE_VERSION_INT = (int)((57) << 16 | (0) << 8 | (100));
	/**
	 * define<br>
	 * Conversion Error : 57.0.<br>
	 * SKIPPED:<br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/version.h:0</i><br>
	 * 57.0.
	 */
	/** <i>native declaration : ffmpeg_build/include/libavdevice/version.h</i> */
	public static final int LIBAVDEVICE_BUILD = (int)((57) << 16 | (0) << 8 | (100));
	/** <i>native declaration : ffmpeg_build/include/libavdevice/version.h</i> */
	public static final String LIBAVDEVICE_IDENT = (String)"Lavd";
	/**
	 * Return the LIBAVDEVICE_VERSION_INT constant.<br>
	 * Original signature : <code>int avdevice_version()</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:4</i>
	 */
	public native int avdevice_version();
	/**
	 * Return the libavdevice build-time configuration.<br>
	 * Original signature : <code>char* avdevice_configuration()</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:9</i>
	 */
	public Pointer<Byte > avdevice_configuration() {
		return (Pointer)Pointer.pointerToAddress(avdevice_configuration$2(), Byte.class);
	}
	@Ptr 
	@Name("avdevice_configuration") 
	protected native long avdevice_configuration$2();
	/**
	 * Return the libavdevice license.<br>
	 * Original signature : <code>char* avdevice_license()</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:14</i>
	 */
	public Pointer<Byte > avdevice_license() {
		return (Pointer)Pointer.pointerToAddress(avdevice_license$2(), Byte.class);
	}
	@Ptr 
	@Name("avdevice_license") 
	protected native long avdevice_license$2();
	/**
	 * Initialize libavdevice and register all the input and output devices.<br>
	 * @warning This function is not thread safe.<br>
	 * Original signature : <code>void avdevice_register_all()</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:20</i>
	 */
	public native void avdevice_register_all();
	/**
	 * Audio input devices iterator.<br>
	 * If d is NULL, returns the first registered input audio/video device,<br>
	 * if d is non-NULL, returns the next registered input audio/video device after d<br>
	 * or NULL if d is the last one.<br>
	 * Original signature : <code>AVInputFormat* av_input_audio_device_next(AVInputFormat*)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:28</i>
	 */
	public Pointer<AVInputFormat > av_input_audio_device_next(Pointer<AVInputFormat > d) {
		return (Pointer)Pointer.pointerToAddress(av_input_audio_device_next(Pointer.getPeer(d)), AVInputFormat.class);
	}
	@Ptr 
	protected native long av_input_audio_device_next(@Ptr long d);
	/**
	 * Video input devices iterator.<br>
	 * If d is NULL, returns the first registered input audio/video device,<br>
	 * if d is non-NULL, returns the next registered input audio/video device after d<br>
	 * or NULL if d is the last one.<br>
	 * Original signature : <code>AVInputFormat* av_input_video_device_next(AVInputFormat*)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:36</i>
	 */
	public Pointer<AVInputFormat > av_input_video_device_next(Pointer<AVInputFormat > d) {
		return (Pointer)Pointer.pointerToAddress(av_input_video_device_next(Pointer.getPeer(d)), AVInputFormat.class);
	}
	@Ptr 
	protected native long av_input_video_device_next(@Ptr long d);
	/**
	 * Audio output devices iterator.<br>
	 * If d is NULL, returns the first registered output audio/video device,<br>
	 * if d is non-NULL, returns the next registered output audio/video device after d<br>
	 * or NULL if d is the last one.<br>
	 * Original signature : <code>AVOutputFormat* av_output_audio_device_next(AVOutputFormat*)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:44</i>
	 */
	public Pointer<AVOutputFormat > av_output_audio_device_next(Pointer<AVOutputFormat > d) {
		return (Pointer)Pointer.pointerToAddress(av_output_audio_device_next(Pointer.getPeer(d)), AVOutputFormat.class);
	}
	@Ptr 
	protected native long av_output_audio_device_next(@Ptr long d);
	/**
	 * Video output devices iterator.<br>
	 * If d is NULL, returns the first registered output audio/video device,<br>
	 * if d is non-NULL, returns the next registered output audio/video device after d<br>
	 * or NULL if d is the last one.<br>
	 * Original signature : <code>AVOutputFormat* av_output_video_device_next(AVOutputFormat*)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:52</i>
	 */
	public Pointer<AVOutputFormat > av_output_video_device_next(Pointer<AVOutputFormat > d) {
		return (Pointer)Pointer.pointerToAddress(av_output_video_device_next(Pointer.getPeer(d)), AVOutputFormat.class);
	}
	@Ptr 
	protected native long av_output_video_device_next(@Ptr long d);
	/**
	 * Send control message from application to device.<br>
	 * @param s         device context.<br>
	 * @param type      message type.<br>
	 * @param data      message data. Exact type depends on message type.<br>
	 * @param data_size size of message data.<br>
	 * @return >= 0 on success, negative on error.<br>
	 *         AVERROR(ENOSYS) when device doesn't implement handler of the message.<br>
	 * Original signature : <code>int avdevice_app_to_dev_control_message(AVFormatContext*, AVAppToDevMessageType, void*, size_t)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:98</i>
	 */
	public int avdevice_app_to_dev_control_message(Pointer<AVFormatContext > s, IntValuedEnum<AvdeviceLibrary.AVAppToDevMessageType > type, Pointer<? > data, @Ptr long data_size) {
		return avdevice_app_to_dev_control_message(Pointer.getPeer(s), (int)type.value(), Pointer.getPeer(data), data_size);
	}
	protected native int avdevice_app_to_dev_control_message(@Ptr long s, int type, @Ptr long data, @Ptr long data_size);
	/**
	 * Send control message from device to application.<br>
	 * @param s         device context.<br>
	 * @param type      message type.<br>
	 * @param data      message data. Can be NULL.<br>
	 * @param data_size size of message data.<br>
	 * @return >= 0 on success, negative on error.<br>
	 *         AVERROR(ENOSYS) when application doesn't implement handler of the message.<br>
	 * Original signature : <code>int avdevice_dev_to_app_control_message(AVFormatContext*, AVDevToAppMessageType, void*, size_t)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:109</i>
	 */
	public int avdevice_dev_to_app_control_message(Pointer<AVFormatContext > s, IntValuedEnum<AvdeviceLibrary.AVDevToAppMessageType > type, Pointer<? > data, @Ptr long data_size) {
		return avdevice_dev_to_app_control_message(Pointer.getPeer(s), (int)type.value(), Pointer.getPeer(data), data_size);
	}
	protected native int avdevice_dev_to_app_control_message(@Ptr long s, int type, @Ptr long data, @Ptr long data_size);
	/**
	 * Initialize capabilities probing API based on AVOption API.<br>
	 * avdevice_capabilities_free() must be called when query capabilities API is<br>
	 * not used anymore.<br>
	 * @param[out] caps      Device capabilities data. Pointer to a NULL pointer must be passed.<br>
	 * @param s              Context of the device.<br>
	 * @param device_options An AVDictionary filled with device-private options.<br>
	 *                       On return this parameter will be destroyed and replaced with a dict<br>
	 *                       containing options that were not found. May be NULL.<br>
	 *                       The same options must be passed later to avformat_write_header() for output<br>
	 *                       devices or avformat_open_input() for input devices, or at any other place<br>
	 *                       that affects device-private options.<br>
	 * @return >= 0 on success, negative otherwise.<br>
	 * Original signature : <code>int avdevice_capabilities_create(AVDeviceCapabilitiesQuery**, AVFormatContext*, AVDictionary**)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:153</i>
	 */
	public int avdevice_capabilities_create(Pointer<Pointer<AVDeviceCapabilitiesQuery > > caps, Pointer<AVFormatContext > s, Pointer<Pointer<AvdeviceLibrary.AVDictionary > > device_options) {
		return avdevice_capabilities_create(Pointer.getPeer(caps), Pointer.getPeer(s), Pointer.getPeer(device_options));
	}
	protected native int avdevice_capabilities_create(@Ptr long caps, @Ptr long s, @Ptr long device_options);
	/**
	 * Free resources created by avdevice_capabilities_create()<br>
	 * @param caps Device capabilities data to be freed.<br>
	 * @param s    Context of the device.<br>
	 * Original signature : <code>void avdevice_capabilities_free(AVDeviceCapabilitiesQuery**, AVFormatContext*)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:160</i>
	 */
	public void avdevice_capabilities_free(Pointer<Pointer<AVDeviceCapabilitiesQuery > > caps, Pointer<AVFormatContext > s) {
		avdevice_capabilities_free(Pointer.getPeer(caps), Pointer.getPeer(s));
	}
	protected native void avdevice_capabilities_free(@Ptr long caps, @Ptr long s);
	/**
	 * List devices.<br>
	 * Returns available device names and their parameters.<br>
	 * @note: Some devices may accept system-dependent device names that cannot be<br>
	 *        autodetected. The list returned by this function cannot be assumed to<br>
	 *        be always completed.<br>
	 * @param s                device context.<br>
	 * @param[out] device_list list of autodetected devices.<br>
	 * @return count of autodetected devices, negative on error.<br>
	 * Original signature : <code>int avdevice_list_devices(AVFormatContext*, AVDeviceInfoList**)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:183</i>
	 */
	public int avdevice_list_devices(Pointer<AVFormatContext > s, Pointer<Pointer<AVDeviceInfoList > > device_list) {
		return avdevice_list_devices(Pointer.getPeer(s), Pointer.getPeer(device_list));
	}
	protected native int avdevice_list_devices(@Ptr long s, @Ptr long device_list);
	/**
	 * Convenient function to free result of avdevice_list_devices().<br>
	 * @param devices device list to be freed.<br>
	 * Original signature : <code>void avdevice_free_list_devices(AVDeviceInfoList**)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:189</i>
	 */
	public void avdevice_free_list_devices(Pointer<Pointer<AVDeviceInfoList > > device_list) {
		avdevice_free_list_devices(Pointer.getPeer(device_list));
	}
	protected native void avdevice_free_list_devices(@Ptr long device_list);
	/**
	 * List devices.<br>
	 * Returns available device names and their parameters.<br>
	 * These are convinient wrappers for avdevice_list_devices().<br>
	 * Device context is allocated and deallocated internally.<br>
	 * @param device           device format. May be NULL if device name is set.<br>
	 * @param device_name      device name. May be NULL if device format is set.<br>
	 * @param device_options   An AVDictionary filled with device-private options. May be NULL.<br>
	 *                         The same options must be passed later to avformat_write_header() for output<br>
	 *                         devices or avformat_open_input() for input devices, or at any other place<br>
	 *                         that affects device-private options.<br>
	 * @param[out] device_list list of autodetected devices<br>
	 * @return count of autodetected devices, negative on error.<br>
	 * @note device argument takes precedence over device_name when both are set.<br>
	 * Original signature : <code>int avdevice_list_input_sources(AVInputFormat*, const char*, AVDictionary*, AVDeviceInfoList**)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:206</i>
	 */
	public int avdevice_list_input_sources(Pointer<AVInputFormat > device, Pointer<Byte > device_name, Pointer<AvdeviceLibrary.AVDictionary > device_options, Pointer<Pointer<AVDeviceInfoList > > device_list) {
		return avdevice_list_input_sources(Pointer.getPeer(device), Pointer.getPeer(device_name), Pointer.getPeer(device_options), Pointer.getPeer(device_list));
	}
	protected native int avdevice_list_input_sources(@Ptr long device, @Ptr long device_name, @Ptr long device_options, @Ptr long device_list);
	/**
	 * Original signature : <code>int avdevice_list_output_sinks(AVOutputFormat*, const char*, AVDictionary*, AVDeviceInfoList**)</code><br>
	 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:208</i>
	 */
	public int avdevice_list_output_sinks(Pointer<AVOutputFormat > device, Pointer<Byte > device_name, Pointer<AvdeviceLibrary.AVDictionary > device_options, Pointer<Pointer<AVDeviceInfoList > > device_list) {
		return avdevice_list_output_sinks(Pointer.getPeer(device), Pointer.getPeer(device_name), Pointer.getPeer(device_options), Pointer.getPeer(device_list));
	}
	protected native int avdevice_list_output_sinks(@Ptr long device, @Ptr long device_name, @Ptr long device_options, @Ptr long device_list);
	/**
	 * AVOption table used by devices to implement device capabilities API. Should not be used by a user.<br>
	 * C type : extern const AVOption[]
	 */
	public Pointer<AVOption > av_device_capabilities() {
		try {
			return (Pointer<AVOption >)BridJ.getNativeLibrary("avdevice").getSymbolPointer("av_device_capabilities").as(DefaultParameterizedType.paramType(Pointer.class, AVOption.class)).get();
		}catch (Throwable $ex$) {
			throw new RuntimeException($ex$);
		}
	}
	/**
	 * AVOption table used by devices to implement device capabilities API. Should not be used by a user.<br>
	 * C type : extern const AVOption[]
	 */
	public AvdeviceLibrary av_device_capabilities(Pointer<AVOption > av_device_capabilities) {
		try {
			{
				BridJ.getNativeLibrary("avdevice").getSymbolPointer("av_device_capabilities").as(DefaultParameterizedType.paramType(Pointer.class, AVOption.class)).set(av_device_capabilities);
				return this;
			}
		}catch (Throwable $ex$) {
			throw new RuntimeException($ex$);
		}
	}
	/** Undefined type */
	public static interface AVDictionary {
		
	};
}