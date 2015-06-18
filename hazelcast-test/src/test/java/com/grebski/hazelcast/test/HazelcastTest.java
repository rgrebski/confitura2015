package com.grebski.hazelcast.test;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.assertj.core.api.Assertions;
import org.testng.annotations.*;

import java.util.Map;
import java.util.stream.IntStream;

public class HazelcastTest {

    @Test
    public void hazelcastTest(){
        Config hazelcastConfig = new Config();
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConfig);
        HazelcastInstance hazelcastInstance2 = Hazelcast.newHazelcastInstance(hazelcastConfig);
        Map<String, String> map = hazelcastInstance.getMap("map");

        map.put("test", "test");

        IntStream.range(1, 1_000_000)
                .forEach(index -> {
                    System.out.println(index);
                    map.put(String.valueOf(index), String.valueOf(index));
                });
    }

    @Test
    public void hazelcastClusterTest(){
        Config hazelcastConfig = new Config();
        HazelcastInstance hazelcastInstance1 = Hazelcast.newHazelcastInstance(hazelcastConfig);
        HazelcastInstance hazelcastInstance2 = Hazelcast.newHazelcastInstance(hazelcastConfig);

        Map<String, String> node1Map = hazelcastInstance1.getMap("map");
        Map<String, String> node2Map = hazelcastInstance2.getMap("map");

        node1Map.put("key", "value");
        Assertions.assertThat(node2Map.get("key")).isEqualTo("value");
    }
}
