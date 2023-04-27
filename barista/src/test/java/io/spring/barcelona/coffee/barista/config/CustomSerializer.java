package io.spring.barcelona.coffee.barista.config;

import java.nio.charset.Charset;

import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * @author Olga Maciaszek-Sharma
 */
public class CustomSerializer<T> extends JsonSerializer<T> {

	private static final String ORDERS_TOPIC = "orders";

	@Override
	public byte[] serialize(String topic, T data) {
		if (ORDERS_TOPIC.equals(topic)) {
			if (data instanceof String dataString) {
				// using a String stub instead of object as input
				return serializeAsString(topic, dataString);
			}
		}
		return super.serialize(topic, data);
	}

	public byte[] serializeAsString(String topic, String data) {
		if (data == null)
			return null;
		else
			return data.getBytes(Charset.defaultCharset());
	}
}
