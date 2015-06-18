package com.grebski.chronicle;

import com.grebski.cache.model.OffHeapUser;
import com.grebski.cache.model.OffHeapUserSample;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;
import org.testng.annotations.*;

import java.text.NumberFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by rgrebski on 07.06.15.
 */
public class ChronicleMapWriteTest {
    public static final String KEY_SAMPLE = "myKeyShouldNotBeLongerThanThisString";
    private static final Logger log = LoggerFactory.getLogger(ChronicleMapWriteTest.class);

    private static final int MAX_ENTRIES = 10_000_000;
    public static final long YEAR_IN_MILLIS = ChronoUnit.YEARS.getDuration().toMillis();
    private NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    private ChronicleMap<String, OffHeapUser> chronicleMap;


    {
        chronicleMap = ChronicleMapBuilder.of(String.class, OffHeapUser.class)
                .constantKeySizeBySample(KEY_SAMPLE)
                .constantValueSizeBySample(new OffHeapUserSample())
                .entries((long) (MAX_ENTRIES * 1.2))
                .create();

    }

    @Test
    public void testChronicleMapWritePerformance() throws InterruptedException {
        Thread thread = new Thread(() -> addHalfOfEntriesToChronicleMap());
        thread.start();

        addHalfOfEntriesToChronicleMap();
        thread.join();
    }

    @Test(dependsOnMethods = "testChronicleMap")
    public void iterateChronicleMapWithoutDeserializingFields(){
        for (OffHeapUser offHeapUser : chronicleMap.values()) {
        }
    }

    @Test(dependsOnMethods = "testChronicleMap")
    public void iterateChronicleMapWithDeserializingFields(){
        for (OffHeapUser offHeapUser : chronicleMap.values()) {
            offHeapUser.getAccountValidUntil();
            offHeapUser.getUsername();
            offHeapUser.getRoleAt(0);
            offHeapUser.getRoleAt(1);
        }
    }

    @Test
    public void testSinglePut(){
        //given
        long accountValidUntil = System.currentTimeMillis() + YEAR_IN_MILLIS;
        String username = RandomStringUtils.randomAlphabetic(20);

        OffHeapUser offHeapUser = chronicleMap.newValueInstance();
        offHeapUser.setAccountValidUntil(accountValidUntil);
        offHeapUser.setUsername(username);

        OffHeapUser.Role role0 = offHeapUser.getRoleAt(0);
        role0.setRole("Role0");
        OffHeapUser.Role role1 = offHeapUser.getRoleAt(1);
        role0.setRole("Role1");

        //when
        chronicleMap.put("test", offHeapUser);

        //then
        OffHeapUser offHeapUserActual = chronicleMap.get("test");
        assertThat(offHeapUserActual).isNotNull();
        assertThat(offHeapUserActual.getUsername()).isEqualTo(username);
        assertThat(offHeapUserActual.getAccountValidUntil()).isEqualTo(accountValidUntil);
        assertThat(offHeapUserActual.getRoleAt(0).getRole()).isEqualTo("Role0");
        assertThat(offHeapUserActual.getRoleAt(0).getRole()).isEqualTo("Role1");
    }

    private void addHalfOfEntriesToChronicleMap() {
        for(int i=0;i<MAX_ENTRIES/2; i++){
            OffHeapUser user = chronicleMap.newValueInstance();
            user.setAccountValidUntil(System.currentTimeMillis() + YEAR_IN_MILLIS);
            user.getRoleAt(0).setRole("Role1");
            user.getRoleAt(1).setRole("Role2");
            user.setUsername(RandomStringUtils.randomAlphabetic(20));

            String key = RandomStringUtils.randomAlphabetic(KEY_SAMPLE.length());
            chronicleMap.put(key, user);
        }
    }

    @AfterMethod
    public void getRunTime(ITestResult tr) {
        long durationInMillis = tr.getEndMillis() - tr.getStartMillis();
        String methodName = tr.getMethod().getMethodName();

        log.debug("Method {} took {}s, chronicleMap size: {}, Ops/s: {}",
                methodName,
                Duration.ofMillis(durationInMillis),
                numberFormat.format(chronicleMap.size()),
                getOpsPerSecond(durationInMillis));
    }

    private String getOpsPerSecond(long durationInMillis) {
        long secondInMillis = ChronoUnit.SECONDS.getDuration().toMillis();
        double opsPerSecond = ((double) chronicleMap.size() / durationInMillis) * secondInMillis;

        return numberFormat.format(opsPerSecond);
    }
}
