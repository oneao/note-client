package cn.oneao.noteclient.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String ROUTING_KEY = "rabbit.msg";
    public static final String DIRECT_EXCHANGE = "directexchange";
    public static final String DIRECT_QUEUE = "directqueue";

    @Bean
    public Queue directQueue() {
        return new Queue(DIRECT_QUEUE);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(directQueue()).to(directExchange()).with(ROUTING_KEY);
    }
}
