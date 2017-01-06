package com.open.lcp.framework.core.configuration;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.open.env.finder.ZKFinder;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

@Configuration
public class KafkaClusterConfig {
	public static final String SE = "kafka.serializer.StringEncoder";

	//@Value("${metadata.broker.list.producer}")
	private String producers;

	//@Value("${request.required.acks}")
	private String ack;

	//@Value("${zookeeper.session.timeout.ms}")
	private int zkTimeOut;

	//@Value("${producer.type}")
	private String type;

	//@Value("${batch.num.messages}")
	private String batchNum;

	//@Value("${metadata.broker.list.consumer}")
	private String consumers;

	//@Value("${metadata.broker.list.data}")
	private String dataKafka;

	private Properties createProducerProp() {
		Properties props = new Properties();

		props.put("metadata.broker.list", producers);// kafka的地址和端口
		props.put("serializer.class", SE);// 配置value的序列化类
		props.put("key.serializer.class", SE);// 配置key的序列化类
		props.put("request.required.acks", ack);
		props.put("zookeeper.session.timeout.ms", zkTimeOut);
		props.put("producer.type", type);
		props.put("batch.num.messages", batchNum);

		return props;
	}

	private Properties createConsumerProp() {
		Properties props = new Properties();

		props.put("metadata.broker.list", consumers);// kafka的地址和端口
		props.put("group.id", "group-vif");// 信息流
		props.put("serializer.class", SE);// 配置value的序列化类
		props.put("key.serializer.class", SE);// 配置key的序列化类
		props.put("request.required.acks", ack);
		props.put("zookeeper.connect", ZKFinder.findZKHosts());

		return props;
	}

	/**
	 * 给数据发kafka消息使用
	 * 
	 * @return
	 */
	private Properties createProductDataProp() {
		Properties props = new Properties();

		props.put("metadata.broker.list", dataKafka);// kafka的地址和端口
		props.put("serializer.class", SE);// 配置value的序列化类
		props.put("key.serializer.class", SE);// 配置key的序列化类
		props.put("request.required.acks", ack);
		props.put("zookeeper.session.timeout.ms", zkTimeOut);
		props.put("producer.type", type);
		props.put("batch.num.messages", batchNum);

		return props;
	}

	/*
	 * private Properties createProductUploadVideoProp() { Properties props =
	 * new Properties();
	 * 
	 * props.put("metadata.broker.list", consumers);//kafka的地址和端口
	 * props.put("serializer.class", SE);//配置value的序列化类
	 * props.put("key.serializer.class", SE);//配置key的序列化类
	 * props.put("request.required.acks", ack);
	 * props.put("zookeeper.session.timeout.ms", zkTimeOut);
	 * props.put("producer.type", type); props.put("batch.num.messages",
	 * batchNum);
	 * 
	 * return props; }
	 */

	/*
	 * private Properties createConsumerUploadVideoProp() { Properties props =
	 * new Properties(); //props.put("zookeeper.connect",
	 * "192.168.226.123:2181"); //props.put("metadata.broker.list",
	 * consumers);//kafka的地址和端口 props.put("group.id", "group-follow-feed");//信息流
	 * props.put("serializer.class", SE);//配置value的序列化类
	 * props.put("key.serializer.class", SE);//配置key的序列化类
	 * props.put("request.required.acks", ack); props.put("zookeeper.connect",
	 * XunleiEnvFinder.getZkEndponts());
	 * 
	 * return props; }
	 */

	/*
	 * @Bean public Producer<String, String> kafkaUpLoadVideoProducer() { return
	 * new Producer<String, String>(new
	 * ProducerConfig(createProductUploadVideoProp())); }
	 * 
	 * @Bean public ConsumerConnector kafkaUpLoadVideoConsumer() { return
	 * Consumer.createJavaConsumerConnector(new
	 * ConsumerConfig(createConsumerUploadVideoProp())); }
	 */

	@Bean
	public Producer<String, String> kafkaDataProducer() {
		return new Producer<String, String>(new ProducerConfig(createProductDataProp()));
	}

	@Bean
	public Producer<String, String> kafkaProducer() {
		return new Producer<String, String>(new ProducerConfig(createProducerProp()));
	}

	@Bean
	public ConsumerConnector kafkaConsumer() {
		return Consumer.createJavaConsumerConnector(new ConsumerConfig(createConsumerProp()));
	}

	public static final void main(String args[]) {
		Properties props = new Properties();
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("metadata.broker.list", "data-center:9192,data-center:9292,data-center:9392");
		props.put("request.required.acks", "1");
		ProducerConfig config = new ProducerConfig(props);
		Producer<String, String> producer = new Producer<String, String>(config);

		// 单个发送
		for (int i = 0; i <= 100; i++) {
			KeyedMessage<String, String> message = new KeyedMessage<String, String>("test", i + "", "Message" + i);
			producer.send(message);
			System.out.println(i);
		}
	}
}
