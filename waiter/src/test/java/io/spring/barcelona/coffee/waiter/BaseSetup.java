package io.spring.barcelona.coffee.waiter;

import java.time.Duration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

/**
 * @author Olga Maciaszek-Sharma
 */
public abstract class BaseSetup {

	// wait for the listeners
	@BeforeEach
	void waitTillListenersSetUp() throws InterruptedException {
		Thread.sleep(500);
	}

}
