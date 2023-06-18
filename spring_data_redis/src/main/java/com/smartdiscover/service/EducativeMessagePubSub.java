package com.smartdiscover.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class EducativeMessagePubSub implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(EducativeMessagePubSub.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void publishMessage(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Message received: " + message.toString());
    }
}
