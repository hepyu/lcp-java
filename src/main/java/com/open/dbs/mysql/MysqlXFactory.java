package com.open.dbs.mysql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import com.google.gson.Gson;
import com.open.dbs.DBConfig;
import com.open.env.finder.ZKFinder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MysqlXFactory {

	private static final Log logger = LogFactory.getLog(MysqlXFactory.class);

	private static Gson gson = new Gson();

	private static final Map<String, DBConfig> mysqlMasterConfigMap = new ConcurrentHashMap<String, DBConfig>();

	private static final Map<String, DBConfig> mysqlSlaveConfigMap = new ConcurrentHashMap<String, DBConfig>();

	private static final Object LOCK_OF_NEWPATH = new Object();

	public static DBConfig getMaster(final String instanceName) {
		final String mysqlMasterZkRoot = ZKFinder.findMysqlMasterZKRoot();
		DBConfig dbconfig = mysqlMasterConfigMap.get(instanceName);
		if (dbconfig == null) {
			synchronized (LOCK_OF_NEWPATH) {
				if (dbconfig == null) {
					ZkClient zkClient = null;
					try {
						zkClient = new ZkClient(ZKFinder.findZKHosts(), 180000, 180000, new ZkSerializer() {

							@Override
							public byte[] serialize(Object paramObject) throws ZkMarshallingError {
								return paramObject == null ? null : paramObject.toString().getBytes();
							}

							@Override
							public Object deserialize(byte[] paramArrayOfByte) throws ZkMarshallingError {
								return new String(paramArrayOfByte);
							}
						});

						dbconfig = loadDBConfig(zkClient, mysqlMasterZkRoot, instanceName);
						mysqlMasterConfigMap.put(instanceName, dbconfig);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						System.exit(-1);
					} finally {
						if (zkClient != null) {
							zkClient.close();
						}
					}
				}
			}
		}
		return mysqlMasterConfigMap.get(instanceName);
	}

	public static DBConfig getSlave(final String instanceName) {
		final String mysqlSlaveZkRoot = ZKFinder.findMysqlSlaveZKRoot();
		DBConfig dbconfig = mysqlSlaveConfigMap.get(instanceName);
		if (dbconfig == null) {
			synchronized (LOCK_OF_NEWPATH) {
				if (dbconfig == null) {
					ZkClient zkClient = null;
					try {
						zkClient = new ZkClient(ZKFinder.findZKHosts(), 180000, 180000, new ZkSerializer() {

							@Override
							public byte[] serialize(Object paramObject) throws ZkMarshallingError {
								return paramObject == null ? null : paramObject.toString().getBytes();
							}

							@Override
							public Object deserialize(byte[] paramArrayOfByte) throws ZkMarshallingError {
								return new String(paramArrayOfByte);
							}
						});

						dbconfig = loadDBConfig(zkClient, mysqlSlaveZkRoot, instanceName);
						mysqlSlaveConfigMap.put(instanceName, dbconfig);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						System.exit(-1);
					} finally {
						if (zkClient != null) {
							zkClient.close();
						}
					}
				}
			}
		}
		return mysqlSlaveConfigMap.get(instanceName);
	}

	private static DBConfig loadDBConfig(ZkClient zkClient, String ssdbZkRoot, String key) {
		String ssdbStr = zkClient.readData(ssdbZkRoot + "/" + key);
		return loadDBConfig(ssdbStr);
	}

	private static DBConfig loadDBConfig(String jsonStr) {
		DBConfig dbConfig = gson.fromJson(jsonStr, DBConfig.class);
		return dbConfig;
	}

}
