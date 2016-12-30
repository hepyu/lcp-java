package com.open.lcp.framework.core.api;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.open.lcp.framework.core.annotation.LcpReq;
import com.open.lcp.framework.core.annotation.LcpRequired;
import com.open.lcp.framework.core.api.command.CommandContext;
import com.open.lcp.framework.util.LcpMixEncUtils;
import com.open.lcp.framework.util.StringToListUtils;
import com.open.lcp.framework.core.annotation.LcpRequired.Struct;

/**
 * 对象转换工具
 * 
 * @author Marshal(imdeep@gmail.com) Initial Created at 2013-10-25
 */
public class ModelCastHolder {

	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(ModelCastHolder.class);

	private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	/**
	 * 从map转成新对象
	 * 
	 * @param paramMap
	 * @param targetClazz
	 * @return
	 */
	public static <T> T mappingNew(Map<String, ?> paramMap, Class<T> targetClazz) {
		if (paramMap == null)
			return null;
		try {
			return mapping(paramMap, targetClazz.newInstance());
		} catch (InstantiationException e) {
			String sig = "";
			CommandContext ctx = LcpThreadLocal.thCommandContext.get();
			if (ctx != null) {
				sig = ctx.getSig();
			}
			logger.error("mapping(Map<String,Object>, Class<T>) sig:" + sig, e);
		} catch (IllegalAccessException e) {
			String sig = "";
			CommandContext ctx = LcpThreadLocal.thCommandContext.get();
			if (ctx != null) {
				sig = ctx.getSig();
			}
			logger.error("mapping(Map<String,Object>, Class<T>) sig:" + sig, e);
		}
		return null;
	}

	/** 单个参数独立注解时的情况 */
	public static <T> Object mappingParameter(Map<String, ?> paramMap, LcpReq lcpReq, Type t) {
		if (paramMap == null || lcpReq == null) {
			return null;
		}
		if (!paramMap.containsKey(lcpReq.name())) {// 无参时：非必选返回默认，必选返回null
			if (!lcpReq.required())
				return getDefaultValue(t);
			return null;
		}
		final Object o = paramMap.get(lcpReq.name());
		Object value = mapping(o, t);
		if (lcpReq.trim() && String.class.isInstance(o)) {
			value = ((String) value).trim();
		}
		return value;
	}

	public static Object getDefaultValue(Type t) {
		if (t == null)
			return null;
		if (t == boolean.class) {
			return false;
		}
		if (t == byte.class) {
			return (byte) 0;
		}
		if (t == short.class) {
			return (short) 0;
		}
		if (t == char.class) {
			return (char) 0;
		}
		if (t == int.class) {
			return 0;
		}
		if (t == long.class) {
			return 0L;
		}
		if (t == double.class) {
			return (double) 0.0;
		}
		if (t == float.class) {
			return (float) 0;
		}
		return null;
	}

	public static Object mapping(Object o, Type t) {
		Class<?> clazz = null;
		if (t instanceof Class<?>) {
			clazz = (Class<?>) t;
		}
		if (o == null) {
			return null;
		}
		if (o.getClass() != String.class) {
			return null;
		}
		String value = (String) o;
		if (clazz != null) {
			if (clazz.isInstance(o)) {
				return o;
			}
			if (clazz == String.class) {
				return value;
			}
			if (clazz == int.class || clazz == Integer.class) {
				return Integer.valueOf(value);
			}
			if (clazz == long.class || clazz == Long.class) {
				return Long.valueOf(value);
			}
			if (clazz == double.class || clazz == Double.class) {
				return Double.valueOf(value);
			}
		}
		Class<?> componentType = null;
		if (clazz != null) {
			componentType = clazz.getComponentType();
		}
		if (clazz != null && componentType != null) {// 是数组
			Class<?> type = componentType;
			if (type == Integer.class) {
				return StringToListUtils.toIntList(value).toArray(new Integer[0]);
			} else if (type == Long.class) {
				return StringToListUtils.toLongList(value).toArray(new Long[0]);
			} else if (type == String.class) {
				return StringToListUtils.toStringList(value, true).toArray(new String[0]);
			} else if (type == int.class) {
				return StringToListUtils.toIntArray(value);
			} else if (type == long.class) {
				return StringToListUtils.toLongArray(value);
			}
		} else if (t instanceof ParameterizedType) {// 泛型
			ParameterizedType pt = (ParameterizedType) t;
			Class<?> type = (Class<?>) (pt.getActualTypeArguments()[0]);
			if (type == Integer.class) {
				return StringToListUtils.toIntList(value);
			} else if (type == Long.class) {
				return StringToListUtils.toLongList(value);
			} else if (type == String.class) {
				return StringToListUtils.toStringList(value, true);
			}
			// Type[] actualTypes = ((ParameterizedType)
			// f.getGenericType()).getActualTypeArguments();
			// if (actualTypes.length == 1) {
			// paramMap.remove(f.getName());
			// f.setAccessible(true);
			// Class<?> type = (Class<?>) actualTypes[0];
			// if (type == Integer.class) {
			// f.set(targetObj, StringToListUtils.toIntList(value));
			// } else if (type == Long.class) {
			// f.set(targetObj, StringToListUtils.toLongList(value));
			// } else if (type == String.class) {
			// f.set(targetObj, StringToListUtils.toStringList(value, true));
			// }
			// }
		}
		return null;
	}

