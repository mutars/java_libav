package com.mutar.libav.bridge.avdevice;
import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * <i>native declaration : ffmpeg_build/include/libavdevice/avdevice.h:58</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("avdevice") 
public class AVDeviceRect extends StructObject {
	static {
		BridJ.register();
	}
	/** < x coordinate of top left corner */
	@Field(0) 
	public int x() {
		return this.io.getIntField(this, 0);
	}
	/** < x coordinate of top left corner */
	@Field(0) 
	public AVDeviceRect x(int x) {
		this.io.setIntField(this, 0, x);
		return this;
	}
	/** < y coordinate of top left corner */
	@Field(1) 
	public int y() {
		return this.io.getIntField(this, 1);
	}
	/** < y coordinate of top left corner */
	@Field(1) 
	public AVDeviceRect y(int y) {
		this.io.setIntField(this, 1, y);
		return this;
	}
	/** < width */
	@Field(2) 
	public int width() {
		return this.io.getIntField(this, 2);
	}
	/** < width */
	@Field(2) 
	public AVDeviceRect width(int width) {
		this.io.setIntField(this, 2, width);
		return this;
	}
	/** < height */
	@Field(3) 
	public int height() {
		return this.io.getIntField(this, 3);
	}
	/** < height */
	@Field(3) 
	public AVDeviceRect height(int height) {
		this.io.setIntField(this, 3, height);
		return this;
	}
	public AVDeviceRect() {
		super();
	}
	public AVDeviceRect(Pointer pointer) {
		super(pointer);
	}
}
