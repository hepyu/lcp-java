package com.open.dbs.mysql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import com.google.gson.Gson;
import com.open.dbs.DBConfig;
import com.open.env.finder.ZKFinder;
import com.open.lcp.ZKResourcePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MysqlXFactory {

	private static final Log logger = LogFactory.getLog(MysqlXFactory.class);

	private static Gson gson = new Gson();

	private static final Map<ZKResourcePath, DBConfig> mysqlMasterConfigMap = new ConcurrentHashMap<ZKResourcePath, DBConfig>();

	private static final Map<ZKResourcePath, DBConfig> mysqlSlaveConfigMap = new ConcurrentHashMap<ZKResourcePath, DBConfig>();

	private static final Object LOCK_OF_NEWPATH = new Object();

	public static DBConfig getMaster(final ZKResourcePath zkResourcePath) {
		DBConfig dbconfig = mysqlMasterConfigMap.get(zkResourcePath);
		if (dbconfig == null) {
			synchronized (LOCK_OF_NEWPATH) {
				dbconfig = mysqlMasterConfigMap.get(zkResourcePath);
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

						dbconfig = loadDBConfig(zkResourcePath, zkClient);
						mysqlMasterConfigMap.put(zkResourcePath, dbconfig);
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
		return mysqlMasterConfigMap.get(zkResourcePath);
	}

	public static DBConfig getSlave(final ZKResourcePath zkResourcePath) {
		DBConfig dbconfig = mysqlSlaveConfigMap.get(zkResourcePath);
		if (dbconfig == null) {
			dbconfig = mysqlSlaveConfigMap.get(zkResourcePath);
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

						dbconfig = loadDBConfig(zkResourcePath, zkClient);
						mysqlSlaveConfigMap.put(zkResourcePath, dbconfig);
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
		return mysqlSlaveConfigMap.get(zkResourcePath);
	}

	private static DBConfig loadDBConfig(ZKResourcePath zkResourcePath, ZkClient zkClient) {
		String ssdbStr = zkClient.readData(ZKFinder.findAbsoluteZKResourcePath(zkResourcePath));
		return loadDBConfig(ssdbStr);
	}

	private static DBConfig loadDBConfig(String jsonStr) {
		DBConfig dbConfig = gson.fromJson(jsonStr, DBConfig.class);
		return dbConfig;
	}

}
