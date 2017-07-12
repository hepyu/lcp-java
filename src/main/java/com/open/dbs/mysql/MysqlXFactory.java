package com.open.dbs.mysql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.open.dbs.DBConfig;
import com.open.env.finder.ZKFinder;
import com.open.jade.jade.dataaccess.DataSourceFactory;
import com.open.jade.jade.dataaccess.datasource.HierarchicalDataSourceFactory;
import com.open.lcp.LcpResource;

public class MysqlXFactory {

	private static final Log logger = LogFactory.getLog(MysqlXFactory.class);

	private static HierarchicalDataSourceFactory hierarchicalDataSourceFactory = new HierarchicalDataSourceFactory();

	private static Gson gson = new Gson();

	private static final Map<LcpResource, DataSourceFactory> dataSourceMap = new ConcurrentHashMap<LcpResource, DataSourceFactory>();

	private static final Object LOCK_OF_NEWPATH = new Object();

	public static DataSourceFactory loadMysqlX(final LcpResource zkResourcePath) {
		DataSourceFactory ds = dataSourceMap.get(zkResourcePath);
		if (ds == null) {
			synchronized (LOCK_OF_NEWPATH) {
				ds = dataSourceMap.get(zkResourcePath);
				if (ds == null) {
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

						DBConfig dbconfig = loadDBConfig(zkResourcePath, zkClient);
						ds = load(zkResourcePath, dbconfig);
						dataSourceMap.put(zkResourcePath, ds);
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
		return dataSourceMap.get(zkResourcePath);
	}

	private static DBConfig loadDBConfig(LcpResource zkResourcePath, ZkClient zkClient) {
		String ssdbStr = zkClient.readData(ZKFinder.findAbsoluteZKResourcePath(zkResourcePath));
		return loadDBConfig(ssdbStr);
	}

	private static DBConfig loadDBConfig(String jsonStr) {
		DBConfig dbConfig = gson.fromJson(jsonStr, DBConfig.class);
		return dbConfig;
	}

	private static DataSourceFactory load(final LcpResource lcpResource, final DBConfig dbconfig) {
		BasicDataSource ds = new BasicDataSource();
		// ds.setDriverClassName("com.mysql.jdbc.Driver");
		// ds.setUrl("jdbc:mysql://123.57.204.187:3306/lcp?useUnicode=true&amp;characterEncoding=utf-8");
		// ds.setUsername("root");
		// ds.setPassword("111111");
		ds.setDriverClassName(dbconfig.getDriverClassName());
		ds.setUrl(dbconfig.getUrl());
		ds.setUsername(dbconfig.getUserName());
		ds.setPassword(dbconfig.getPassword());
		ds.setTimeBetweenEvictionRunsMillis(3600000);
		ds.setMinEvictableIdleTimeMillis(3600000);

		hierarchicalDataSourceFactory.registerDataSource(lcpResource.lcpAnnotationName(), ds);
		return hierarchicalDataSourceFactory;
	}

}
