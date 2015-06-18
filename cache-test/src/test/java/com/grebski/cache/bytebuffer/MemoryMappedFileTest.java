package com.grebski.cache.bytebuffer;

import com.grebski.cache.util.ByteUtil;
import org.testng.annotations.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Memory mapped file test
 */
public class MemoryMappedFileTest {

    public static final long LONG_SIZE_IN_BYTES = (8 * ByteUtil.KB);
    private AtomicInteger producerConsumerSwitch = new AtomicInteger();

    @Test
    public void memoryMappedFileWriter() throws IOException, InterruptedException {
        File mappedFile = new File("/tmp/mappedFile.tmp");
        mappedFile.delete();

        FileChannel fileChannel = new RandomAccessFile(mappedFile, "rw").getChannel();

        long bufferSize = 1000 * LONG_SIZE_IN_BYTES;
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize);

        long startAddress = 0;
        long elementsToPut = 200_000_000;

        for (int counter = 0; counter < elementsToPut; counter++) {
            if (!mappedByteBuffer.hasRemaining()) {
                startAddress += mappedByteBuffer.position();
                mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startAddress, bufferSize);
            }

            mappedByteBuffer.putLong(counter);
        }
    }

    @Test
    public void writeLongToFile() throws IOException {
        File f = new File("/tmp/longObject.tmp");
        f.delete();

        Long longObj = 1L;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream objOutputStream = new ObjectOutputStream(new FileOutputStream(f))){
            objOutputStream.writeLong(longObj);
        }
    }
    @Test
    public void writeLongAsObjectToFile() throws IOException {
        File f = new File("/tmp/longObject.tmp");
        f.delete();

        Long longObj = 1L;
        FileOutputStream out = new FileOutputStream(f);
        try (ObjectOutputStream objOutputStream = new ObjectOutputStream(out)){
            objOutputStream.writeLong(longObj);
            objOutputStream.writeLong(longObj);
        }

    }
}
