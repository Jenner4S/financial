package com.zhou.seller;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HazelcastMapTest {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    //@PostConstruct
    public void put(){
        Map map = hazelcastInstance.getMap("zhou");
        map.put("name","imooc");
    }
}
