package com.open.lcp.biz.comment.facade.resp;

public interface PointResult {

	public int getResult() ;
	public void setResult(int result);
	
	public long getNewPoint();
	public void setNewPoint(long newPoint);
	
	public long getPoint();
	public void setPoint(long point);	

	public String getSource();
	public void setSource(String source);
	
	public String getMemo() ;
	public void setMemo(String memo) ;
}
