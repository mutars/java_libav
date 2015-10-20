package com.mutar.libav.bridge.avcodec;
import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * @brief This structure is used as a callback between the FFmpeg<br>
 * decoder (vd_) and presentation (vo_) module.<br>
 * This is used for defining a video frame containing surface,<br>
 * picture parameter, bitstream information etc which are passed<br>
 * between the FFmpeg decoder and its clients.<br>
 * <i>native declaration : ffmpeg_build/include/libavcodec/vdpau.h:107</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("avcodec") 
public class vdpau_render_state extends StructObject {
	static {
		BridJ.register();
	}
	/**
	 * < Used as rendered surface, never changed.<br>
	 * C type : VdpVideoSurface
	 */
	@Field(0) 
	public int surface() {
		return this.io.getIntField(this, 0);
	}
	/**
	 * < Used as rendered surface, never changed.<br>
	 * C type : VdpVideoSurface
	 */
	@Field(0) 
	public vdpau_render_state surface(int surface) {
		this.io.setIntField(this, 0, surface);
		return this;
	}
	/** < Holds FF_VDPAU_STATE_* values. */
	@Field(1) 
	public int state() {
		return this.io.getIntField(this, 1);
	}
	/** < Holds FF_VDPAU_STATE_* values. */
	@Field(1) 
	public vdpau_render_state state(int state) {
		this.io.setIntField(this, 1, state);
		return this;
	}
	/** C type : AVVDPAUPictureInfo */
	@Field(2) 
	public AVVDPAUPictureInfo info() {
		return this.io.getNativeObjectField(this, 2);
	}
	/** C type : AVVDPAUPictureInfo */
	@Field(2) 
	public vdpau_render_state info(AVVDPAUPictureInfo info) {
		this.io.setNativeObjectField(this, 2, info);
		return this;
	}
	@Field(3) 
	public int bitstream_buffers_allocated() {
		return this.io.getIntField(this, 3);
	}
	@Field(3) 
	public vdpau_render_state bitstream_buffers_allocated(int bitstream_buffers_allocated) {
		this.io.setIntField(this, 3, bitstream_buffers_allocated);
		return this;
	}
	@Field(4) 
	public int bitstream_buffers_used() {
		return this.io.getIntField(this, 4);
	}
	@Field(4) 
	public vdpau_render_state bitstream_buffers_used(int bitstream_buffers_used) {
		this.io.setIntField(this, 4, bitstream_buffers_used);
		return this;
	}
	/**
	 * Failed to convert value bitstream_buffers of type VdpBitstreamBuffer*<br>
	 * C type : VdpBitstreamBuffer*
	 */
	public vdpau_render_state() {
		super();
	}
	public vdpau_render_state(Pointer pointer) {
		super(pointer);
	}
}
