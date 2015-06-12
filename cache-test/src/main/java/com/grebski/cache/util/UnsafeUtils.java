package com.grebski.cache.util;

import sun.misc.Unsafe;

import java.lang.reflect.Constructor;

/**
 * Unsafe utilities
 */
public class UnsafeUtils {
    /**
     * Creates unsafe instance
     *
     * @return
     * @throws Exception
     */
    public static Unsafe createUnsafe() {
        try {
            Constructor<Unsafe> unsafeConstructor = Unsafe.class.getDeclaredConstructor();
            unsafeConstructor.setAccessible(true);
            return unsafeConstructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
