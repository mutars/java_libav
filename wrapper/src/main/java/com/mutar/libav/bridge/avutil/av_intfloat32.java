package com.mutar.libav.bridge.avutil;
import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
import org.bridj.ann.Union;
/**
 * <i>native declaration : ffmpeg_build/include/libavutil/intfloat.h:3</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Union 
@Library("avutil") 
public class av_intfloat32 extends StructObject {
	static {
		BridJ.register();
	}
	@Field(0) 
	public int i() {
		return this.io.getIntField(this, 0);
	}
	@Field(0) 
	public av_intfloat32 i(int i) {
		this.io.setIntField(this, 0, i);
		return this;
	}
	@Field(1) 
	public float f() {
		return this.io.getFloatField(this, 1);
	}
	@Field(1) 
	public av_intfloat32 f(float f) {
		this.io.setFloatField(this, 1, f);
		return this;
	}
	public av_intfloat32() {
		super();
	}
	public av_intfloat32(Pointer pointer) {
		super(pointer);
	}
}