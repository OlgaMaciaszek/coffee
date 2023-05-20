package io.spring.barcelona.coffee.barista;

import io.spring.barcelona.coffee.barista.orders.Order;
import io.spring.barcelona.coffee.barista.orders.OrderEntry;
import io.spring.barcelona.coffee.barista.service.KafkaHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.verifier.converter.YamlContract;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierReceiver;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Olga Maciaszek-Sharma
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {BaristaApplication.class, BaseTestClass.TestConfig.class})
@Testcontainers
@AutoConfigureMessageVerifier
@ActiveProfiles("contracts")
public abstract class BaseTestClass {

    @Autowired
    KafkaHandler kafkaHandler;

    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));

    public void triggerServing() {
        Order order = new Order();
        order.add(new OrderEntry("latte", 1));
        order.add(new OrderEntry("v60", 2));
        kafkaHandler.process(order);
    }

    public void triggerError() {
        Order order = new Order();
        order.add(new OrderEntry("expresso", 1));
        kafkaHandler.process(order);
    }

    @Configuration
    @EnableKafka
    static class TestConfig {

        @Bean
        KafkaMessageVerifier kafkaTemplateMessageVerifier() {
            return new KafkaMessageVerifier();
        }

        @Bean
        @Primary
        JsonMessageConverter noopMessageConverter() {
            return new NoopJsonMessageConverter();
        }

    }

    static class KafkaMessageVerifier implements MessageVerifierReceiver<Message<?>> {

        private static final Log LOG = LogFactory.getLog(KafkaMessageVerifier.class);

        Map<String, BlockingQueue<Message<?>>> broker = new ConcurrentHashMap<>();


        @Override
        public Message receive(String destination, long timeout, TimeUnit timeUnit, @Nullable YamlContract contract) {
            broker.putIfAbsent(destination, new ArrayBlockingQueue<>(1));
            BlockingQueue<Message<?>> messageQueue = broker.get(destination);
            Message<?> message;
            try {
                message = messageQueue.poll(timeout, timeUnit);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (message != null) {
                LOG.info("Removed a message from a topic [" + destination + "]");
                LOG.info(message.getPayload().toString());
            }
            return message;
        }


        @KafkaListener(id = "baristaContractTestListener", topics = {"errors", "servings"})
        public void listen(ConsumerRecord payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
            LOG.info("Got a message from a topic [" + topic + "]");
            Map<String, Object> headers = new HashMap<>();
            new DefaultKafkaHeaderMapper().toHeaders(payload.headers(), headers);
            broker.putIfAbsent(topic, new ArrayBlockingQueue<>(1));
            BlockingQueue<Message<?>> messageQueue = broker.get(topic);
            messageQueue.add(MessageBuilder.createMessage(payload.value(), new MessageHeaders(headers)));
        }

        @Override
        public Message receive(String destination, YamlContract contract) {
            return receive(destination, 15, TimeUnit.SECONDS, contract);
        }

    }
}

class NoopJsonMessageConverter extends JsonMessageConverter {

    NoopJsonMessageConverter() {
    }

    @Override
    protected Object convertPayload(Message<?> message) {
        return message.getPayload();
    }
}
