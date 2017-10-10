package com.open.lcp.biz.lbs.service.dao;

import com.open.lcp.biz.lbs.LBSConstant;
import com.open.lcp.biz.lbs.service.dao.entity.LBSDeviceEntity;
import com.open.lcp.orm.jade.annotation.DAO;
import com.open.lcp.orm.jade.annotation.SQL;

@DAO(catalog = LBSConstant.DB_MYSQL_NAME)
public interface LBSDeviceDAO {

	@SQL("INSERT INTO lbs_device(device_id, xl_user_id, lat, lng, city_code, city_name, ctime, utime) VALUES(:1.deviceId, :1.xlUserId, :1.lat, :1.lng, :1.cityCode, :1.cityName, :1.ctime, :1.utime) ON DUPLICATE KEY UPDATE xl_user_id=:1.xlUserId, lat=:1.lat, lng=:1.lng, city_code=:1.cityCode, city_name=:1.cityName, utime=:1.utime")
	public int insertOrUpdateDeviceCoordinate(LBSDeviceEntity deviceCoordinate);

	@SQL("SELECT device_id, xl_user_id, lat, lng, city_code, city_name, ctime, utime FROM lbs_device WHERE device_id=:1")
	public LBSDeviceEntity getDeviceCoordinate(String deviceId);

}