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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mysema.query.types.ConstantImpl;
import com.mysema.query.types.Expression;
import com.mysema.query.types.Operator;
import com.mysema.query.types.Ops;

/**
 * CaseBuilder enables the construction of typesafe case-when-then-else
 * constructs :
 * e.g.
 *
 * <pre>
 * Expression&lt;String&gt; cases = new CaseBuilder()
 *     .when(c.annualSpending.gt(10000)).then("Premier")
 *     .when(c.annualSpending.gt(5000)).then("Gold")
 *     .when(c.annualSpending.gt(2000)).then("Silver")
 *     .otherwise("Bronze");
 * </pre>
 *
 * @author tiwe
 *
 */
public final class CaseBuilder {

    private static class CaseElement<A> {

        @Nullable
        private final BooleanExpression condition;

        private final Expression<A> target;

        public CaseElement(@Nullable BooleanExpression condition, Expression<A> target) {
            this.condition = condition;
            this.target = target;
        }

        public BooleanExpression getCondition() {
            return condition;
        }

        public Expression<A> getTarget() {
            return target;
        }

    }

    /**
     * Cascading typesafe Case builder
     *
     * @author tiwe
     *
     * @param <A>
     */
    public abstract static class Cases<A, Q extends Expression<A>> {

        private final List<CaseElement<A>> cases = new ArrayList<CaseElement<A>>();

        private final Class<A> type;

        public Cases(Class<A> type) {
            this.type = type;
        }

        Cases<A,Q> addCase(BooleanExpression condition, Expression<A> expr) {
            cases.add(0, new CaseElement<A>(condition, expr));
            return this;
        }

        protected abstract Q createResult(Class<A> type, Expression<A> last);

        public Q otherwise(A constant) {
            return otherwise(new ConstantImpl<A>(constant));
        }

        public Q otherwise(Expression<A> expr) {
            cases.add(0, new CaseElement<A>(null, expr));
            Expression<A> last = null;
            for (CaseElement<A> element : cases) {
                if (last == null) {
                    last = SimpleOperation.create(type, Ops.CASE_ELSE,
                            element.getTarget());
                } else {
                    last = SimpleOperation.create(type, Ops.CASE_WHEN,
                            element.getCondition(),
                            element.getTarget(),
                            last);
                }
            }
            return createResult(type, last);
        }

        public CaseWhen<A,Q> when(BooleanExpression b) {
            return new CaseWhen<A,Q>(this, b);
        }

    }

    /**
     * Intermediate When state
     *
     * @author tiwe
     *
     * @param <A>
     */
    public static class CaseWhen<A,Q extends Expression<A>> {

        private final BooleanExpression b;

        private final Cases<A,Q> cases;

        public CaseWhen(Cases<A,Q> cases, BooleanExpression b) {
            this.cases = cases;
            this.b = b;
        }

        public Cases<A,Q> then(A constant) {
            return then(new ConstantImpl<A>(constant));
        }

        public Cases<A,Q> then(Expression<A> expr) {
            return cases.addCase(b, expr);
        }
    }

    /**
     * Initial state of Case construction
     *
     * @author tiwe
     *
     */
    public static class Initial {

        private final BooleanExpression when;

        public Initial(BooleanExpression b) {
            this.when = b;
        }

        public <A> Cases<A,Expression<A>> then(A constant) {
            return then(new ConstantImpl<A>(constant));
        }
        
        public Cases<Boolean,BooleanExpression> then(BooleanExpression expr) {
            return thenBoolean(expr);
        }

        private Cases<Boolean, BooleanExpression> thenBoolean(Expression<Boolean> expr) {
            return new Cases<Boolean,BooleanExpression>(Boolean.class) {
                @SuppressWarnings("unchecked")
                @Override
                protected BooleanExpression createResult(Class<Boolean> type, Expression<Boolean> last) {
                    return BooleanOperation.create((Operator)Ops.CASE, last);
                }

            }.addCase(when, expr);
        }

        public Cases<String,StringExpression> then(StringExpression expr) {
            return thenString(expr);
        }
        
        private Cases<String,StringExpression> thenString(Expression<String> expr) {
            return new Cases<String,StringExpression>(String.class) {
                @SuppressWarnings("unchecked")
                @Override
                protected StringExpression createResult(Class<String> type, Expression<String> last) {
                    return StringOperation.create((Operator)Ops.CASE, last);
                }

            }.addCase(when, expr);
        }

        @SuppressWarnings("unchecked")
        public <A> Cases<A, Expression<A>> then(Expression<A> expr) {
            return new Cases<A,Expression<A>>((Class)expr.getType()) {
                @Override
                protected Expression<A> createResult(Class<A> type, Expression<A> last) {
                    return SimpleOperation.create(type, Ops.CASE, last);
                }

            }.addCase(when, expr);
        }
        
        public <A extends Number & Comparable<?>> Cases<A, NumberExpression<A>> then(NumberExpression<A> expr) {
            return thenNumber(expr);
        }
        
        @SuppressWarnings("unchecked")
        private <A extends Number & Comparable<?>> Cases<A, NumberExpression<A>> thenNumber(Expression<A> expr) {
            return new Cases<A, NumberExpression<A>>((Class)expr.getType()) {
                @Override
                protected NumberExpression<A> createResult(Class<A> type, Expression<A> last) {
                    return NumberOperation.create(type, (Operator)Ops.CASE, last);
                }

            }.addCase(when, expr);
        }

        public Cases<String, StringExpression> then(String str) {
            return thenString(ConstantImpl.create(str));
        }

        public <A extends Number & Comparable<?>> Cases<A, NumberExpression<A>> then(A num) {
            return thenNumber(new ConstantImpl<A>(num));
        }
        
        public Cases<Boolean, BooleanExpression> then(boolean b) {
            return thenBoolean(ConstantImpl.create(b));
        }
    }

    public Initial when(BooleanExpression b) {
        return new Initial(b);
    }

}
