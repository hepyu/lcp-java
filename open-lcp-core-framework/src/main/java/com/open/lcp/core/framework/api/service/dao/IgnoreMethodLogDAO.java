package com.open.lcp.core.framework.api.service.dao;

import java.util.List;

import com.open.lcp.core.env.LcpResource;
import com.open.lcp.core.framework.api.service.dao.entity.IgnoreMethodLogEntity;
import com.open.lcp.orm.jade.annotation.DAO;
import com.open.lcp.orm.jade.annotation.SQL;

@DAO(catalog = LcpResource.dbAnnotationName_lcp_mysql_core_framework_master)
public interface IgnoreMethodLogDAO {

	@SQL("SELECT method_name, ctime FROM lcp_sys_config_ignore_log_method ORDER BY method_name ASC")
	public List<IgnoreMethodLogEntity> listAll();

}
