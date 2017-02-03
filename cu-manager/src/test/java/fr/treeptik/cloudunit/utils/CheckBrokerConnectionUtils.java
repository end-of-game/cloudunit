package fr.treeptik.cloudunit.utils;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.net.URL;

/**
 * Created by guillaume on 01/10/16.
 */
@Component
public class CheckBrokerConnectionUtils {

    public String checkActiveMQJMSProtocol(String messageAsString,
                                           String url) throws JMSException {
        ActiveMQConnectionFactory activeMQConnectionFactory =
                new ActiveMQConnectionFactory(String.format("tcp://%s", url));
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(activeMQConnectionFactory);
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        Destination destination = new ActiveMQQueue("myQueue");
        jmsTemplate.setDefaultDestination(destination);
        jmsTemplate.send(destination, t -> {
            TextMessage message = t.createTextMessage(messageAsString);
            return message;
        });
        TextMessage receivedMessage = (TextMessage) jmsTemplate.receive(destination);
        return receivedMessage.getText();
    }

    public String checkRabbitMQAMQPProtocol(String messageAsString,
                                            String url,
                                            String user,
                                            String password,
                                            String vhost) throws Exception {
        org.springframework.amqp.rabbit.connection.CachingConnectionFactory cf =
                new org.springframework.amqp.rabbit.connection.CachingConnectionFactory();
        URL formattedUrl = new URL("http://" + url);
        cf.setHost(formattedUrl.getHost());
        cf.setPort(formattedUrl.getPort());
        cf.setUsername(user);
        cf.setPassword(password);
        cf.setVirtualHost(vhost);
        RabbitAdmin admin = new RabbitAdmin(cf);
        org.springframework.amqp.core.Queue queue = new org.springframework.amqp.core.Queue("myQueue");
        admin.declareQueue(queue);
        TopicExchange exchange = new TopicExchange("myExchange");
        admin.declareExchange(exchange);
        admin.declareBinding(
                BindingBuilder.bind(queue).to(exchange).with("foo.*"));
        RabbitTemplate template = new RabbitTemplate(cf);
        template.convertAndSend("myExchange", "foo.bar", messageAsString);
        String receivedMessage = template.receiveAndConvert("myQueue").toString();
        return receivedMessage;
    }

}
