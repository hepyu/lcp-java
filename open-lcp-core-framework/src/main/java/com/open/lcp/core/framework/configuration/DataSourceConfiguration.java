package com.open.lcp.core.framework.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.open.lcp.core.env.LcpResource;
import com.open.lcp.dbs.mysql.MysqlXFactory;
import com.open.lcp.orm.jade.context.spring.JadeBeanFactoryPostProcessor;
import com.open.lcp.orm.jade.dataaccess.DataSourceFactory;

@Configuration
public class DataSourceConfiguration {

	// <bean id="jade.dataSource.com.chen.dao"
	// class="org.apache.commons.dbcp.BasicDataSource"
	// destroy-method="close">
	// <property name="driverClassName"
	// value="com.mysql.jdbc.Driver"></property>
	// <property name="url"
	// value="jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&amp;characterEncoding=utf-8"></property>
	// <property name="username" value="test"></property>
	// <property name="password" value="test"></property>
	// <!-- 运行判断连接超时任务的时间间隔，单位为毫秒，默认为-1，即不执行任务。 -->
	// <property name="timeBetweenEvictionRunsMillis"
	// value="3600000"></property>
	// <!-- 连接的超时时间，默认为半小时。 -->
	// <property name="minEvictableIdleTimeMillis" value="3600000"></property>
	// </bean>
	// @Bean(name = "jade.dataSourceFactory")
	// @Bean(name = "jade.dataSourceFactory") // only master
	@Bean(name = LcpResource.dbAnnotationName_lcp_mysql_core_framework_master) // only master
	public DataSourceFactory getFrameworkDataSource() {
		return MysqlXFactory.loadMysqlX(LcpResource.lcp_mysql_core_framework_master);
	}

	@Bean(name = LcpResource.dbAnnotationName_lcp_mysql_core_feature_user_master) // only master
	public DataSourceFactory getPassportDataSource() {
		return MysqlXFactory.loadMysqlX(LcpResource.lcp_mysql_core_feature_user_master);
	}

	@Bean(name = LcpResource.dbAnnotationName_lcp_mysql_biz_master) // only master
	public DataSourceFactory getBizDataSource() {
		return MysqlXFactory.loadMysqlX(LcpResource.lcp_mysql_biz_master);
	}

	// @Bean(name = "lcpBiz") // master and slave
	// public MasterSlaveDataSourceFactory getMasterSlaveDataSource() {
	//
	// DBConfig masterDBConfig =
	// ZKFinder.findMysqlMaster(ResourceEnum.mysql_lcpBiz_master.resourceName());
	// BasicDataSource master = new BasicDataSource();
	// // ds.setDriverClassName("com.mysql.jdbc.Driver");
	// //
	// ds.setUrl("jdbc:mysql://123.57.204.187:3306/lcp?useUnicode=true&amp;characterEncoding=utf-8");
	// // ds.setUsername("root");
	// // ds.setPassword("111111");
	// master.setDriverClassName(masterDBConfig.getDriverClassName());
	// master.setUrl(masterDBConfig.getUrl());
	// master.setUsername(masterDBConfig.getUserName());
	// master.setPassword(masterDBConfig.getPassword());
	// master.setTimeBetweenEvictionRunsMillis(3600000);
	// master.setMinEvictableIdleTimeMillis(3600000);
	//
	// DBConfig slaveDBConfig =
	// ZKFinder.findMysqlSlave(ResourceEnum.mysql_lcpBiz_slave.resourceName());
	// BasicDataSource slave = new BasicDataSource();
	// // ds.setDriverClassName("com.mysql.jdbc.Driver");
	// //
	// ds.setUrl("jdbc:mysql://123.57.204.187:3306/lcp?useUnicode=true&amp;characterEncoding=utf-8");
	// // ds.setUsername("root");
	// // ds.setPassword("111111");
	// slave.setDriverClassName(slaveDBConfig.getDriverClassName());
	// slave.setUrl(slaveDBConfig.getUrl());
	// slave.setUsername(slaveDBConfig.getUserName());
	// slave.setPassword(slaveDBConfig.getPassword());
	// slave.setTimeBetweenEvictionRunsMillis(3600000);
	// slave.setMinEvictableIdleTimeMillis(3600000);
	//
	// List<DataSource> slaves = new ArrayList<DataSource>();
	// slaves.add(slave);
	//
	// boolean queryFromMaster = false;
	// return new MasterSlaveDataSourceFactory(master, slaves, queryFromMaster);
	// }

	@Bean
	public JadeBeanFactoryPostProcessor getJadeBeanFactoryPostProcessor() {
		return new JadeBeanFactoryPostProcessor();
	}
}
