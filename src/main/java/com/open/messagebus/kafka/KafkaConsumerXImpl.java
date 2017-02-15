package com.open.messagebus.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Logger;

public abstract class KafkaConsumerXImpl<K, V> implements KafkaConsumerX<K, V> {

	private static final Logger logger = Logger.getLogger(KafkaConsumerXImpl.class);

	private final KafkaConsumerHolder<K, V> kafkaConsumerHolder;

	KafkaConsumerXImpl(ZKKafkaConsumerConfig cfg) {
		this.kafkaConsumerHolder = new KafkaConsumerHolder<K, V>(cfg);
	}

	KafkaConsumerXImpl(KafkaConsumerHolder<K, V> kafkaConsumerHolder) {
		this.kafkaConsumerHolder = kafkaConsumerHolder;
	}

	KafkaConsumerHolder<K, V> getKafkaConsumerHolder() {
		return this.kafkaConsumerHolder;
	}

	private KafkaConsumer<K, V> kafkaConsumer() {
		return kafkaConsumerHolder.getKafkaConsumer();
	}

	public void start(String topic) {
		KafkaConsumer<K, V> consumer = kafkaConsumer();
		consumer.subscribe(Arrays.asList(topic));
		consumer.seekToBeginning(new ArrayList<TopicPartition>());

		while (true) {
			ConsumerRecords<K, V> records = consumer.poll(1000);
			for (ConsumerRecord<K, V> record : records) {
				logger.warn("fetched from partition " + record.partition() + ", offset: " + record.offset()
						+ ", message: " + record.value());
				doMessage(record);
			}
			// 按分区读取数据
			// for (TopicPartition partition : records.partitions()) {
			// List<ConsumerRecord<String, String>> partitionRecords =
			// records.records(partition);
			// for (ConsumerRecord<String, String> record : partitionRecords) {
			// System.out.println(record.offset() + ": " + record.value());
			// }
			// }

		}
	}

	public abstract void doMessage(ConsumerRecord<K, V> consumerRecord);

}
