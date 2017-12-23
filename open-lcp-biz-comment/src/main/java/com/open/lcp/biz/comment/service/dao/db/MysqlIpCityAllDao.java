package com.open.lcp.biz.comment.service.dao.db;

import org.nutz.dao.entity.annotation.SQL;

import com.open.lcp.biz.comment.service.dao.db.entity.IpCityAllEntity;
import com.open.lcp.core.env.LcpResource;
import com.open.lcp.orm.jade.annotation.DAO;

@DAO(catalog = LcpResource.dbAnnotationName_lcp_mysql_biz_comment_master)
public interface MysqlIpCityAllDao {

	@SQL("select province,city from ip_city_all where :1 between start_ip and end_ip limit 1")
	public IpCityAllEntity getCityByIp(long ip);
}
