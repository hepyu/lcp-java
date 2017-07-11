package com.open.lcp.framework.core.api.service.dao;

import java.util.List;

import com.open.jade.jade.annotation.DAO;
import com.open.jade.jade.annotation.DataSource;
import com.open.jade.jade.annotation.SQL;
import com.open.lcp.framework.core.api.service.dao.entity.IgnoreMethodLogEntity;

@DataSource(catalog = "lcp_framework")
@DAO(catalog = "lcp")
public interface IgnoreMethodLogDAO {

	@SQL("SELECT method_name, ctime FROM lcp_sys_config_ignore_log_method ORDER BY method_name ASC")
	public List<IgnoreMethodLogEntity> listAll();

}
