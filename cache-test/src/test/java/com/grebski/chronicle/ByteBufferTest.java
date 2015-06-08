package com.grebski.chronicle;

import com.google.common.collect.ImmutableList;
import com.grebski.cache.util.ByteBufferUtils;
import com.grebski.cache.util.ByteUtil;
import com.grebski.cache.util.JvmUtils;
import org.testng.annotations.*;

import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test of this class should be run separately with different JVM args
 */
public class ByteBufferTest {


    @Test
    public void testAllocateDirect2GB() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        JvmUtils.verifyJvmArgumentsPresent(ImmutableList.of("-Xmx64m", "-XX:MaxDirectMemorySize=2g"));

        long capacity2GB = (2 * ByteUtil.GB) - 1;
        assertThat(capacity2GB).isLessThanOrEqualTo(Integer.MAX_VALUE);

        //after this line process memory grows up to 2GB+
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect((int) capacity2GB);

        //perform "GC" on created byte buffer
        ByteBufferUtils.callCleaner(byteBuffer);
    }

    @Test
    public void testAllocateDirectBiggerThanMaxDirectMemorySize() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        JvmUtils.verifyJvmArgumentsPresent(ImmutableList.of("-Xmx64m"));
        JvmUtils.verifyJvmArgumentsNotPresent(ImmutableList.of("-XX:MaxDirectMemorySize"));

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect((int) (ByteUtil.GB) + 1); //OutOfMemoryError: Direct buffer memory
    }

    @Test
    public void testAllocateBiggerThanHeap() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        JvmUtils.verifyJvmArgumentsPresent(ImmutableList.of("-Xmx64m", "-XX:MaxDirectMemorySize=2g"));

        ByteBuffer byteBuffer = ByteBuffer.allocate((int) (64 * ByteUtil.MB)); //OutOfMemoryError: Java heap space
    }
}
