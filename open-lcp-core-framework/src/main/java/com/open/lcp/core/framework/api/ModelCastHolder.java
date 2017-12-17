package com.open.lcp.core.framework.api;

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
import com.open.lcp.core.common.util.StringToListUtils;
import com.open.lcp.core.api.annotation.LcpHttpRequest;
import com.open.lcp.core.api.annotation.LcpParamRequired;
import com.open.lcp.core.api.annotation.LcpParamRequired.Struct;
import com.open.lcp.core.api.command.CommandContext;
import com.open.lcp.core.framework.util.LcpMixEncUtil;

public class ModelCastHolder {

	/**
	 * Logger for this class
	 */
	private static final Log logger = LogFactory.getLog(ModelCastHolder.class);

	private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

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

	public static <T> Object mappingParameter(Map<String, ?> paramMap, LcpHttpRequest lcpReq, Type t) {
		if (paramMap == null || lcpReq == null) {
			return null;
		}
		if (!paramMap.containsKey(lcpReq.name())) {// 鏃犲弬鏃讹細闈炲繀閫夎繑鍥為粯璁わ紝蹇呴�夎繑鍥瀗ull
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
		if (clazz != null && componentType != null) {// 鏄暟缁�
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
		} else if (t instanceof ParameterizedType) {// 娉涘瀷
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
	 * 鐢╩ap涓殑鏁版嵁琛ュ厖鐩爣瀵硅薄
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
				List<Field> fs = FieldLoadHolder.getFields(targetObj.getClass());
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
					final LcpParamRequired req = f.getAnnotation(LcpParamRequired.class);
					final LcpParamRequired.Struct struct = req == null ? null : req.struct();
					if (req != null) {
						if (req.trim()) {
							value = value.trim();
						}
						final CommandContext cctx = LcpThreadLocal.thCommandContext.get();
						if (req.aes()) {
							final byte[] aesKey = cctx.getAesKey();
							if (req.gz()) {
								value = LcpMixEncUtil.AesGzDecode(value, aesKey);
							} else {
								value = LcpMixEncUtil.AesDecode(value, aesKey);
							}
							cctx.addStatExt(f.getName(), value);
							paramMap.put(f.getName(), value);
						} else if (req.gz()) {
							value = new String(LcpMixEncUtil.ungzBase64(value));
							cctx.addStatExt(f.getName(), value);
							paramMap.put(f.getName(), value);
						}
						if (req.trim()) {
							value = value.trim();
							paramMap.put(f.getName(), value);
						}
					}
					Class<?> componentType = f.getType().getComponentType();
					if (struct == Struct.JSON) {// Json瑙ｆ瀽
						paramMap.remove(f.getName());
						f.setAccessible(true);
						try {
							if (!value.startsWith("{") && !value.startsWith("[")) {// json缁撴瀯鑷姩鍏煎
								if (req != null && req.aes()) {
									final byte[] aeskey = LcpThreadLocal.thCommandContext.get().getAesKey();
									value = LcpMixEncUtil.AesGzDecode(value, aeskey);
								} else {
									value = new String(LcpMixEncUtil.ungzBase64(value), "UTF-8");
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
					} else if (componentType != null) {// 鏄暟缁�
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
					} else if (f.getGenericType() instanceof ParameterizedType) {// 娉涘瀷
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
	 * 鍘熷璞¤浆鎴愭寚瀹氱殑鏂板璞″疄渚�
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
	 * 鍘熷璞¤浆鎴愭寚瀹氱殑瀵硅薄
	 * 
	 * @param o
	 * @param n
	 * @return
	 */
	public static <O, N> N cast(O o, N n) {
		if (o == null || n == null)
			return null;
		List<Field> oFields = FieldLoadHolder.getFields(o.getClass());
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
	 * 鍘烲ist杞崲涓烘柊鐨凩ist
	 * 
	 * @param os
	 * @param clazzN
	 * @return
	 */
	public static <O, N> List<N> cast(List<O> os, Class<N> clazzN) {
		return cast(os, clazzN, null);
	}

	/**
	 * 鍘熸暟缁勮浆鎹负鏂扮殑List
	 * 
	 * @param os
	 * @param clazzN
	 * @return
	 */
	public static <O, N> List<N> cast(O[] os, Class<N> clazzN) {
		return cast(os, clazzN, null);
	}

	/**
	 * 鍘烲ist杞崲涓烘柊鐨凩ist锛屽苟澧炲姞杞崲鍚庣殑鑷畾涔夊鐞�
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
	 * 鍘烲ist杞崲涓烘柊鐨凩ist锛屽苟澧炲姞杞崲鍚庣殑鑷畾涔夊鐞�
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
	 * 鎶妋ap涓殑value鎸塻orted鍒楄〃涓殑key椤哄簭杩斿洖
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
