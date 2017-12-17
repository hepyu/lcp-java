package com.open.lcp.core.app.init.service.dao;

import com.open.lcp.core.app.init.service.dao.entity.AppInitInfoEntity;
import com.open.lcp.orm.jade.annotation.DAO;
import com.open.lcp.orm.jade.annotation.SQL;

@DAO(catalog = "mysql_lcp_biz")
public interface AppInitInfoDAO {

	@SQL("select device_id, miui_version, product_version, miui_type, language, country, screen_height, device, android_version, model, screen_resolution, screen_width, screen_density, mac, imei, mac_md5, imei_md5 from app_init_info where device_id=:1")
	public AppInitInfoEntity getAppInit(String deviceId);

	@SQL("insert into app_init_info value (:1.deviceId,:1.miuiVersion, :1.productVersion, :1.miuiType, :1.language, :1.country, :1.screenHeight, :1.device, :1.androidVersion, model=:1.model, :1.screenResolution, :1.screenWidth, :1.screenDensity, :1.mac, :1.imei, :1.macMd5, :1.imeiMd5) on DUPLICATE KEY UPDATE miui_version=:1.miuiVersion, product_version=:1.productVersion, miui_type=:1.miuiType, language=:1.language, country=:1.country, screen_height=:1.screenHeight, device=:1.device, android_version=:1.androidVersion, model=:1.model, screen_resolution=:1.screenResolution, screen_width=:1.screenWidth, screen_density=:1.screenDensity, mac=:1.mac, imei=:1.imei, mac_md5=:1.macMd5, imei_md5=:1.imeiMd5")
	public int saveAppInit(AppInitInfoEntity appInitInfo);
}