	/**
	 * 用map中的数据补充目标对象
	 * 
	 * @param paramMap
	 * @param targetObj
	 * @return
	 */
	public static <T> T mapping(Map<String, ?> paramMapSrc, T targetObj) {
		if (paramMapSrc == null)
			return targetObj;
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.putAll(paramMapSrc);
			{
				List<Field> fs = LcpFieldLoadHolder.getFields(targetObj.getClass());
				for (Field f : fs) {
					if (f == null)
						continue;
					if (!paramMap.containsKey(f.getName()))
						continue;
					Object o = paramMap.get(f.getName());
					if (o == null)
						continue;
					if (o.getClass() != String.class)
						continue;
					String value = (String) o;
					final LcpRequired req = f.getAnnotation(LcpRequired.class);
					final LcpRequired.Struct struct = req == null ? null : req.struct();
					if (req != null) {
						if (req.trim()) {
							value = value.trim();
						}
						final CommandContext cctx = LcpThreadLocal.thCommandContext.get();
						if (req.aes()) {
							final byte[] aesKey = cctx.getAesKey();
							if (req.gz()) {
								value = LcpMixEncUtils.AesGzDecode(value, aesKey);
							} else {
								value = LcpMixEncUtils.AesDecode(value, aesKey);
							}
							cctx.addStatExt(f.getName(), value);
							paramMap.put(f.getName(), value);
						} else if (req.gz()) {
							value = new String(LcpMixEncUtils.ungzBase64(value));
							cctx.addStatExt(f.getName(), value);
							paramMap.put(f.getName(), value);
						}
						if (req.trim()) {
							value = value.trim();
							paramMap.put(f.getName(), value);
						}
					}
					Class<?> componentType = f.getType().getComponentType();
					if (struct == Struct.JSON) {// Json解析
						paramMap.remove(f.getName());
						f.setAccessible(true);
						try {
							if (!value.startsWith("{") && !value.startsWith("[")) {// json结构自动兼容
								if (req != null && req.aes()) {
									final byte[] aeskey = LcpThreadLocal.thCommandContext.get().getAesKey();
									value = LcpMixEncUtils.AesGzDecode(value, aeskey);
								} else {
									value = new String(LcpMixEncUtils.ungzBase64(value), "UTF-8");
								}
							}
							Object oT = gson.fromJson(value, f.getGenericType());
							f.set(targetObj, oT);
						} catch (Exception e) {
							String sig = "";
							CommandContext ctx = LcpThreadLocal.thCommandContext.get();
							if (ctx != null) {
								sig = ctx.getSig();
							}
							logger.error(String.format(
									"mapping(Map<String,Object>, T) gson.fromJson name[%s], value[%s] sig[%s]",
									f.getName(), value, sig), e);
						}
					} else if (componentType != null) {// 是数组
						paramMap.remove(f.getName());
						f.setAccessible(true);
						Class<?> type = componentType;
						if (type == Integer.class) {
							f.set(targetObj, StringToListUtils.toIntList(value).toArray(new Integer[0]));
						} else if (type == Long.class) {
							f.set(targetObj, StringToListUtils.toLongList(value).toArray(new Long[0]));
						} else if (type == String.class) {
							f.set(targetObj, StringToListUtils.toStringList(value, true).toArray(new String[0]));
						} else if (type == int.class) {
							f.set(targetObj, StringToListUtils.toIntArray(value));
						} else if (type == long.class) {
							f.set(targetObj, StringToListUtils.toLongArray(value));
						}
					} else if (f.getGenericType() instanceof ParameterizedType) {// 泛型
						Type[] actualTypes = ((ParameterizedType) f.getGenericType()).getActualTypeArguments();
						if (actualTypes.length == 1) {
							paramMap.remove(f.getName());
							f.setAccessible(true);
							Class<?> type = (Class<?>) actualTypes[0];
							if (type == Integer.class) {
								f.set(targetObj, StringToListUtils.toIntList(value));
							} else if (type == Long.class) {
								f.set(targetObj, StringToListUtils.toLongList(value));
							} else if (type == String.class) {
								f.set(targetObj, StringToListUtils.toStringList(value, true));
							}
						}
					}
				}
			}
			BeanUtils.populate(targetObj, paramMap);
			return targetObj;
		} catch (IllegalAccessException e) {
			logger.error("mapping(Map<String,Object>, T)", e);
		} catch (InvocationTargetException e) {
			logger.error("mapping(Map<String,Object>, T)", e);
		}
		return null;
	}

	/**
	 * 原对象转成指定的新对象实例
	 * 
	 * @param o
	 * @param clazzN
	 * @return
	 */
	public static <O, N> N castNew(O o, Class<N> clazzN) {
		if (o == null)
			return null;
		try {
			N n = clazzN.newInstance();
			return cast(o, n);
		} catch (InstantiationException e) {
			logger.error("cast(O, Class<N>)", e);
		} catch (IllegalAccessException e) {
			logger.error("cast(O, Class<N>)", e);
		}
		return null;
	}

	/**
	 * 原对象转成指定的对象
	 * 
	 * @param o
	 * @param n
	 * @return
	 */
	public static <O, N> N cast(O o, N n) {
		if (o == null || n == null)
			return null;
		List<Field> oFields = LcpFieldLoadHolder.getFields(o.getClass());
		Map<String, Object> values = new HashMap<String, Object>();
		for (Field f : oFields) {
			f.setAccessible(true);
			try {
				values.put(f.getName(), f.get(o));
			} catch (IllegalArgumentException e) {
				logger.error("cast(O, N)", e);
			} catch (IllegalAccessException e) {
				logger.error("cast(O, N)", e);
			}
		}
		return mapping(values, n);
	}

	/**
	 * 原List转换为新的List
	 * 
	 * @param os
	 * @param clazzN
	 * @return
	 */
	public static <O, N> List<N> cast(List<O> os, Class<N> clazzN) {
		return cast(os, clazzN, null);
	}

	/**
	 * 原数组转换为新的List
	 * 
	 * @param os
	 * @param clazzN
	 * @return
	 */
	public static <O, N> List<N> cast(O[] os, Class<N> clazzN) {
		return cast(os, clazzN, null);
	}

	/**
	 * 原List转换为新的List，并增加转换后的自定义处理
	 * 
	 * @param os
	 * @param clazzN
	 * @param after
	 * @return
	 */
	public static <O, N> List<N> cast(O[] os, Class<N> clazzN, IAfterCast<O, N> after) {
		if (os == null)
			return null;
		List<N> ns = new ArrayList<N>(os.length);
		for (O o : os) {
			try {
				N n = clazzN.newInstance();
				cast(o, n);
				if (after != null)
					after.cast(o, n);
				ns.add(n);
			} catch (InstantiationException e) {
				logger.error("cast(List<O>, Class<N>)", e);
				return null;
			} catch (IllegalAccessException e) {
				logger.error("cast(List<O>, Class<N>)", e);
				return null;
			}
		}
		return ns;
	}

	/**
	 * 原List转换为新的List，并增加转换后的自定义处理
	 * 
	 * @param os
	 * @param clazzN
	 * @param after
	 * @return
	 */
	public static <O, N> List<N> cast(List<O> os, Class<N> clazzN, IAfterCast<O, N> after) {
		if (os == null)
			return null;
		List<N> ns = new ArrayList<N>(os.size());
		for (O o : os) {
			try {
				N n = clazzN.newInstance();
				cast(o, n);
				if (after != null)
					after.cast(o, n);
				ns.add(n);
			} catch (InstantiationException e) {
				logger.error("cast(List<O>, Class<N>)", e);
				return null;
			} catch (IllegalAccessException e) {
				logger.error("cast(List<O>, Class<N>)", e);
				return null;
			}
		}
		return ns;
	}

	public static interface IAfterCast<O, N> {

		public void cast(O o, N n);
	}

	/**
	 * 把map中的value按sorted列表中的key顺序返回
	 * 
	 * @param sorted
	 * @param map
	 * @return
	 */
	public static <K, V> List<V> mapToList(List<K> sorted, Map<K, V> map) {
		if (sorted == null || map == null)
			return null;
		final List<V> values = new ArrayList<V>();
		if (sorted.isEmpty() || map.isEmpty())
			return values;
		for (K k : sorted) {
			if (k == null)
				continue;
			V v = map.get(k);
			if (v == null)
				continue;
			values.add(v);
		}
		return values;
	}
}
