package com.open.lcp.core.framework.api.listener;

import com.open.lcp.core.base.command.CommandContext;

/**
 * 命令侦听：不支持动态变更
 * 
 * @author 
 */
public interface CommandListener {

    /**
     * 返回需要侦听的接口列表，大小写不敏感，仅在首次加载时获取，之后不会更新。
     * 
     * @return
     */
    public String[] getCommands();

    /**
     * 命令执行前调用。如果需要改变指令执行的数据，可以此操作。
     * 
     * @param ctx 用户请求报文的相关数据。
     */
    public void beforeExec(CommandContext ctx);

    /**
     * 命令执行后调用。如果需要知道执行后的结果，可以此操作。
     * 
     * @param ctx 用户请求报文的相关数据
     * @param execBeginTime 接口调用的起始时间
     * @param code 执行结果：错误码。执行结果未知时，值为-1
     * @param data 执行结果：具体对象
     * @param ext 其它信息，一般为版本升级信息
     */
    public void afterExec(CommandContext ctx, long execBeginTime, int code, Object data, Object ext);
}
