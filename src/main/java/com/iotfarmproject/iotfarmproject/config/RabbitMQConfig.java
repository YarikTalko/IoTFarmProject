package com.iotfarmproject.iotfarmproject.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.sensors.queue.name}")
    private String sensorsQueue;

    @Value("${rabbitmq.equipment.queue.name}")
    private String equipmentQueue;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.sensors.routing_key.name}")
    private String sensorsRoutingKey;

    @Value("${rabbitmq.equipment.routing_key.name}")
    private String equipmentRoutingKey;

    @Bean
    public Queue sensorDataQueue() {
        return new Queue(sensorsQueue);
    }

    @Bean
    public Queue equipmentQueue() {
        return new Queue(equipmentQueue);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding sensorsBinding() {
        return BindingBuilder
                .bind(sensorDataQueue())
                .to(exchange())
                .with(sensorsRoutingKey);
    }

    @Bean
    public Binding equipmentBinding() {
        return BindingBuilder
                .bind(equipmentQueue())
                .to(exchange())
                .with(equipmentRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
