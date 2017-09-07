package com.open.lcp.biz.lbs.service.dao;

import java.util.List;

import com.open.lcp.biz.lbs.LBSConstant;
import com.open.lcp.biz.lbs.service.dao.entity.LBSLocationEntity;
import com.open.lcp.orm.jade.annotation.DAO;
import com.open.lcp.orm.jade.annotation.SQL;

@DAO(catalog = LBSConstant.DB_MYSQL_NAME)
public interface LBSLocationDAO {

	public final String SQL_SELECT_PART = " location_code, location_name, lat, lng, polygon, city_code, city_name, description, ctime, utime ";

	@SQL("INSERT INTO location_coordinate(" + SQL_SELECT_PART
			+ ") VALUES(:1.locationCode, :1.locationName, :1.lat, :1.lng, :1.polygon, :1.cityCode, :1.cityName, :1.description, :1.ctime, :1.utime) ON DUPLICATE KEY UPDATE location_name=:1.locationName, lat=:1.lat, lng=:1.lng, description=:1.description, utime=:1.utime, city_code=:1.cityCode, city_name=:1.cityName")
	public int insertOrUpdateLocationCoordinate(LBSLocationEntity locationCoordinate);

	@SQL("UPDATE location_coordinate SET description=:2 WHERE location_code=:1")
	public int updateLocationDesc(long locationCode, String description);

	@SQL("SELECT " + SQL_SELECT_PART
			+ " FROM location_coordinate WHERE location_code>:1 ORDER BY location_code ASC LIMIT :2")
	public List<LBSLocationEntity> listLocationCoordinate(long startLocationCode, int pageSize);

	@SQL("UPDATE location_coordinate SET location_code=:1.locationCode, city_code=:1.cityCode, city_name=:1.cityName, description=:1.description WHERE location_name=:1.locationName")
	public int updateForWashData(LBSLocationEntity locationCoordinate);

	@SQL("SELECT location_code, location_name, description FROM location_coordinate WHERE city_code=:1 ")
	public List<LBSLocationEntity> listAll(long cityCode);

	@SQL("SELECT " + SQL_SELECT_PART + " FROM location_coordinate WHERE location_code=:1")
	public LBSLocationEntity getLocationCoordinate(long locationCode);

	@SQL("DELETE FROM location_coordinate WHERE location_code=:1")
	public int deleteLocationCoordinate(long locationCode);

}
