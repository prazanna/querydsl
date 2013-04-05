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
package com.mysema.query.types.expr;

import javax.annotation.Nullable;

import com.mysema.query.types.CollectionExpression;
import com.mysema.query.types.ConstantImpl;
import com.mysema.query.types.Expression;
import com.mysema.query.types.ExpressionUtils;
import com.mysema.query.types.Operator;
import com.mysema.query.types.Ops;
import com.mysema.query.types.Path;
import com.mysema.query.types.PathImpl;

/**
 * ComparableExpression extends {@link ComparableExpressionBase} to provide comparison methods.
 *
 * @author tiwe
 *
 * @param <T> expression type
 */
@SuppressWarnings({"unchecked"})
public abstract class ComparableExpression<T extends Comparable> extends ComparableExpressionBase<T> {

    private static final long serialVersionUID = 5761359576767404270L;

    public ComparableExpression(Expression<T> mixin) {
        super(mixin);
    }
    
    @Override
    public ComparableExpression<T> as(Path<T> alias) {
        return ComparableOperation.create(getType(),(Operator)Ops.ALIAS, mixin, alias);
    }
    
    @Override
    public ComparableExpression<T> as(String alias) {
        return as(new PathImpl<T>(getType(), alias));
    }

    /**
     * Get a <code>from &lt;= this &lt;= to</code> expression
     *
     * @param from
     * @param to
     * @return
     */
    public final BooleanExpression between(@Nullable T from, @Nullable T to) {
        if (from == null) {
            if (to != null) {
                return BooleanOperation.create(Ops.LOE, mixin, new ConstantImpl<T>(to));
            } else {
                throw new IllegalArgumentException("Either from or to needs to be non-null");
            }
        } else if (to == null) {
            return BooleanOperation.create(Ops.GOE, mixin, new ConstantImpl<T>(from));
        } else {
            return BooleanOperation.create(Ops.BETWEEN, mixin, new ConstantImpl<T>(from), new ConstantImpl<T>(to));    
        }        
    }

    /**
     * Get a <code>first &lt;= this &lt;= second</code> expression
     *
     * @param from
     * @param to
     * @return
     */
    public final BooleanExpression between(@Nullable Expression<T> from, @Nullable Expression<T> to) {
        if (from == null) {
            if (to != null) {
                return BooleanOperation.create(Ops.LOE, mixin, to);
            } else {
                throw new IllegalArgumentException("Either from or to needs to be non-null");
            }
        } else if (to == null) {
            return BooleanOperation.create(Ops.GOE, mixin, from);
        } else {
            return BooleanOperation.create(Ops.BETWEEN, mixin, from, to);    
        }
        
    }

    /**
     * Get a <code>this not between from and to</code> expression
     *
     * @param from
     * @param to
     * @return
     */
    public final BooleanExpression notBetween(T from, T to) {
        return between(from, to).not();
    }

    /**
     * Get a <code>this not between from and to</code> expression
     *
     * @param from
     * @param to
     * @return
     */
    public final BooleanExpression notBetween(Expression<T> from, Expression<T> to) {
        return between(from, to).not();
    }

    /**
     * Get a <code>this &gt; right</code> expression
     *
     * @param right rhs of the comparison
     * @return
     * @see java.lang.Comparable#compareTo(Object)
     */
    public BooleanExpression gt(T right) {
        return gt(new ConstantImpl<T>(right));
    }

    /**
     * Get a <code>this &gt; right</code> expression
     *
     * @param right rhs of the comparison
     * @return
     * @see java.lang.Comparable#compareTo(Object)
     */
    public BooleanExpression gt(Expression<T> right) {
        return BooleanOperation.create(Ops.GT, mixin, right);
    }
    
    /**
     * @param right
     * @return
     */
    public BooleanExpression gtAll(CollectionExpression<?, ? super T> right) {
        return gt(ExpressionUtils.<T>all(right));
    }

    
    /**
     * @param right
     * @return
     */
    public BooleanExpression gtAny(CollectionExpression<?, ? super T> right) {
        return gt(ExpressionUtils.<T>any(right));
    }

    /**
     * Get a <code>this &gt;= right</code> expression
     *
     * @param right rhs of the comparison
     * @return
     * @see java.lang.Comparable#compareTo(Object)
     */
    public BooleanExpression goe(T right) {
        return goe(new ConstantImpl<T>(right));
    }

    /**
     * Get a <code>this &gt;= right</code> expression
     *
     * @param right rhs of the comparison
     * @return
     * @see java.lang.Comparable#compareTo(Object)
     */
    public BooleanExpression goe(Expression<T> right) {
        return BooleanOperation.create(Ops.GOE, mixin, right);
    }
    
    /**
     * @param right
     * @return
     */
    public BooleanExpression goeAll(CollectionExpression<?, ? super T> right) {
        return goe(ExpressionUtils.<T>all(right));
    }
    
    /**
     * @param right
     * @return
     */
    public BooleanExpression goeAny(CollectionExpression<?, ? super T> right) {
        return goe(ExpressionUtils.<T>any(right));
    }

    /**
     * Get a <code>this &lt; right</code> expression
     *
     * @param right rhs of the comparison
     * @return
     * @see java.lang.Comparable#compareTo(Object)
     */
    public final BooleanExpression lt(T right) {
        return lt(new ConstantImpl<T>(right));
    }

    /**
     * Get a <code>this &lt; right</code> expression
     *
     * @param right rhs of the comparison
     * @return
     * @see java.lang.Comparable#compareTo(Object)
     */
    public final BooleanExpression lt(Expression<T> right) {
        return BooleanOperation.create(Ops.LT, mixin, right);
    }
    
    /**
     * @param right
     * @return
     */
    public BooleanExpression ltAll(CollectionExpression<?, ? super T> right) {
        return lt(ExpressionUtils.<T>all(right));
    }

    
    /**
     * @param right
     * @return
     */
    public BooleanExpression ltAny(CollectionExpression<?, ? super T> right) {
        return lt(ExpressionUtils.<T>any(right));
    }

    /**
     * Get a <code>this &lt;= right</code> expression
     *
     * @param right rhs of the comparison
     * @return
     * @see java.lang.Comparable#compareTo(Object)
     */
    public final BooleanExpression loe(T right) {
        return BooleanOperation.create(Ops.LOE, mixin, new ConstantImpl<T>(right));
    }

    /**
     * Get a <code>this &lt; right</code> expression
     *
     * @param right rhs of the comparison
     * @return
     * @see java.lang.Comparable#compareTo(Object)
     */
    public final BooleanExpression loe(Expression<T> right) {
        return BooleanOperation.create(Ops.LOE, mixin, right);
    }
    
    /**
     * @param right
     * @return
     */
    public BooleanExpression loeAll(CollectionExpression<?, ? super T> right) {
        return loe(ExpressionUtils.<T>all(right));
    }
    
    /**
     * @param right
     * @return
     */
    public BooleanExpression loeAny(CollectionExpression<?, ? super T> right) {
        return loe(ExpressionUtils.<T>any(right));
    }

}
