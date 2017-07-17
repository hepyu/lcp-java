package com.open.lcp.biz.comment.service.dao;

import org.nutz.dao.entity.annotation.SQL;

import com.open.lcp.biz.comment.service.dao.entity.IpCityAllEntity;
import com.open.lcp.orm.jade.annotation.DAO;

@DAO(catalog = "lcp_biz_comment")
public interface IpCityAllDao {

	@SQL("select province,city from ip_city_all where :1 between start_ip and end_ip limit 1")
	public IpCityAllEntity getCityByIp(long ip);
}
