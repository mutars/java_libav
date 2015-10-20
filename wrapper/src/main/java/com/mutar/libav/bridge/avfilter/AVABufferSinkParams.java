package com.mutar.libav.bridge.avfilter;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVSampleFormat;
import org.bridj.BridJ;
import org.bridj.IntValuedEnum;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * <i>native declaration : ffmpeg_build/include/libavfilter/buffersink.h:37</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("avfilter") 
public class AVABufferSinkParams extends StructObject {
	static {
		BridJ.register();
	}
	/**
	 * < list of allowed sample formats, terminated by AV_SAMPLE_FMT_NONE<br>
	 * C type : AVSampleFormat*
	 */
	@Field(0) 
	public Pointer<IntValuedEnum<AVSampleFormat > > sample_fmts() {
		return this.io.getPointerField(this, 0);
	}
	/**
	 * < list of allowed sample formats, terminated by AV_SAMPLE_FMT_NONE<br>
	 * C type : AVSampleFormat*
	 */
	@Field(0) 
	public AVABufferSinkParams sample_fmts(Pointer<IntValuedEnum<AVSampleFormat > > sample_fmts) {
		this.io.setPointerField(this, 0, sample_fmts);
		return this;
	}
	/**
	 * < list of allowed channel layouts, terminated by -1<br>
	 * C type : const int64_t*
	 */
	@Field(1) 
	public Pointer<Long > channel_layouts() {
		return this.io.getPointerField(this, 1);
	}
	/**
	 * < list of allowed channel layouts, terminated by -1<br>
	 * C type : const int64_t*
	 */
	@Field(1) 
	public AVABufferSinkParams channel_layouts(Pointer<Long > channel_layouts) {
		this.io.setPointerField(this, 1, channel_layouts);
		return this;
	}
	/**
	 * < list of allowed channel counts, terminated by -1<br>
	 * C type : const int*
	 */
	@Field(2) 
	public Pointer<Integer > channel_counts() {
		return this.io.getPointerField(this, 2);
	}
	/**
	 * < list of allowed channel counts, terminated by -1<br>
	 * C type : const int*
	 */
	@Field(2) 
	public AVABufferSinkParams channel_counts(Pointer<Integer > channel_counts) {
		this.io.setPointerField(this, 2, channel_counts);
		return this;
	}
	/** < if not 0, accept any channel count or layout */
	@Field(3) 
	public int all_channel_counts() {
		return this.io.getIntField(this, 3);
	}
	/** < if not 0, accept any channel count or layout */
	@Field(3) 
	public AVABufferSinkParams all_channel_counts(int all_channel_counts) {
		this.io.setIntField(this, 3, all_channel_counts);
		return this;
	}
	/**
	 * < list of allowed sample rates, terminated by -1<br>
	 * C type : int*
	 */
	@Field(4) 
	public Pointer<Integer > sample_rates() {
		return this.io.getPointerField(this, 4);
	}
	/**
	 * < list of allowed sample rates, terminated by -1<br>
	 * C type : int*
	 */
	@Field(4) 
	public AVABufferSinkParams sample_rates(Pointer<Integer > sample_rates) {
		this.io.setPointerField(this, 4, sample_rates);
		return this;
	}
	public AVABufferSinkParams() {
		super();
	}
	public AVABufferSinkParams(Pointer pointer) {
		super(pointer);
	}
}
