package com.open.lcp.core.framework.api;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.open.lcp.core.framework.annotation.LcpDesc;
import com.open.lcp.core.framework.annotation.LcpReq;
import com.open.lcp.core.framework.annotation.LcpRequired;

/**
 * 对象生成ODML
 * 
 * @author
 */
public class ModelDefLanHolder {

	private static final Map<Class<?>, String> MDL_MAP = new ConcurrentHashMap<Class<?>, String>();

	private static final Map<Class<?>, String> TEXT_MDL_MAP = new ConcurrentHashMap<Class<?>, String>();

	/**
	 * 单个对象的Required注解校验
	 * 
	 * @param o
	 * @return
	 */
	public static String getMDL(Class<?> c) {
		if (c == null)
			return "";
		String mdl = MDL_MAP.get(c);
		if (mdl != null)
			return mdl;
		mdl = getMDL(c, 0).trim();
		MDL_MAP.put(c, mdl);
		return mdl;
	}

	private static final String TYPE_NAME_LNG = "java.lang.";

	private static String getMDL(Class<?> c, int layer) {
		StringBuilder sb = new StringBuilder();
		appendTab(sb, layer);
		sb.append("<class name=\"");
		sb.append(c.getName());
		LcpDesc desc = c.getAnnotation(LcpDesc.class);
		if (desc != null) {
			sb.append(" desc=\"");
			sb.append(desc.value().replaceAll("\\\\", "\\\\").replaceAll("\"", "\\\""));
		}
		sb.append("\">\r\n");
		List<Field> fs = FieldLoadHolder.getFields(c);
		int subLayer = layer + 1;
		for (Field f : fs) {
			Class<?> type = f.getType();
			String typeName = type.getName();
			LcpRequired req = f.getAnnotation(LcpRequired.class);
			appendTab(sb, subLayer);
			sb.append("<field name=\"");
			sb.append(f.getName());
			sb.append("\"");

			Class<?> componentType = f.getType().getComponentType();
			if (componentType != null) {// 是数组
				type = componentType;
				typeName = type.getName();
				sb.append(" listing=\"array\"");
			} else if (f.getGenericType() instanceof ParameterizedType) {// 泛型
				Type[] actualTypes = ((ParameterizedType) f.getGenericType()).getActualTypeArguments();
				if (actualTypes.length == 1) {
					type = (Class<?>) actualTypes[0];
					typeName = type.getName();
					sb.append(" listing=\"list\"");
				}
			}
			String shortTypeName = typeName;
			if (shortTypeName.startsWith(TYPE_NAME_LNG))
				shortTypeName = shortTypeName.substring(TYPE_NAME_LNG.length());
			sb.append(" type=\"");
			sb.append(shortTypeName);
			sb.append("\"");
			if (req != null) {
				sb.append(" required=\"");
				sb.append(req.value());
				sb.append("\" desc=\"");
				sb.append(req.desc().replaceAll("\\\\", "\\\\").replaceAll("\"", "\\\""));
				sb.append("\"");
				if (req.max() != 0 || req.min() != 0) {
					sb.append(" min=\"");
					sb.append(req.min());
					if (req.max() >= req.min()) {
						sb.append("\" max=\"");
						sb.append(req.max());
					}
					sb.append("\"");
				}
			}
			if (typeName.startsWith("java.")) {
				sb.append(" />\r\n");
				continue;
			}
			sb.append(" trim=\"");
			sb.append(req.trim());
			sb.append("\"");

			sb.append(">\r\n");
			sb.append(getMDL(type, subLayer + 1));
			appendTab(sb, subLayer);
			sb.append("</field>\r\n");
		}
		appendTab(sb, layer);
		sb.append("</class>\r\n");
		return sb.toString();
	}

