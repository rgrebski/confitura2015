package com.grebski.cache.bytebuffer;

import com.grebski.cache.ShortArrayOffHeap;
import com.grebski.cache.util.UnsafeUtils;
import org.testng.annotations.*;

import static org.assertj.core.api.Assertions.assertThat;

public class UnsafeTest {

    //2GB of native memory needed to run this test
    @Test
    public void testHugeArray() throws Exception {
        //given
        long maxIntPlus1 = Integer.MAX_VALUE + 1L;
        ShortArrayOffHeap longArray = new ShortArrayOffHeap(maxIntPlus1);

        //when
        longArray.putShortAt(0, Short.MIN_VALUE);
        longArray.putShortAt(Integer.MAX_VALUE + 1L, Short.MAX_VALUE);

        //then
        assertThat(longArray.getShortAt(0)).isEqualTo(Short.MIN_VALUE);
        assertThat(longArray.getShortAt(Integer.MAX_VALUE + 1L)).isEqualTo(Short.MAX_VALUE);
        longArray.destroy(); //free memory
    }

    @Test
    public void testUnsafeExceedAllocatedMemory() {
        UnsafeUtils.createUnsafe().allocateMemory(Long.MAX_VALUE);
    }
}
