package com.grebski.redis.test;

import org.testng.annotations.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;

@Test()
public class RedisTopicTest {

    public static final String CHANNEL_NAME = "channelX";
    public static final int MESSAGE_NUMBER_TO_PRODUCE = 10;
    private Jedis jedis;
    private LinkedList<String> consumedMessages = new LinkedList<>();
    private Jedis jedis2;

    @BeforeClass
    public void beforeClass() {
        jedis = new Jedis("localhost");
        jedis2 = new Jedis("localhost");
    }

    @AfterClass
    public void afterClass() {
        jedis = new Jedis("localhost");
        jedis.flushAll();
    }

    @Test(timeOut = 2000) //fail test after 2s
    public void testProducerConsumer() throws InterruptedException {
        runConsumerInNewThread();
        Thread.sleep(500); //wait for consumer to subscribe
        runProducerInNewThread();

        while (consumedMessages.size() < MESSAGE_NUMBER_TO_PRODUCE) {
            Thread.sleep(100);
        }

        assertThat(consumedMessages)
                .hasSize(MESSAGE_NUMBER_TO_PRODUCE)
                .contains("message_0")
                .contains("message_" + (MESSAGE_NUMBER_TO_PRODUCE - 1));
    }

    private void runProducerInNewThread() {
        Thread producer = new Thread(() -> runProducer());
        producer.start();
    }

    private void runConsumerInNewThread() throws InterruptedException {
        Thread thread = new Thread(() -> jedis2.subscribe(jedisConsume(), CHANNEL_NAME));
        thread.start();
    }

    private JedisPubSub jedisConsume() {
        return new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                consumedMessages.add(message);
                super.onMessage(channel, message);
            }
        };
    }

    private void runProducer() {
        for (int i = 0; i < MESSAGE_NUMBER_TO_PRODUCE; i++) {
            jedis.publish(CHANNEL_NAME, "message_" + i);
        }
    }

}
