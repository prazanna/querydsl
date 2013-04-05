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
package com.mysema.query.types;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

/**
 * Default implementation of the {@link TemplateExpression} interface
 * 
 * @author tiwe
 *
 * @param <T> expression type
 */
@Immutable
public class TemplateExpressionImpl<T> extends ExpressionBase<T> implements TemplateExpression<T> {

    private static final long serialVersionUID = 6951623726800809083L;

    private final ImmutableList<?> args;

    private final Template template;

    public static <C> Expression<C> create(Class<C> cl, String template) {
        return new TemplateExpressionImpl<C>(cl, TemplateFactory.DEFAULT.create(template), ImmutableList.of());
    }
    
    public static <C> Expression<C> create(Class<C> cl, String template, Object one) {
        return new TemplateExpressionImpl<C>(cl, TemplateFactory.DEFAULT.create(template), ImmutableList.of(one));
    }
    
    public static <C> Expression<C> create(Class<C> cl, String template, Object one, Object two) {
        return new TemplateExpressionImpl<C>(cl, TemplateFactory.DEFAULT.create(template), ImmutableList.of(one, two));
    }
    
    public static <C> Expression<C> create(Class<C> cl, String template, Object... args) {
        return new TemplateExpressionImpl<C>(cl, TemplateFactory.DEFAULT.create(template), args);
    }

    public static <C> Expression<C> create(Class<C> cl, Template template, Object... args) {
        return new TemplateExpressionImpl<C>(cl, template, args);
    }

    protected TemplateExpressionImpl(Class<? extends T> type, Template template, Object... args) {
        this(type, template, ImmutableList.copyOf(args));
    }    
    
    public TemplateExpressionImpl(Class<? extends T> type, Template template, ImmutableList<?> args) {
        super(type);
        this.args = args;
        this.template = template;
    }

    @Override
    public final Object getArg(int index) {
        return getArgs().get(index);
    }

    @Override
    public final List<?> getArgs() {
        return args;
    }

    @Override
    public final Template getTemplate() {
        return template;
    }

    @Override
    public final boolean equals(Object o) {
       if (o == this) {
           return true;
       } else if (o instanceof TemplateExpression) {
           TemplateExpression<?> c = (TemplateExpression<?>)o;
           return c.getTemplate().equals(template)
               && c.getType().equals(getType());
       } else {
           return false;
       }
    }
    
    @Override
    public final <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

}
