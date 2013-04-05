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
package com.mysema.query.jpa;

import com.mysema.query.types.CollectionExpression;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Expression;
import com.mysema.query.types.Ops;
import com.mysema.query.types.expr.ComparableExpression;
import com.mysema.query.types.expr.ComparableOperation;
import com.mysema.query.types.expr.StringExpression;
import com.mysema.query.types.expr.StringOperation;

/**
 * JPAExpressions provides factory methods for JPQL specific operations
 * elements.
 *
 * @author tiwe
 */
@SuppressWarnings("unchecked")
public final class JPAExpressions {

    public static <A extends Comparable<? super A>> ComparableExpression<A> avg(CollectionExpression<?,A> col) {
        return ComparableOperation.create((Class)col.getParameter(0), Ops.QuantOps.AVG_IN_COL, (Expression<?>)col);
    }

    public static <A extends Comparable<? super A>> ComparableExpression<A> max(CollectionExpression<?,A> left) {
        return ComparableOperation.create((Class)left.getParameter(0), Ops.QuantOps.MAX_IN_COL, (Expression<?>)left);
    }

    public static <A extends Comparable<? super A>> ComparableExpression<A> min(CollectionExpression<?,A> left) {
        return ComparableOperation.create((Class)left.getParameter(0), Ops.QuantOps.MIN_IN_COL, (Expression<?>)left);
    }

    public static StringExpression type(EntityPath<?> path) {
        return StringOperation.create(JPQLTemplates.TYPE, path);
    }
    
    private JPAExpressions() {}

}
