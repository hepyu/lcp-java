package com.open.lcp.framework.core.api.command;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.open.lcp.framework.core.annotation.LcpReq;
import com.open.lcp.framework.core.annotation.LcpRequired;
import com.open.lcp.framework.core.api.LcpFieldLoadHolder;
import com.open.lcp.framework.core.api.RequiredCheck;
import com.open.lcp.framework.core.api.RequiredCheck.ErrorType;

/**
 * 必要字段及阈值校验
 * 
 * @author
 */
public class LcpRequiredCheckHolder {

	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(LcpRequiredCheckHolder.class);

	private static final RequiredCheck CHECK_PASS = RequiredCheck.build(null, null, ErrorType.Pass);

	private static final RequiredCheck CHECK_ROOT_NULL = RequiredCheck.build(null, null, ErrorType.Required);

	/**
	 * 单层对象的Required注解校验
	 * 
	 * @param o
	 * @return
	 */
	public static RequiredCheck checkMonolayer(Object o) {
		if (o == null) {
			return CHECK_ROOT_NULL;
		}
		Class<?> clazz = o.getClass();
		if (clazz.getName().startsWith("java.")) {
			return CHECK_PASS;
		}
		List<Field> fields = LcpFieldLoadHolder.getFields(clazz);
		for (Field field : fields) {
			RequiredCheck result = check(field, o);
			if (result != null && result.getErrorType() != ErrorType.Pass) {
				return result;
			}
		}
		return CHECK_PASS;
	}

	/**
	 * 单个对象的Required注解校验
	 * 
	 * @param o
	 * @return
	 */
	public static RequiredCheck checkMultilayer(Object o) {
		if (o == null) {
			return CHECK_ROOT_NULL;
		}
		Class<?> clazz = o.getClass();
		if (clazz.getName().startsWith("java.lang.") || !clazz.getName().contains(".")) {
			return CHECK_PASS;
		}
		List<Field> fields = LcpFieldLoadHolder.getFields(clazz);
		for (Field field : fields) {
			RequiredCheck result = check(field, o);
			if (result != null && result.getErrorType() != ErrorType.Pass) {// 未通过校验
				return result;
			}
			Object value = null;
			try {
				field.setAccessible(true);
				value = field.get(o);
			} catch (Exception e) {
				continue;
			}
			if (value == null)
				continue;

			Class<?> arrayClazz = value.getClass().getComponentType();
			if (arrayClazz != null) {// 确实是数组
				Object[] os = (Object[]) value;
				int aoIndex = 0;
				for (Object ao : os) {
					aoIndex++;
					if (ao == null) {// 数组中出现null值
						return RequiredCheck.buildArrayHasNull(o, field, aoIndex);
					}
					result = checkMultilayer(ao);
					if (result != null && result.getErrorType() != ErrorType.Pass) {// 未通过校验
						return result;
					}
				}
			}
			if (List.class.isInstance(value)) {
				List<?> ls = (List<?>) value;
				int aoIndex = 0;
				for (Object lo : ls) {
					aoIndex++;
					if (lo == null) {// 数组中出现null值
						return RequiredCheck.buildArrayHasNull(o, field, aoIndex);
					}
					result = checkMultilayer(lo);
					if (result != null && result.getErrorType() != ErrorType.Pass) {// 未通过校验
						return result;
					}
				}
			}
		}
		return CHECK_PASS;
	}

	private static RequiredCheck check(Field field, Object o) {
		// Class<?> clazz = o.getClass();
		LcpRequired required = field.getAnnotation(LcpRequired.class);
		if (required == null) {
			return null;
		}
		field.setAccessible(true);
		try {
			Object value = field.get(o);
			if (value == null) {
				if (required.value()) {
					return RequiredCheck.build(o, field, ErrorType.Required);
				}
				return null;
			}
			Class<?> c = value.getClass();
			if (required.max() != 0 || required.min() != 0) {
				{// double有小数位，单独校验
					Double dValue = null;
					if (c == double.class) {
						dValue = field.getDouble(o);
					} else if (c == Double.class) {
						dValue = (Double) field.get(o);
					}
					if (dValue != null) {
						if (dValue > required.max() && required.max() >= required.min()) {
							return RequiredCheck.build(o, field, ErrorType.MaxLimited);
						}
						if (dValue < required.min()) {
							return RequiredCheck.build(o, field, ErrorType.MinLimited);
						}
					}
				}
				// 其它无小数的情况
				Long longValue = null;
				if (c == int.class) {
					long curValue = field.getInt(o);
					longValue = curValue;
				} else if (c == Integer.class) {
					long curValue = (Integer) field.get(o);
					longValue = curValue;
				} else if (c == long.class) {
					longValue = field.getLong(o);
				} else if (c == Long.class) {
					longValue = (Long) field.get(o);
				} else if (c == String.class) {
					String strValue = (String) field.get(o);
					long length = strValue.length();
					longValue = length;
				}
				if (longValue != null) {
					if (longValue > required.max() && required.max() >= required.min()) {
						return RequiredCheck.build(o, field, ErrorType.MaxLimited);
					}
					if (longValue < required.min()) {
						return RequiredCheck.build(o, field, ErrorType.MinLimited);
					}
				}
			}
		} catch (Exception e) {
			logger.warn("checkSingle exception", e);
		}
		return null;
	}

	public static RequiredCheck check(LcpReq lcpReq, Object value) {
		if (lcpReq == null) {
			return null;
		}
		try {
			if (value == null) {
				if (lcpReq.required()) {
					return RequiredCheck.buildMcpReq(value, lcpReq, ErrorType.Required);
				}
				return null;
			}
			if (lcpReq.max() != 0 || lcpReq.min() != 0) {
				Class<?> c = value.getClass();
				{// double有小数位，单独校验
					Double dValue = null;
					if (c == double.class || c == Double.class) {
						dValue = (Double) value;
					}
					if (dValue != null) {
						if (dValue > lcpReq.max() && lcpReq.max() >= lcpReq.min()) {
							return RequiredCheck.buildMcpReq(value, lcpReq, ErrorType.MaxLimited);
						}
						if (dValue < lcpReq.min()) {
							return RequiredCheck.buildMcpReq(value, lcpReq, ErrorType.MinLimited);
						}
					}
				}
				// 其它无小数的情况
				Long longValue = null;
				if (c == int.class || c == Integer.class) {
					long curValue = (Integer) value;
					longValue = curValue;
				} else if (c == long.class || c == Long.class) {
					longValue = (Long) value;
				} else if (c == String.class) {
					String strValue = (String) value;
					long length = strValue.length();
					longValue = length;
				}
				if (longValue != null) {
					if (longValue > lcpReq.max() && lcpReq.max() >= lcpReq.min()) {
						return RequiredCheck.buildMcpReq(value, lcpReq, ErrorType.MaxLimited);
					}
					if (longValue < lcpReq.min()) {
						return RequiredCheck.buildMcpReq(value, lcpReq, ErrorType.MinLimited);
					}
				}
			}
		} catch (Exception e) {
			logger.warn("checkSingle exception", e);
		}
		return null;
	}
}
