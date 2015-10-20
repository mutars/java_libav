package com.mutar.libav.bridge.avutil;
import com.mutar.libav.bridge.avutil.AvutilLibrary.AVStereo3DType;
import org.bridj.BridJ;
import org.bridj.IntValuedEnum;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * <i>native declaration : ffmpeg_build/include/libavutil/stereo3d.h:22</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("avutil") 
public class AVStereo3D extends StructObject {
	static {
		BridJ.register();
	}
	/** C type : AVStereo3DType */
	@Field(0) 
	public IntValuedEnum<AVStereo3DType > type() {
		return this.io.getEnumField(this, 0);
	}
	/** C type : AVStereo3DType */
	@Field(0) 
	public AVStereo3D type(IntValuedEnum<AVStereo3DType > type) {
		this.io.setEnumField(this, 0, type);
		return this;
	}
	@Field(1) 
	public int flags() {
		return this.io.getIntField(this, 1);
	}
	@Field(1) 
	public AVStereo3D flags(int flags) {
		this.io.setIntField(this, 1, flags);
		return this;
	}
	public AVStereo3D() {
		super();
	}
	public AVStereo3D(Pointer pointer) {
		super(pointer);
	}
}
