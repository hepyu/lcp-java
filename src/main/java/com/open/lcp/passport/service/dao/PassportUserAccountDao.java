package com.open.lcp.passport.service.dao;

import com.open.jade.jade.annotation.DAO;
import com.open.jade.jade.annotation.SQL;
import com.open.lcp.passport.service.dao.entity.PassportUserAccountEntity;

@DAO(catalog = "mcp")
public interface PassportUserAccountDao {

	@SQL("INSERT INTO passport_user_account(user_id, user_name, nick_name, avatar, gender, update_ip, regist_ip, update_time, regist_time) values(:1.userId, :1.userName, :1.nickName, :1.avatar, :1.gender, :1.updateIp, :1.registIp, :1.updateTime, :1.registTime) on duplicate key update user_name=:1.userName, nick_name=:1.nickName, avatar=:1.avatar, gender=:1.gender, update_time=:1.updateTime, update_ip=:1.updateIp")
	public int insertOrUpdate(PassportUserAccountEntity passportUserAccountEntity);

	@SQL("INSERT IGNORE INTO passport_user_account(user_id, user_name, nick_name, avatar, gender, update_ip, regist_ip, update_time, regist_time) values(:1.userId, :1.userName, :1.nickName, :1.avatar, :1.gender, :1.updateIp, :1.registIp, :1.updateTime, :1.registTime)")
	public int insert(PassportUserAccountEntity passportUserAccountEntity);

	@SQL("SELECT user_id, user_name, nick_name, avatar, gender, update_ip, regist_ip, update_time, regist_time, description from passport_user_account WHERE user_id=:1 LIMIT 1")
	public PassportUserAccountEntity getUserInfoByUserId(Long userId);

	@SQL("UPDATE passport_user_account SET gender=:2 WHERE user_id=:1")
	public int updateGender(Long userId, int gender);

	@SQL("UPDATE passport_user_account SET nick_name=:2 WHERE user_id=:1")
	public int updateNickName(Long userId, String nickName);

	@SQL("UPDATE passport_user_account SET avatar=:2 WHERE user_id=:1")
	public int updateAvatar(Long userId, String avatar);

	@SQL("UPDATE passport_user_account SET description=:2 WHERE user_id=:1")
	public int updateDescription(Long userId, String description);

	@Deprecated
	@SQL("DELETE FROM passport_user_account WHERE user_id=:1")
	public int delPassportUserAccountByUserId(Long userId);

}
