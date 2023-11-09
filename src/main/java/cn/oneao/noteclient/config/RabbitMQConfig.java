package cn.oneao.noteclient.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String DIRECT_EXCHANGE = "directexchange";
    public static final String DIRECT_QUEUE = "directqueue";//点赞通知队列
    public static final String ROUTING_KEY = "rabbit.msg";

    public static final String COMMENT_REPLY_QUEUE = "comment_reply_queue";//评论回复邮箱通知队列
    public static final String COMMENT_REPLY_KEY = "comment.reply.key";
    @Bean
    public Queue directQueue1() {
        return new Queue(DIRECT_QUEUE);
    }
    @Bean
    public Queue directQueue2() {
        return new Queue(COMMENT_REPLY_QUEUE);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }
    @Bean
    public Binding binding1() {
        return BindingBuilder.bind(directQueue1()).to(directExchange()).with(ROUTING_KEY);
    }
    @Bean
    public Binding binding2() {
        return BindingBuilder.bind(directQueue2()).to(directExchange()).with(COMMENT_REPLY_KEY);
    }
}
