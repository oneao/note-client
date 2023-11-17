package cn.oneao.noteclient.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfig {

    public static final String DIRECT_EXCHANGE = "directexchange";
    public static final String DIRECT_QUEUE = "directqueue";//点赞通知队列
    public static final String ROUTING_KEY = "rabbit.msg";

    public static final String COMMENT_REPLY_QUEUE = "comment_reply_queue";
    public static final String COMMENT_REPLY_KEY = "comment.reply.key";

    public static final String COMMENT_REPLY_NOTICE_QUEUE = "comment_reply_notice_queue";
    public static final String COMMENT_REPLY_NOTICE_KEY = "comment.reply.notice.key";

    public static final String USER_LEVEL_UP_QUEUE = "user_level_up_queue";
    public static final String USER_LEVEL_UP_KEY = "user.level.up.key";
    @Bean
    public Queue directQueue1() {
        return new Queue(DIRECT_QUEUE);
    }
    @Bean
    public Queue directQueue2() {
        return new Queue(COMMENT_REPLY_QUEUE);
    }
    @Bean
    public Queue directQueue3() {
        return new Queue(COMMENT_REPLY_NOTICE_QUEUE);
    }
    @Bean
    public Queue directQueue4() {
        return new Queue(USER_LEVEL_UP_QUEUE);
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
    @Bean
    public Binding binding3() {
        return BindingBuilder.bind(directQueue3()).to(directExchange()).with(COMMENT_REPLY_NOTICE_KEY);
    }
    @Bean
    public Binding binding4() {
        return BindingBuilder.bind(directQueue4()).to(directExchange()).with(USER_LEVEL_UP_KEY);
    }
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

}
