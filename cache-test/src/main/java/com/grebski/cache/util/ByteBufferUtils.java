package com.grebski.cache.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

/**
 * {@link ByteBuffer} utils
 */
public class ByteBufferUtils {

    /**
     * Perform "GC" of byte buffer
     * @param byteBuffer byte buffer to be "Garbage Collected"
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void callCleaner(ByteBuffer byteBuffer) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method cleanerMethod = byteBuffer.getClass().getMethod("cleaner");
        cleanerMethod.setAccessible(true);

        Object cleaner = cleanerMethod.invoke(byteBuffer);

        Method cleanMethod = cleaner.getClass().getMethod("clean");
        cleanMethod.setAccessible(true);
        cleanMethod.invoke(cleaner);
    }
}
