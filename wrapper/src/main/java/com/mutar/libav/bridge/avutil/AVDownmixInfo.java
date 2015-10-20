package com.mutar.libav.bridge.avutil;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVDownmixType;
import org.bridj.BridJ;
import org.bridj.IntValuedEnum;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * <i>native declaration : ffmpeg_build/include/libavutil/downmix_info.h:22</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("avutil") 
public class AVDownmixInfo extends StructObject {
	static {
		BridJ.register();
	}
	/** C type : AVDownmixType */
	@Field(0) 
	public IntValuedEnum<AVDownmixType > preferred_downmix_type() {
		return this.io.getEnumField(this, 0);
	}
	/** C type : AVDownmixType */
	@Field(0) 
	public AVDownmixInfo preferred_downmix_type(IntValuedEnum<AVDownmixType > preferred_downmix_type) {
		this.io.setEnumField(this, 0, preferred_downmix_type);
		return this;
	}
	@Field(1) 
	public double center_mix_level() {
		return this.io.getDoubleField(this, 1);
	}
	@Field(1) 
	public AVDownmixInfo center_mix_level(double center_mix_level) {
		this.io.setDoubleField(this, 1, center_mix_level);
		return this;
	}
	@Field(2) 
	public double center_mix_level_ltrt() {
		return this.io.getDoubleField(this, 2);
	}
	@Field(2) 
	public AVDownmixInfo center_mix_level_ltrt(double center_mix_level_ltrt) {
		this.io.setDoubleField(this, 2, center_mix_level_ltrt);
		return this;
	}
	@Field(3) 
	public double surround_mix_level() {
		return this.io.getDoubleField(this, 3);
	}
	@Field(3) 
	public AVDownmixInfo surround_mix_level(double surround_mix_level) {
		this.io.setDoubleField(this, 3, surround_mix_level);
		return this;
	}
	@Field(4) 
	public double surround_mix_level_ltrt() {
		return this.io.getDoubleField(this, 4);
	}
	@Field(4) 
	public AVDownmixInfo surround_mix_level_ltrt(double surround_mix_level_ltrt) {
		this.io.setDoubleField(this, 4, surround_mix_level_ltrt);
		return this;
	}
	@Field(5) 
	public double lfe_mix_level() {
		return this.io.getDoubleField(this, 5);
	}
	@Field(5) 
	public AVDownmixInfo lfe_mix_level(double lfe_mix_level) {
		this.io.setDoubleField(this, 5, lfe_mix_level);
		return this;
	}
	public AVDownmixInfo() {
		super();
	}
	public AVDownmixInfo(Pointer pointer) {
		super(pointer);
	}
}
