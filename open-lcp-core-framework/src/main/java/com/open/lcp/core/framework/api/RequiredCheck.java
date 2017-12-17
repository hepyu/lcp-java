package com.open.lcp.core.framework.api;

import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.open.lcp.core.api.annotation.LcpHttpRequest;
import com.open.lcp.core.api.annotation.LcpParamRequired;

/**
 * 对象校验结果
 * 
 * @author 
 */
public class RequiredCheck {

    /**
     * Logger for this class
     */
    private static final Log logger = LogFactory.getLog(RequiredCheck.class);

    private RequiredCheck() {
    }

    public static final RequiredCheck buildMcpReq(Object value, LcpHttpRequest lcpReq, ErrorType errorType) {
        RequiredCheck requiredCheck = new RequiredCheck();
        requiredCheck.value = value;
        requiredCheck.lcpReq = lcpReq;
        requiredCheck.errorType = errorType;
        return requiredCheck;
    }

    public static final RequiredCheck build(Object value, Field field, ErrorType errorType) {
        RequiredCheck requiredCheck = new RequiredCheck();
        requiredCheck.value = value;
        requiredCheck.field = field;
        if (requiredCheck.field != null) {
            requiredCheck.lcpRequired = requiredCheck.field.getAnnotation(LcpParamRequired.class);
        }
        requiredCheck.errorType = errorType;
        return requiredCheck;
    }

    public static final RequiredCheck buildArrayHasNull(Object value, Field field, int index) {
        RequiredCheck requiredCheck = new RequiredCheck();
        requiredCheck.value = value;
        requiredCheck.field = field;
        if (requiredCheck.field != null) {
            requiredCheck.lcpRequired = requiredCheck.field.getAnnotation(LcpParamRequired.class);
        }
        requiredCheck.errorType = ErrorType.ArrayHasNull;
        requiredCheck.arrayIndex = index;
        return requiredCheck;
    }

    public enum ErrorType {
        Required, MaxLimited, MinLimited, ArrayHasNull, Pass
    };

    private Object value;

    private Field field;

    private LcpHttpRequest lcpReq;

    private ErrorType errorType;

    private LcpParamRequired lcpRequired = null;

    private int arrayIndex;

    public Field getField() {
        return field;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public Object getValue() {
        return value;
    }

    public LcpParamRequired getRequired() {
        return lcpRequired;
    }

    public String toMessage() {
        String cName = "";
        if (this.value != null) {
            cName = this.value.getClass().getName();
        } else if (lcpReq != null) {
            cName = "方法内参数";
        }
        String fName = "";
        if (this.field != null) {
            fName = this.field.getName();
        } else if (lcpReq != null) {
            fName = lcpReq.name();
        }
        Object value = null;
        if (this.value != null && this.field != null) {
            try {
                value = this.field.get(this.value);
            } catch (Exception ex) {
                logger.warn("RequiredCheck.toMessage()", ex);
            }
        }
        long max = 0;
        long min = 0;
        if (lcpRequired != null) {
            max = lcpRequired.max();
            min = lcpRequired.min();
        } else if (lcpReq != null) {
            max = lcpReq.max();
            min = lcpReq.min();
        }
        switch (errorType) {
            case Required:
                if (field == null) {
                    return "当前对象为null";
                }
                return cName + "的" + fName + "值为null";
            case MaxLimited:
                return String.format("%s的%s属性值%s大于上限%s", cName, fName, value, max);
            case MinLimited:
                return String.format("%s的%s属性值%s小于下限%s", cName, fName, value, min);
            case ArrayHasNull:
                return String.format("%s的%s属性值%s中第%s项为null值", cName, fName, value, arrayIndex);
            case Pass:
                return "检验通过";
        }
        return "未知";
    }

    @Override
    public String toString() {
        return "RequiredCheck [value=" + value + ", field=" + field + ", errorType=" + errorType + "]";
    }

}
