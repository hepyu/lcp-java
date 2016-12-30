package com.open.lcp.framework.core.api.service.dao;

import java.util.List;

import com.open.lcp.framework.core.api.service.dao.entity.ApiMaxThreadsEntity;
import com.open.lcp.framework.core.api.service.dao.entity.AppAuthInfoEntity;
import com.open.lcp.framework.core.api.service.dao.entity.AppInfoEntity;
import com.open.lcp.framework.core.api.service.dao.entity.LcpTimeSwitcherEntity;

import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;

@DAO(catalog = "lcp")
public interface AppInfoDao {

	// *** (1). table lcp_sys_config_app_info  ***//

	@SQL("SELECT app_id,app_name,app_secret_key,app_package_name,app_platform_id,app_os_id,add_time,blcode,responsible,is_point,is_short_msg,recommend_platform,is_use_https FROM lcp_sys_config_app_info")
	public List<AppInfoEntity> getAppList();

	@SQL("insert into lcp_sys_config_app_info values(:1.appId, :1.appName, :1.appSecretKey, :1.appPackageName, :1.appPlatformId, :1.appOsId, now(), :1.blCode, :1.responsible, :1.isPoint, :1.isShortMsg, :1.recommendPlatform, :1.isUseHttps) ")
	public int createApp(AppInfoEntity appInfo);

	@SQL("SELECT app_id,app_name,app_secret_key,app_package_name,app_platform_id,app_os_id,add_time,blcode,responsible,is_point,is_short_msg,recommend_platform,is_use_https FROM lcp_sys_config_app_info where app_id=:1")
	public AppInfoEntity findAppInfoByAppId(int appId);

	// *** (2). table lcp_sys_config_client_auth : ***//

	@SQL("SELECT id,app_id,auth_method,auth_ips,add_time FROM lcp_sys_config_client_auth where app_id = :1")
	public List<AppAuthInfoEntity> loadAcceptMethodsByAppId(int appId);

	@SQL("SELECT id,app_id,auth_method,auth_ips,add_time FROM lcp_sys_config_client_auth")
	public List<AppAuthInfoEntity> loadAllAuthorities();

	// *** (3). table lcp_time_switcher : ***//

	/* 时间开关 */
	@SQL("SELECT tsid,tsname,tsbegin,tsend,tsext FROM lcp_time_switcher")
	public List<LcpTimeSwitcherEntity> getLcpTimeSwitcher();

	@SQL("update lcp_time_switcher set tsbegin = :2, tsend = :3 where tsid = :1")
	public int setLcpTimeSwitcher(int tsid, long tsbegin, long tsend);

	@SQL("update lcp_time_switcher set tsbegin = :2, tsend = :3, tsext = :4 where tsid = :1")
	public int setLcpTimeSwitcher(int tsid, long tsbegin, long tsend, String tsext);

	// *** (4). table lcp_api_max_threads : ***//

	/* 接口并发限制 */
	@SQL("SELECT api,max_threads,out_resp,keys_req FROM lcp_api_max_threads")
	public List<ApiMaxThreadsEntity> getApiMaxThreads();
}
