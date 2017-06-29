package com.open.lcp.passport.service.dao;

import java.util.List;
import com.open.jade.jade.annotation.DAO;
import com.open.jade.jade.annotation.SQL;
import com.open.lcp.passport.service.dao.entity.PassportOAuthAccountEntity;

@DAO(catalog = "mcp")
public interface PassportOAuthAccountDao {

	@SQL("INSERT INTO passport_oauth_account(user_id, open_id, type, user_name, nick_name, avatar, gender, update_ip, bind_ip, update_time, bind_time) values(:1.userId, :1.openId, :1.type, :1.userName, :1.nickName, :1.headIconUrl, :1.gender, :1.updateIp, :1.bindIp, :1.updateTime, :1.bindTime) on duplicate key update user_name=:1.nickName, nick_name=:1.nickName, avatar=:1.headIconUrl, gender=:1.gender, update_time=:1.updateTime, update_ip=:1.updateIp")
	public int insertOrUpdate(PassportOAuthAccountEntity passportOAuthAccountEntity);

	@SQL("INSERT INTO passport_oauth_account(user_id, open_id, type, user_name, nick_name, avatar, gender, update_ip, bind_ip, update_time, bind_time) values(:1.userId, :1.openId, :1.type, :1.userName, :1.nickName, :1.headIconUrl, :1.gender, :1.updateIp, :1.bindIp, :1.updateTime, :1.bindTime)")
	public int insert(PassportOAuthAccountEntity passportOAuthAccountEntity);

	@SQL("SELECT user_id FROM passport_oauth_account WHERE open_id=:1 AND type=:2")
	public Long getUserId(String openId, int type);

	@SQL("SELECT * FROM passport_oauth_account WHERE user_id=:1 AND type=:2")
	public List<PassportOAuthAccountEntity> getOAuthAccountInfo(Long userId, int type);

	@SQL("SELECT type FROM passport_oauth_account WHERE user_id=:1")
	public List<Integer> getOAuthAccountTypeList(Long userId);

	@SQL("SELECT count(1) FROM passport_oauth_account WHERE user_id=:1 AND type NOT IN(2)")
	public int countAccountExceptMobile(Long userId);

	@SQL("SELECT count(1) FROM passport_oauth_account WHERE user_id=:1 AND type='weichat'")
	public int existWeichatAccount(Long userId);

	@SQL("SELECT count(1) FROM passport_oauth_account WHERE user_id=:1")
	public int existAccount(Long userId);

	// @Deprecated
	// @SQL("DELETE FROM passport_oauth_account WHERE user_id=:1")
	// public int delPassportOAuthAccountByUserId(Long userId);

	@SQL("SELECT * FROM passport_oauth_account WHERE user_id=:1")
	public List<PassportOAuthAccountEntity> getOAuthAccountListByUserId(Long userId);

	@SQL("DELETE FROM passport_oauth_account WHERE user_id=:1 AND type=:2 AND (SELECT t.total FROM (SELECT count(1) as total FROM passport_oauth_account where user_id=:1) AS t) > 1;")
	public int unbindOAuthAccount(Long userId, int userAccountType);

	// 下列方法仅仅用于洗数据，其他场合不准使用
	@SQL("UPDATE passport_oauth_account SET user_name=:2, nick_name=:3, update_time=:4 WHERE user_id=:1 AND type=7")
	public int updateTextField(Long userId, String userName, String nickName, long ts);

	@Deprecated
	@SQL("DELETE FROM passport_oauth_account WHERE user_id=:1")
	public int delPassportOAuthAccountByUserId(Long userId);

}
