package com.open.lcp.framework.core.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author
 */
public class NZCodeHolder {

	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(NZCodeHolder.class);

	private static final Map<Integer, NZCode> NZMAP = new ConcurrentHashMap<Integer, NZCode>();

	static {
		for (NZCodeBase b : NZCodeBase.values()) {
			set(b.getCode(), b.getMessage(), b.getMessage());
		}
	}

	/**
	 * 取NZCode
	 * 
	 * @param nzcode
	 *            非零错误码
	 * @return
	 */
	public static NZCode get(Integer nzcode) {
		return NZMAP.get(nzcode);
	}

	public static void set(ApiException api, String cause) {
		set(api.getErrorCode(), api.getMessage(), cause);
	}

	/**
	 * 增加一项NZCode定义
	 * 
	 * @param nzcode
	 *            错误码
	 * @param cause
	 *            错误原因：开发及调试用。
	 * @param message
	 *            用户提示，展现用。
	 */
	public static void set(int nzcode, String cause, String message) {
		NZCode old = NZMAP.get(nzcode);
		if (old != null) {
			final String errorMsg = String.format("NZCodeHolder nzcode duplicate definition, old:%s ,new: %s(%s)",
					old.toString(), cause, message);
			if (logger.isDebugEnabled()) {
				logger.error("", new Exception(errorMsg));
			} else {
				logger.error(errorMsg);
			}
			return;
		}
		final NZCode n = new NZCode();
		n.setCode(nzcode);
		n.setCause(cause);
		n.setMessage(message);
		NZMAP.put(nzcode, n);
	}

	public static Collection<NZCode> getAll() {
		return NZMAP.values();
	}

	public static Set<Integer> getKeys() {
		return NZMAP.keySet();
	}

	public static int[] getSortedNZCodes() {
		Set<Integer> ks = NZMAP.keySet();
		int[] nzcodes = new int[ks.size()];
		int i = 0;
		for (Integer nzcode : ks) {
			nzcodes[i++] = nzcode;
		}
		Arrays.sort(nzcodes);
		return nzcodes;
	}
}
