package com.iotfarmproject.iotfarmproject.irrigation_control.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEvent(String message) {
        rabbitTemplate.convertAndSend("irrigation_control_queue", message);
        System.out.println("Event sent: " + message);
    }
}
