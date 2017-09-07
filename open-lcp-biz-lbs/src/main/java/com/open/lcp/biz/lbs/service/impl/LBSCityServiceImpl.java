package com.open.lcp.biz.lbs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.open.lcp.biz.lbs.service.LBSCityService;
import com.open.lcp.biz.lbs.service.cache.LBSCityJVMCache;
import com.open.lcp.biz.lbs.service.dao.entity.LBSCityEntity;

@Component
public class LBSCityServiceImpl implements LBSCityService {

	@Autowired
	private LBSCityJVMCache cityJVMCache;

	@Override
	public LBSCityEntity getCity(long cityCode) {
		try {
			return cityJVMCache.cityCodeToDBEntityCache.get(cityCode);
		} catch (Exception e) {
			return null;
		}
	}
}
