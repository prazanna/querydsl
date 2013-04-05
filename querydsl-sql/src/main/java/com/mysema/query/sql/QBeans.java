/*
 * Copyright 2011, Mysema Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mysema.query.sql;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mysema.query.types.Expression;
import com.mysema.query.types.ExpressionBase;
import com.mysema.query.types.FactoryExpression;
import com.mysema.query.types.QBean;
import com.mysema.query.types.Visitor;
import com.mysema.util.ArrayUtils;

/**
 * Expression used to project a list of beans
 * 
 * @author luis
 */
public class QBeans extends ExpressionBase<Beans> implements FactoryExpression<Beans> {

    private static final long serialVersionUID = -4411839816134215923L;
    
    private final ImmutableMap<RelationalPath<?>, QBean<?>> qBeans;
    
    private final ImmutableList<Expression<?>> expressions;

    @SuppressWarnings("unchecked")
    public QBeans(RelationalPath<?>... beanPaths) {
        super(Beans.class);
        try {
            final ImmutableList.Builder<Expression<?>> listBuilder = ImmutableList.builder();
            final ImmutableMap.Builder<RelationalPath<?>, QBean<?>> mapBuilder = ImmutableMap.builder();
            for (RelationalPath path : beanPaths) {
                Map<String, Expression<?>> bindings = new LinkedHashMap<String, Expression<?>>();
                for (Field field : path.getClass().getFields()) {
                    if (Expression.class.isAssignableFrom(field.getType()) && !Modifier.isStatic(field.getModifiers())) {
                        field.setAccessible(true);
                        Expression<?> column = (Expression<?>) field.get(path);
                        bindings.put(field.getName(), column);
                        listBuilder.add(column);
                    }
                }
                mapBuilder.put(path, new QBean<Object>(path.getType(), bindings));
            }
            expressions = listBuilder.build();
            qBeans = mapBuilder.build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public List<Expression<?>> getArgs() {
        return expressions;
    }

    @Override
    public Beans newInstance(Object... args) {
        int offset = 0;
        Map<RelationalPath<?>, Object> beans = new HashMap<RelationalPath<?>, Object>();
        for (Map.Entry<RelationalPath<?>, QBean<?>> entry : qBeans.entrySet()) {
            RelationalPath<?> path = entry.getKey();
            QBean<?> qBean = entry.getValue();
            int argsSize = qBean.getArgs().size();
            Object[] subArgs = ArrayUtils.subarray(args, offset, offset + argsSize);
            beans.put(path, qBean.newInstance(subArgs));
            offset += argsSize;
        }
        return new Beans(beans);
    }

}