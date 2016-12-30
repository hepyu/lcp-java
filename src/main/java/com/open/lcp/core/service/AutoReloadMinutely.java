package com.open.lcp.core.service;

public interface AutoReloadMinutely {
	/**
	 * 是否需要初始load
	 * 
	 * @return
	 */
	public boolean initLoad();

	/**
	 * 每次是否允许被调度，返回true则调用reload方法。
	 * 
	 * @param hour
	 *            当前小时：0-23
	 * @param minute
	 *            当前分钟：0-59
	 * @param minuteOfAll
	 *            当前总分钟数：1970年开始的总分钟数
	 * @return
	 */
	public boolean reloadable(int hour, int minute, long minuteOfAll);

	/**
	 * 重新装载。
	 * 
	 * @return 需要输出的info日志。null则无日志。
	 */
	public String reload();

	
}
