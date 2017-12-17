package com.open.lcp.message.kafka;

public class KafkaConsumerLoader {

	public static KafkaConsumerX<String, String> loadKafkaConsumerX(String instanceName,
			KafkaMessageExecutor<String, String> executor) {
		return KafkaConsumerXFactory.getKafkaConsumerX(instanceName, executor);
	}

}