	public static String getMtmlMDL(LcpReq lcpReq, Type type, Map<Integer, Set<String>> errorCodes) {
		if (lcpReq == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='0' cellspacing='0' cellpadding='0' class='mcp_model'>\r\n<caption>");
		sb.append("独立参数");
		sb.append("</caption>\r\n");
		sb.append(
				"<tr><th>Required</th><th>Name</th><th>Type</th><th>min</th><th>max</th><th>trim</th><th>aes加密</th><th>gz</th><th>Description</th><th>errorCode</th><th>errorMsg</th></tr>\r\n");
		Class<?> clazz = null;
		if (type instanceof Class<?>) {
			clazz = (Class<?>) type;
		}
		boolean isRequired = lcpReq.required();
		sb.append("<tr>");
		if (isRequired) {
			sb.append("<td>required</td>");
		} else {
			sb.append("<td>optional</td>");
		}
		sb.append("<td>").append(lcpReq.name()).append("</td>");// name
		Class<?> componentType = null;
		if (clazz != null) {
			componentType = clazz.getComponentType();
		}
		if (componentType != null) {// 是数组
			Class<?> arraytype = componentType;
			sb.append("<td>");// type
			sb.append(arraytype.getSimpleName());
			sb.append("[]</td>");
		} else if (type instanceof ParameterizedType) {// 泛型
			Type[] actualTypes = ((ParameterizedType) type).getActualTypeArguments();
			if (actualTypes.length == 1) {
				clazz = (Class<?>) actualTypes[0];
			}
			sb.append("<td>List&lt;");// type
			sb.append(clazz.getSimpleName());
			sb.append("&gt;</td>");
		} else {
			if (clazz != null) {
				sb.append(String.format("<td>%s</td>", clazz.getSimpleName()));
			} else {
				sb.append("<td>未知类型</td>");
			}
		}
		if ((lcpReq.max() != 0 || lcpReq.min() != 0)) {// min max
			sb.append("<td>").append(lcpReq.min()).append("</td><td>");
			if (lcpReq.max() >= lcpReq.min()) {
				sb.append(lcpReq.max());
			} else {
				sb.append("&nbsp;");
			}
			sb.append("</td>");
		} else {// min max
			sb.append("<td>&nbsp;</td><td>&nbsp;</td>");
		}
		if (lcpReq.trim()) {
			sb.append("<td>是</td>");
		} else {
			sb.append("<td>否</td>");
		}
		sb.append("<td>否</td>");// AES，单个参数不支持aes，所以只有否。
		sb.append("<td>否</td>");// GZ，单个参数不支持gz，所以只有否。
		if (lcpReq.desc() != null && lcpReq.desc().trim().length() > 0) {
			sb.append("<td>").append(lcpReq.desc().trim().replaceAll("\\\\", "\\\\").replaceAll("\"", "\\\""));
			sb.append("</td>");
		} else {
			sb.append("<td>&nbsp;</td>");
		}
		if (lcpReq.errorCode() > 0) {
			String extInfo = "";// 错误码被复用的次数。
			if (errorCodes != null) {
				Set<String> ls = errorCodes.get(lcpReq.errorCode());
				if (ls != null && ls.size() > 1) {
					StringBuilder sbExtInfo = new StringBuilder("<br/><font color='blue'>此错误码被复用");
					sbExtInfo.append(ls.size());
					sbExtInfo.append("次：");
					for (String l : ls) {
						sbExtInfo.append("<br/>");
						sbExtInfo.append(l);
					}
					sbExtInfo.append("</font>");
					extInfo = sbExtInfo.toString();
				}
			}
			if (lcpReq.errorCode() >= 7000) {
				sb.append("<td>");
				sb.append(lcpReq.errorCode());
				sb.append(extInfo);
				sb.append("</td>");
			} else {
				sb.append("<td>");
				sb.append(lcpReq.errorCode());
				sb.append("<br/><font color='red'>注解指定错误码不应小于7000</font>");
				sb.append(extInfo);
				sb.append("</td>");
			}
			if (lcpReq.errorMsg() != null && !lcpReq.errorMsg().isEmpty()) {
				sb.append("<td>");
				sb.append(lcpReq.errorMsg());
				sb.append("</td>");
			} else {
				sb.append("<td>&nbsp;</td>");
			}
		} else {
			sb.append("<td>&nbsp;</td><td>&nbsp;</td>");
		}
		sb.append("</tr>\r\n");
		sb.append("</table>");
		return sb.toString();
	}

	public static String getMtmlMDL(Class<?> c, Map<Integer, Set<String>> errorCodes) {
		if (c == null)
			return null;
		String text = TEXT_MDL_MAP.get(c);
		if (text != null)
			return text;
		Data data = new Data();
		data.add(c);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < data.size(); i++) {
			if (i > 0) {
				sb.append("<br/>\r\n");
			}
			sb.append(getMtmlMDL(data.get(i), data, errorCodes));
		}
		text = sb.toString().trim();
		TEXT_MDL_MAP.put(c, text);
		return text;
	}

