package com.open.lcp.core.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.open.dbs.cache.SSDBX;
import com.open.lcp.core.api.info.AppInitInfo;
import com.open.lcp.core.service.AppInitService;

@Service
public class AppInitServiceImpl implements AppInitService {
	
	private static final Log logger = LogFactory.getLog(AppInitServiceImpl.class);
	private static final String APP_INIT_KEY = "app-init-";
	
	private SSDBX ssdbx = SSDBLoader.loadSSDBX("mcp_app_init");
	@Resource
	AppInitDao appInitDao;

	@Override
	public AppInitInfo getAppInitInfo(String deviceId) {
        try{
            AppInitInfo appInitInfo = ssdbx.get(APP_INIT_KEY + deviceId, AppInitInfo.class);
            if (appInitInfo == null) {
                appInitInfo = appInitDao.getAppInit(deviceId);
                if(appInitInfo == null){
                    ssdbx.set(APP_INIT_KEY + deviceId, "{}");
                    return null;
                }
                ssdbx.set(APP_INIT_KEY+deviceId, appInitInfo);
                return appInitInfo;
            }else{
                if(StringUtils.isBlank(appInitInfo.getDeviceId())){
                    return null;
                }
                return appInitInfo;
            }
        }catch (Exception e){
            logger.error("init ssdb query error deviceId:"+deviceId, e);
        }
        return appInitDao.getAppInit(deviceId);
	}

	@Override
	public boolean setAppInitInfo(AppInitInfo appInitInfo) {
		
		AppInitInfo app = (AppInitInfo)appInitInfo;
		if(logger.isDebugEnabled()){
			logger.debug("app.init ------- "+app);
		}
		int result = appInitDao.saveAppInit(app);
		
		if(result > 0 && ssdbx.set(APP_INIT_KEY+app.getDeviceId(), app) > -1){
			return true;
		}
		return false;
	}

}
