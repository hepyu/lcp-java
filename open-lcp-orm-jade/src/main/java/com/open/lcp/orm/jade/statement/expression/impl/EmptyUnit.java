package com.open.lcp.orm.jade.statement.expression.impl;

import com.open.lcp.orm.jade.statement.expression.ExprResolver;
import com.open.lcp.orm.jade.statement.expression.ExqlContext;
import com.open.lcp.orm.jade.statement.expression.ExqlUnit;

/**
 * 输出空白的语句单元, 代替空的表达式。
 * 
 * @author han.liao
 */
public class EmptyUnit implements ExqlUnit {

    @Override
    public boolean isValid(ExprResolver exprResolver) {
        // Empty unit is always valid.
        return true;
    }

    @Override
    public void fill(ExqlContext exqlContext, ExprResolver exprResolver) throws Exception {
        // Do nothing.
    }
}
