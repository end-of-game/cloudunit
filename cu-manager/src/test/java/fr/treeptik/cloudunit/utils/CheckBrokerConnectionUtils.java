package fr.treeptik.cloudunit.utils;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * Created by guillaume on 01/10/16.
 */
@Component
public class CheckBrokerConnectionUtils {

    public String checkActiveMQJMSProtocol(String messageAsString,
                                           String url) throws JMSException {
        ActiveMQConnectionFactory activeMQConnectionFactory =
                new ActiveMQConnectionFactory(String.format("tcp://%s",url));
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

}
