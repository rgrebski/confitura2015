package com.grebski.cache;

import com.grebski.cache.util.UnsafeUtils;
import sun.misc.Unsafe;

/**
 * array of Shorts that holds its data off heap
 */
public class ShortArrayOffHeap {
    private final long size;
    private final long startAddress;
    private final short SHORT_LENGTH_IN_BYTES = 2;

    private final Unsafe unsafe = UnsafeUtils.createUnsafe();

    public ShortArrayOffHeap(long size) {
        this.size = size;
        long allocationSize = size * SHORT_LENGTH_IN_BYTES;
        startAddress = unsafe.allocateMemory(allocationSize);
        unsafe.setMemory(startAddress, allocationSize, (byte) 0);
    }

    public void putShortAt(long index, short value) {
        verifyIndexNotExceedsSize(index);
        unsafe.putShort(startAddress + (index * SHORT_LENGTH_IN_BYTES), value);
    }

    public short getShortAt(long index) {
        verifyIndexNotExceedsSize(index);
        return unsafe.getShort(startAddress + (index * SHORT_LENGTH_IN_BYTES));
    }

    public void destroy() {
        unsafe.freeMemory(startAddress);
    }

    private void verifyIndexNotExceedsSize(long index) {
        if (index > size) {
            throw new IndexOutOfBoundsException();
        }
    }
}
