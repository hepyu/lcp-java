package com.open.lcp.queue.kafka;

public class KafkaProducerLoader {

	public static KafkaProducerX<String, String> loadKafkaProducerX(String source) {
		return KafkaProducerXFactory.getKafkaProducerX(source);
	}

}
