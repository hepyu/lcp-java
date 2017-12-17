package com.open.lcp.message.kafka;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.open.lcp.core.env.finder.ZKFinder;
import com.open.lcp.core.register.mangocity.zk.ConfigChangeListener;
import com.open.lcp.core.register.mangocity.zk.ConfigChangeSubscriber;
import com.open.lcp.core.register.mangocity.zk.ZkConfigChangeSubscriberImpl;

public class KafkaProducerXFactory {

	private static final Log logger = LogFactory.getLog(KafkaProducerXFactory.class);

	private static final Map<String, KafkaProducerXImpl<String, String>> kafkaxMap = new ConcurrentHashMap<String, KafkaProducerXImpl<String, String>>();

	private static final Object LOCK_OF_NEWPATH = new Object();

	public static KafkaProducerX<String, String> getKafkaProducerX(final String source) {

//		final String ssdbZkRoot = ZKFinder.findSSDBZKRoot();
//		KafkaProducerXImpl<String, String> producer = kafkaxMap.get(source);
//		if (producer == null) {
//			synchronized (LOCK_OF_NEWPATH) {
//				ZkClient zkClient = null;
//				try {
//					zkClient = new ZkClient(ZKFinder.findZKHosts(), 10000, 10000, new ZkSerializer() {
//
//						@Override
//						public byte[] serialize(Object paramObject) throws ZkMarshallingError {
//							return paramObject == null ? null : paramObject.toString().getBytes();
//						}
//
//						@Override
//						public Object deserialize(byte[] paramArrayOfByte) throws ZkMarshallingError {
//							return new String(paramArrayOfByte);
//						}
//					});
//
//					ConfigChangeSubscriber sub = new ZkConfigChangeSubscriberImpl(zkClient, ssdbZkRoot);
//					sub.subscribe(source, new ConfigChangeListener() {
//
//						@Override
//						public void configChanged(String key, String value) {
//
//							ZKKafkaProducerConfig producerConfig = loadKafkaProducerConfig(value);
//							kafkaxMap.get(source).getKafkaProducerHolder().setProducerConfig(producerConfig);
//
//						}
//					});
//					// String initValue = sub.getInitValue(source);
//
//					// {"ip":"123.57.204.187","port":"8888","timeout":"200","cfg":{"maxActive":"100","testWhileIdle":true}}
//					ZKKafkaProducerConfig producerConfig = loadKafkaConfig(zkClient, ssdbZkRoot, source);
//					kafkaxMap.put(source, new KafkaProducerXImpl<String, String>(producerConfig));
//				} catch (Exception e) {
//					logger.error(e.getMessage(), e);
//					System.exit(-1);
//				} finally {
//					if (zkClient != null) {
//						zkClient.close();
//					}
//				}
//			}
//		}

		return kafkaxMap.get(source);
	}

	private static ZKKafkaProducerConfig loadKafkaConfig(ZkClient zkClient, String ssdbZkRoot, String key) {
		String kafkaStr = zkClient.readData(ssdbZkRoot + "/" + key);
		return loadKafkaProducerConfig(kafkaStr);
	}

	private static ZKKafkaProducerConfig loadKafkaProducerConfig(String jsonStr) {
		Gson gson = new Gson();
		ZKKafkaProducerConfig zkKafkaConfig = gson.fromJson(jsonStr, ZKKafkaProducerConfig.class);
		return zkKafkaConfig;
	}

}
