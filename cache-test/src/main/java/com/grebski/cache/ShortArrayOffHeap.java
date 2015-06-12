package com.grebski.cache;

import com.grebski.cache.util.UnsafeUtils;
import sun.misc.Unsafe;

/**
 * array of Shorts that holds its data off heap
 */
public class ShortArrayOffHeap {
    private final long size;
    private final long startAddress;
    private final int SHORT_LENGTH_IN_BYTES = 2;


    private final Unsafe unsafe = UnsafeUtils.createUnsafe();
    private final long endAddress;

    public ShortArrayOffHeap(long size) {
        this.size = size;
        endAddress = size * SHORT_LENGTH_IN_BYTES;
        startAddress = unsafe.allocateMemory(endAddress);
        unsafe.addressSize();
        unsafe.setMemory(startAddress, endAddress, (byte) 0);
    }

    public void putShortAt(long index, short value) {
        verifyIndex(index);
        unsafe.putShort(startAddress + (index * SHORT_LENGTH_IN_BYTES), value);
    }

    private void verifyIndex(long index) {
        if (index > size) {
            throw new IndexOutOfBoundsException();
        }
    }

    public short getShortAt(long index) {
        verifyIndex(index);
        return unsafe.getShort(startAddress + (index * SHORT_LENGTH_IN_BYTES));
    }

    public void destroy() {
        unsafe.freeMemory(startAddress);
    }
}
