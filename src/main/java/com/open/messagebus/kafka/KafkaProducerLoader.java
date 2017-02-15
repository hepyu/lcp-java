package com.open.messagebus.kafka;

public class KafkaProducerLoader {

	public static KafkaProducerX<String, String> loadKafkaProducerX(String source) {
		return KafkaProducerXFactory.getKafkaProducerX(source);
	}

}
