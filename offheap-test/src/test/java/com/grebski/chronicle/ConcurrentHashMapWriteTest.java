package com.grebski.chronicle;

import com.grebski.cache.model.OnHeapUser;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;
import org.testng.annotations.*;

import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by rgrebski on 07.06.15.
 */
public class ConcurrentHashMapWriteTest {
    public static final String KEY_SAMPLE = "myKeyShouldNotBeLongerThanThisString";
    private static final int MAX_ENTRIES = 10_000_000;
    private static final int KEY_LENGTH = KEY_SAMPLE.length();
    public static final long YEAR_IN_MILLIS = ChronoUnit.YEARS.getDuration().toMillis();
    private Logger log = LoggerFactory.getLogger(getClass());
    private ConcurrentHashMap<String, OnHeapUser> concurrentHashMap = new ConcurrentHashMap<>();
    private NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);


    @Test
    public void testConcurrentHashMap() throws InterruptedException {
        LocalDateTime startTime = LocalDateTime.now();
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> test(startTime, concurrentHashMap), 0, 1, TimeUnit.SECONDS);

        Thread thread = new Thread(() -> addHalfOfEntriesToConcurrentHashMap());
        thread.start();


        addHalfOfEntriesToConcurrentHashMap();
        thread.join();
    }


    private void test(LocalDateTime startTime, Map map) {
        double secondsRunning = (double) Duration.between(startTime, LocalDateTime.now()).getSeconds();
        double opsPerSecond = map.size() / secondsRunning;
        System.out.println(LocalDateTime.now() + " performance: " + opsPerSecond + "ops/s, map size: " + concurrentHashMap.size());
    }

    private void addHalfOfEntriesToConcurrentHashMap() {
        for(int i=0;i<MAX_ENTRIES/2; i++){
            OnHeapUser user = new OnHeapUser();
            user.setAccountValidUntil(LocalDateTime.now().plusYears(1));
            user.setUsername(RandomStringUtils.randomAlphabetic(3));
            user.setRoles(OnHeapUser.Role.forStrings("Role1", "Role2"));

            String key = RandomStringUtils.randomAlphabetic(KEY_LENGTH);
            concurrentHashMap.put(key, user);
        }
    }


    @AfterMethod
    public void getRunTime(ITestResult tr) {
        long durationInMillis = tr.getEndMillis() - tr.getStartMillis();
        String methodName = tr.getMethod().getMethodName();

        log.debug("Method {} took {}s, chronicleMap size: {}, Ops/s: {}",
                methodName,
                Duration.ofMillis(durationInMillis),
                numberFormat.format(concurrentHashMap.size()),
                getOpsPerSecond(durationInMillis));
    }

    private String getOpsPerSecond(long durationInMillis) {
        long secondInMillis = ChronoUnit.SECONDS.getDuration().toMillis();
        double opsPerSecond = ((double) concurrentHashMap.size() / durationInMillis) * secondInMillis;

        return numberFormat.format(opsPerSecond);
    }
}
