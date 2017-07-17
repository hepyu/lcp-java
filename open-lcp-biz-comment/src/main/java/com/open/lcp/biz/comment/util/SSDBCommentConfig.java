package com.open.lcp.biz.comment.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bchbc.dbs.cache.SSDBX;
import com.xunlei.xlmc.ssdb.SSDBLoader;

import java.util.Date;

@Configuration
public class SSDBCommentConfig {

	//TODO 迁移阶段两个ssdb共存
	@Bean(name = "ssdbCommentNew")
	public SSDBX ssdbCommentNew(){
		return SSDBLoader.loadSSDBX("comment_ssdb");
	}

	@Bean(name = "ssdbCommentOld")
	public SSDBX ssdbCommentOld(){
        return SSDBLoader.loadSSDBX("mcp");
	}

    /**
     * 单独给评论计数设置的一个client，允许失败，超时时间较短
     */
    @Bean(name = "ssdbCommentFast")
    public SSDBX ssdbxCommentFast(){
        return SSDBLoader.loadSSDBX("mcp_comment_fast");
    }

}