	private static String getMtmlMDL(Class<?> c, Data data, Map<Integer, Set<String>> errorCodes) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table border='0' cellspacing='0' cellpadding='0' class='mcp_model'>\r\n<caption>");
		sb.append(c.getSimpleName());
		sb.append(" | ");
		LcpDesc desc = c.getAnnotation(LcpDesc.class);
		if (desc != null) {
			sb.append(desc.value().replaceAll("\\\\", "\\\\").replaceAll("\"", "\\\""));
		}
		sb.append("</caption>\r\n");
		List<Field> fs = FieldLoadHolder.getFields(c);
		List<Field> fsRequired = new ArrayList<Field>();
		List<Field> fsNotRequired = new ArrayList<Field>();
		{
			for (Field f : fs) {
				LcpRequired req = f.getAnnotation(LcpRequired.class);
				if (req != null && req.value()) {
					fsRequired.add(f);
				} else {
					fsNotRequired.add(f);
				}
			}
		}
		final int requiredSize = fsRequired.size();
		final int notRequiredSize = fsNotRequired.size();
		fs = fsRequired;
		fs.addAll(fsNotRequired);
		sb.append(
				"<tr><th>Required</th><th>Name</th><th>Type</th><th>min</th><th>max</th><th>trim</th><th>aes加密</th><th>gz</th><th>struct</th><th>Description</th><th>errorCode</th><th>errorMsg</th></tr>\r\n");
		boolean requiredBegin = false;
		boolean notRequiredBegin = false;
		for (Field f : fs) {
			Class<?> type = f.getType();
			String typeName = type.getName();
			LcpRequired req = f.getAnnotation(LcpRequired.class);
			boolean isRequired = req != null && req.value();
			sb.append("<tr>");
			if (isRequired && !requiredBegin) {
				requiredBegin = true;
				if (requiredSize > 1) {
					sb.append("<td rowspan=\"");
					sb.append(requiredSize);
					sb.append("\">required</td>");
				} else {
					sb.append("<td>required</td>");
				}
			} else if (!isRequired && !notRequiredBegin) {
				notRequiredBegin = true;
				if (notRequiredSize > 1) {
					sb.append("<td rowspan=\"");
					sb.append(notRequiredSize);
					sb.append("\">optional</td>");
				} else {
					sb.append("<td>optional</td>");
				}
			}
			sb.append("<td>").append(f.getName()).append("</td>");// name
			final Class<?> componentType = f.getType().getComponentType();
			if (componentType != null) {// 是数组
				type = componentType;
				typeName = type.getName();
				sb.append("<td>");// type
				sb.append(type.getSimpleName());
				sb.append("[]</td>");
			} else if (f.getGenericType() instanceof ParameterizedType) {// 泛型
				String simpleName = null;
				Type[] actualTypes = ((ParameterizedType) f.getGenericType()).getActualTypeArguments();
				if (actualTypes.length == 1) {
					if (ParameterizedType.class.isInstance(actualTypes[0])) {// 泛型中还是泛型
						ParameterizedType pt2 = (ParameterizedType) actualTypes[0];
						Class<?> rt = (Class<?>) pt2.getRawType();
						StringBuilder sbTypeName = new StringBuilder();
						sbTypeName.append(rt.getSimpleName());
						sbTypeName.append("&lt;");
						actualTypes = pt2.getActualTypeArguments();
						for (int i = 0; i < actualTypes.length; i++) {
							if (i > 0) {
								sbTypeName.append(",");
							}
							Class<?> at = (Class<?>) actualTypes[i];
							sbTypeName.append(at.getSimpleName());
							if (!at.getName().startsWith("java.")) {
								data.add(at);
							}
						}
						sbTypeName.append("&gt;");
						simpleName = sbTypeName.toString();
						// } else if
						// (TypeVariable.class.isInstance(actualTypes[0])) {
						// TypeVariable<?> tv = (TypeVariable<>) actualTypes[0];
						// String name = tv.getName();
						// Type[] b = tv.getBounds();
						// GenericDeclaration gd = tv.getGenericDeclaration();
						// System.out.println(String.format("Name:%s,
						// Bounds[]:%s, GenericDeclaration:%s", name,
						// Arrays.toString(b), gd));
					} else {
						type = (Class<?>) actualTypes[0];
						typeName = type.getName();
						simpleName = type.getSimpleName();
					}
				}
				sb.append("<td>List&lt;");// type
				sb.append(simpleName);
				sb.append("&gt;</td>");
			} else {
				sb.append("<td>");// type
				sb.append(type.getSimpleName());
				sb.append("</td>");
			}
			if (req != null && (req.max() != 0 || req.min() != 0)) {// min max
				sb.append("<td>").append(req.min()).append("</td><td>");
				if (req.max() >= req.min()) {
					sb.append(req.max());
				} else {
					sb.append("&nbsp;");
				}
				sb.append("</td>");
			} else {// min max
				sb.append("<td>&nbsp;</td><td>&nbsp;</td>");
			}
			// trim
			if (req == null) {
				sb.append("<td>&nbsp;</td>");
			} else if (req.trim()) {
				sb.append("<td>是</td>");
			} else {
				sb.append("<td>否</td>");
			}
			// aes
			if (req == null) {
				sb.append("<td>&nbsp;</td>");
			} else if (req.aes()) {
				sb.append("<td>是</td>");
			} else {
				sb.append("<td>否</td>");
			}
			// gz
			if (req == null) {
				sb.append("<td>&nbsp;</td>");
			} else if (req.gz()) {
				sb.append("<td>是</td>");
			} else {
				sb.append("<td>否</td>");
			}

			if (req == null || req.struct() == null) {
				sb.append("<td>&nbsp;</td>");
			} else {
				sb.append(String.format("<td>%s</td>", req.struct().name()));
			}
			if (req != null && req.desc() != null && req.desc().trim().length() > 0) {
				sb.append("<td>").append(req.desc().trim().replaceAll("\\\\", "\\\\").replaceAll("\"", "\\\""));
				sb.append("</td>");
			} else {
				sb.append("<td>&nbsp;</td>");
			}

			if (req != null && req.errorCode() > 0) {
				String extInfo = "";// 错误码被复用的次数。
				if (errorCodes != null) {
					Set<String> ls = errorCodes.get(req.errorCode());
					if (ls != null && ls.size() > 1) {
						StringBuilder sbExtInfo = new StringBuilder("<br/><font color='blue'>此错误码被复用");
						sbExtInfo.append(ls.size());
						sbExtInfo.append("次：");
						for (String l : ls) {
							sbExtInfo.append("<br/>");
							sbExtInfo.append(l);
						}
						sbExtInfo.append("</font>");
						extInfo = sbExtInfo.toString();
					}
				}
				if (req.errorCode() >= 7000) {
					sb.append("<td>");
					sb.append(req.errorCode());
					sb.append(extInfo);
					sb.append("</td>");
				} else {
					sb.append("<td>");
					sb.append(req.errorCode());
					sb.append("<br/><font color='red'>注解指定错误码不应小于7000</font>");
					sb.append(extInfo);
					sb.append("</td>");
				}
				if (req.errorMsg() != null && !req.errorMsg().isEmpty()) {
					sb.append("<td>");
					sb.append(req.errorMsg());
					sb.append("</td>");
				} else {
					sb.append("<td>&nbsp;</td>");
				}
			} else {
				sb.append("<td>&nbsp;</td><td>&nbsp;</td>");
			}
			sb.append("</tr>\r\n");
			if (typeName.startsWith("java.") || !typeName.contains(".")) {
				continue;
			}
			data.add(type);
		}
		sb.append("</table>");
		return sb.toString();
	}

	private static void appendTab(StringBuilder sb, int count) {
		for (int i = 0; i < count; i++)
			sb.append("\t");
	}

	private static final class Data {

		private Set<String> set = new HashSet<String>();

		private List<Class<?>> list = new ArrayList<Class<?>>();

		public void add(Class<?> c) {
			if (set.contains(c.getName())) {
				return;
			}
			set.add(c.getName());
			list.add(c);
		}

		public int size() {
			return list.size();
		}

		public Class<?> get(int index) {
			return list.get(index);
		}
	}
}
