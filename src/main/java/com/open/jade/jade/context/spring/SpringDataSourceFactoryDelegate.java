package com.open.jade.jade.context.spring;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import com.open.jade.jade.annotation.DataSource;
import com.open.jade.jade.dataaccess.DataSourceFactory;
import com.open.jade.jade.dataaccess.DataSourceHolder;
import com.open.jade.jade.statement.StatementMetaData;

/**
 * 
 * @author 王志亮 [qieqie.wang@gmail.com]
 * 
 */
public class SpringDataSourceFactoryDelegate implements DataSourceFactory {

	private final Log logger = LogFactory.getLog(SpringDataSourceFactoryDelegate.class);

	private ListableBeanFactory beanFactory;

	private DataSourceFactory dataSourceFactory;

	public SpringDataSourceFactoryDelegate(ListableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public DataSourceHolder getHolder(StatementMetaData metaData, Map<String, Object> runtimeProperties) {
		if (dataSourceFactory == null) {
			ListableBeanFactory beanFactory = this.beanFactory;
			if (beanFactory != null) {
				DataSource ds = metaData.getDAOMetaData().getDAOClass().getAnnotation(DataSource.class);
				if (beanFactory.containsBean(ds.catalog())) {
					dataSourceFactory = (DataSourceFactory) beanFactory.getBean(ds.catalog(), DataSourceFactory.class);
				}

				// else if
				// (beanFactory.containsBeanDefinition("jade.dataSourceFactory"))
				// {
				// dataSourceFactory = (DataSourceFactory)
				// beanFactory.getBean("jade.dataSourceFactory",
				// DataSourceFactory.class);
				// } else {
				// dataSourceFactory = new SpringDataSourceFactory(beanFactory);
				// }
				else {
					logger.error("no datasource:" + ds.catalog());
					System.exit(-1);
				}
				this.beanFactory = null;
			}
		}
		return dataSourceFactory.getHolder(metaData, runtimeProperties);
	}

}
