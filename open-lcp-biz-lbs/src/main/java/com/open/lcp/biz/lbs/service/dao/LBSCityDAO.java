package com.open.lcp.biz.lbs.service.dao;

import com.open.lcp.biz.lbs.LBSConstant;
import com.open.lcp.biz.lbs.service.dao.entity.LBSDeviceEntity;
import com.open.lcp.orm.jade.annotation.DAO;
import com.open.lcp.orm.jade.annotation.SQL;

@DAO(catalog = LBSConstant.DB_MYSQL_NAME)
public interface LBSCityDAO {

	@SQL("SELECT city_code, city_name, ctime, utime FROM lbs_city WHERE city_code=:1 Limit 1")
	public LBSDeviceEntity getCityInfo(long cityCode);

}
