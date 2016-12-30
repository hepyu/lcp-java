package com.open.lcp.framework.core.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.BeanUtils;

/**
 * 属性加载
 * 
 * @author 
 */
public class LcpFieldLoadHolder {

    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(LcpFieldLoadHolder.class);

    private static final Map<Class<?>, List<Field>> fieldMap = new ConcurrentHashMap<Class<?>, List<Field>>();

    private static final Map<Class<?>, Map<String, Field>> fieldNameMap = new ConcurrentHashMap<Class<?>, Map<String, Field>>();

    /**
     * 取当前对象的所有属性，包括继承过来的属性
     * 
     * @param clazz
     * @return
     */
    public static final List<Field> getFields(Class<?> clazz) {
        List<Field> fieldsAll = fieldMap.get(clazz);
        if (fieldsAll == null) {
            fieldsAll = new ArrayList<Field>();
            Class<?> c = clazz;
            while (c != null) {
                Field[] arrayFields = c.getDeclaredFields();
                final List<Field> fields = new ArrayList<Field>();
                for (Field f : arrayFields) {
                    if (Modifier.isFinal(f.getModifiers())) continue;
                    if (Modifier.isStatic(f.getModifiers())) continue;
                    if (Modifier.isTransient(f.getModifiers())) continue;
                    fields.add(f);
                }
                fieldsAll.addAll(0, fields);
                c = c.getSuperclass();
            }
            fieldMap.put(clazz, fieldsAll);
        }
        return fieldsAll;
    }

    /**
     * 取当前对象的所有属性，包括继承过来的属性
     * 
     * @param clazz
     * @return
     */
    public static final Map<String, Field> getFieldsMap(Class<?> clazz) {
        Map<String, Field> fields = fieldNameMap.get(clazz);
        if (fields == null) {
            fields = new HashMap<String, Field>();
            Class<?> c = clazz;
            while (c != null) {
                Field[] arrayFields = c.getDeclaredFields();
                for (Field f : arrayFields)
                    fields.put(f.getName(), f);
                c = c.getSuperclass();
            }
            fieldNameMap.put(clazz, fields);
        }
        return fields;
    }

    public static final String toString(Object o) {
        if (o == null) return null;
        try {
            Map<?, ?> map = BeanUtils.describe(o);
            if (map == null) return null;
            return map.toString();
        } catch (IllegalAccessException e) {
            logger.error("toString(Object)", e); //$NON-NLS-1$
        } catch (InvocationTargetException e) {
            logger.error("toString(Object)", e); //$NON-NLS-1$
        } catch (NoSuchMethodException e) {
            logger.error("toString(Object)", e); //$NON-NLS-1$
        }
        return null;
    }
}
