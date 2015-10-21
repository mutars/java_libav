package com.mutar.libav.api.bridge;

import org.bridj.Pointer;
import org.bridj.StructObject;

public abstract class AbstractWrapper<E extends StructObject> implements IWrapper<E> {

    protected E delegate;

    public AbstractWrapper(E obj) {
        this.delegate = obj;
    }

    @Override
    public void clearWrapperCache() {
        //Pointer.release(getPointer());
        //TODO debug it
        //delegate = null;
    }

    @Override
    public Pointer<E> getPointer() {
        return Pointer.getPointer(delegate);
    }

    public E getDelegate() {
        return delegate;
    }

}
