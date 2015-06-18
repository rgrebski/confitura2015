package com.grebski.redis.test;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;
import redis.clients.jedis.Jedis;

import java.text.NumberFormat;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Redis tests
 */
public class RedisTest {
    private static Logger log = LoggerFactory.getLogger(RedisTest.class);

    private String key;
    private String value;
    private Jedis jedis;
    private NumberFormat numberFormat = NumberFormat.getNumberInstance();

    @BeforeClass
    public void setUp() {
        key = RandomStringUtils.randomAlphabetic(5);
        value = RandomStringUtils.randomAlphabetic(5);

        Jedis jedis = new Jedis("localhost");
        jedis.flushAll();
        jedis.close();
    }

    @AfterClass
    public void tearDown() {
        Jedis jedis = new Jedis("localhost");
        jedis.flushAll();
        jedis.close();
    }


    @BeforeMethod
    public void beforeMethod() {
        jedis = new Jedis("localhost");
    }

    @AfterMethod
    public void afterMethod() {
        jedis.close();
    }

    @Test
    public void smokeTest_redisInsertData() {
        jedis.set(key, value);
        jedis.close();
    }

    @Test(dependsOnMethods = "redisInsertData")
    public void smokeTest_redisReadData() {
        assertThat(jedis.get(key)).isEqualTo(value + " ");
    }

    @Test
    public void userSuccessfulLoginSimulation() {
        //login
        User user = User.builder()
                .login("rgrebski")
                .id(1234)
                .email("rgrebski@gmail.com")
                .build();

        String userKey = "user:" + user.getId(); //user:1234

        jedis.hset(userKey, "login", user.getLogin());
        jedis.hset(userKey, "email", user.getEmail());

        assertThat(jedis.hget(userKey, "email")).isEqualTo("rgrebski@gmail.com");
        assertThat(jedis.hgetAll(userKey)).hasSize(2);

        //logout
        Set<String> keys = jedis.hkeys(userKey);
        jedis.hdel(userKey, (String[]) keys.stream().toArray(size -> new String[size]));

        //no data in cache after logout
        assertThat(jedis.hgetAll(userKey)).isEmpty();
    }

    @Test(invocationCount = 5)
    public void userUnsuccessfulLoginSimulation() {
        //asume user has just logged in and we retrieved his data somehow
        User user = User.builder()
                .login("rgrebski") //user enters login
                .id(1234) //id is retrieved from db
                .build();

        //settings
        String userKey = "user:" + user.getId(); //user:1234
        String failedLoginsMapKey = "failedLogins";
        int failedLoginlimit = 3;

        Long failedLogins = jedis.hincrBy(userKey, failedLoginsMapKey, 1);

        if (failedLogins > failedLoginlimit) {
            throw new RuntimeException(
                    String.format("Failed logins limit exceeded [Current: %s, Limit: %s",
                            failedLogins, failedLoginlimit));
        }
    }

}
