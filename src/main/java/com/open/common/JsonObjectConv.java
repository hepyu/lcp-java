package com.open.common;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nutz.ssdb4j.spi.ObjectConv;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonObjectConv implements ObjectConv {
	public static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeSpecialFloatingPointValues().create();
	public static final Charset charset = Charset.forName("UTF-8");

	@Override
	public byte[] bytes(Object obj) {
		if (obj == null) {
			return null;
		}
		if (byte[].class == obj.getClass()) {
			return (byte[]) obj;
		}
		if (obj.getClass().getName().startsWith("java.lang.")) {
			return toBytes(String.valueOf(obj));
		}
		String json = gson.toJson(obj);
		return json.getBytes(charset);
	}

	@Override
	public byte[][] bytess(Object... objs) {
		List<byte[]> ls = new ArrayList<byte[]>();
		for (Object o : objs) {
			if (o == null) {
				continue;
			}
			ls.add(bytes(o));
		}
		return ls.toArray(new byte[0][0]);
	}

	@SuppressWarnings("unchecked")
	public <T> T toObject(byte[] bts, Class<T> t) {
		if (bts == null) {
			return null;
		}
		if (t == byte[].class) {
			return (T) bts;
		}
		if (String.class == t) {
			return (T) toString(bts);
		} else if (Integer.class == t || int.class == t) {
			return (T) Integer.valueOf(toString(bts));
		} else if (Long.class == t || long.class == t) {
			return (T) Long.valueOf(toString(bts));
		} else if (double.class == t || Double.class == t) {
			return (T) Double.valueOf(toString(bts));
		} else if (Boolean.class == t || boolean.class == t) {
			return (T) Boolean.valueOf(toString(bts));
		}
		return gson.fromJson(toString(bts), t);
	}

	@SuppressWarnings("unchecked")
	public <T> T toObject(byte[] bts, int offset, int length, Class<T> t) {
		if (bts == null || bts.length < offset + length) {
			return null;
		}
		if (t == byte[].class) {
			return (T) Arrays.copyOfRange(bts, offset, offset + length);
		}
		final String json = new String(bts, offset, length, charset);
		if (String.class == t) {
			return (T) json;
		} else if (Integer.class == t || int.class == t) {
			return (T) Integer.valueOf(json);
		} else if (Long.class == t || long.class == t) {
			return (T) Long.valueOf(json);
		} else if (double.class == t || Double.class == t) {
			return (T) Double.valueOf(json);
		} else if (Boolean.class == t || boolean.class == t) {
			return (T) Boolean.valueOf(json);
		}
		return gson.fromJson(json, t);
	}

	public String toString(byte[] bts) {
		if (bts == null) {
			return null;
		}
		return new String(bts, charset);
	}

	public String toString(byte[] bts, int offset, int length) {
		if (bts == null) {
			return null;
		}
		return new String(bts, offset, length, charset);
	}

	public byte[] toBytes(String str) {
		if (str == null) {
			return null;
		}
		return str.getBytes(charset);
	}
}
