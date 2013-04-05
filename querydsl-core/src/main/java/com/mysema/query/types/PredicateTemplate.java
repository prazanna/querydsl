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

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

/**
 * PredicateTemplate provides a Boolean typed TemplateExpression implementation 
 * 
 * @author tiwe
 *
 */
@Immutable
public class PredicateTemplate extends TemplateExpressionImpl<Boolean> implements Predicate{
    
    private static final long serialVersionUID = -5371430939203772072L;

    @Nullable
    private volatile Predicate not;
    
    public PredicateTemplate(String template, Object... args) {
        super(Boolean.class, TemplateFactory.DEFAULT.create(template), args);
    }
    
    public PredicateTemplate(Template template, Object... args) {
        super(Boolean.class, template, args);
    }

    public PredicateTemplate(Template template, ImmutableList<?> args) {
        super(Boolean.class, template, args);
    }
    
    @Override
    public Predicate not() {
        if (not == null) {
            not = PredicateOperation.create(Ops.NOT, this);
        }
        return not;
    }

}
