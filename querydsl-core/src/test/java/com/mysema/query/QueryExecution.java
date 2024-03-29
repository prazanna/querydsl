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
package com.mysema.query;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import com.mysema.commons.lang.Pair;
import com.mysema.query.types.CollectionExpression;
import com.mysema.query.types.Expression;
import com.mysema.query.types.MapExpression;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.ArrayExpression;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.CollectionExpressionBase;
import com.mysema.query.types.expr.DateExpression;
import com.mysema.query.types.expr.DateTimeExpression;
import com.mysema.query.types.expr.ListExpression;
import com.mysema.query.types.expr.MapExpressionBase;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.expr.StringExpression;
import com.mysema.query.types.expr.TimeExpression;
import com.mysema.query.types.path.ListPath;

/**
 * The Class StandardTest.
 *
 * @author tiwe
 */
public abstract class QueryExecution {

    private final List<String> errors = new ArrayList<String>();

    private final List<String> failures = new ArrayList<String>();

    private final MatchingFiltersFactory matchers;

    private final ProjectionsFactory projections;

    private final FilterFactory filters;

    private boolean runFilters = true;

    private boolean runProjections = true;

    private boolean counts = true;

    private int total;

    public QueryExecution(Module module, Target target) {
        projections = new ProjectionsFactory(module, target);
        filters = new FilterFactory(projections, module, target);
        matchers = new MatchingFiltersFactory(module, target);
    }

    public QueryExecution(ProjectionsFactory p, FilterFactory f, MatchingFiltersFactory m) {
        projections = p;
        filters = f;
        matchers = m;
    }

    private void runProjectionQueries(Collection<? extends Expression<?>> projections) {
        if (this.runProjections) {
            for (Expression<?> pr : projections) {
                total++;
                try{
                    System.err.println(pr);
                    // projection
                    runProjection(pr);
                    System.err.println();

                    // projection distinct
                    runProjectionDistinct(pr);
                    System.err.println();

                } catch(Throwable t) {
                    t.printStackTrace();
                    t = addError(pr, t);
                }
            }
        }
    }

    private Throwable addError(Expression<?> expr, Throwable throwable) {
        StringBuilder error = new StringBuilder();
        error.append(expr + " failed : \n");
        error.append(" " + throwable.getClass().getName() + " : " + throwable.getMessage() + "\n");
        if (throwable.getCause() != null) {
            throwable = throwable.getCause();
            error.append(" " + throwable.getClass().getName() + " : " + throwable.getMessage() + "\n");
        }
        errors.add(error.toString());
        return throwable;
    }

    private void runFilterQueries(Collection<Predicate> filters, boolean matching) {
        if (this.runFilters) {
            for (Predicate f : filters) {
                total++;
                try{
                    System.err.println(f);
                    // filter
                    int results = runFilter(f);
                    System.err.println();

                    // filter distinct
                    runFilterDistinct(f);
                    System.err.println();

                    if (counts) {
                        // count
                        runCount(f);
                        System.err.println();

                        // count distinct
                        runCountDistinct(f);
                        System.err.println();
                    }

                    if (matching && results == 0) {
                        failures.add(f + " failed");
                    }

                }catch(Throwable t) {
                    t.printStackTrace();
                    t = addError(f, t);
                }
            }
        }
    }

    protected abstract Pair<Projectable, Expression<?>[]> createQuery();

    protected abstract Pair<Projectable, Expression<?>[]> createQuery(Predicate filter);

    private long runCount(Predicate f) {
        Pair<Projectable, Expression<?>[]> p = createQuery(f);
        try{
            return p.getFirst().count();
        }finally{
            close(p.getFirst());
        }
    }

    private long runCountDistinct(Predicate f) {
        Pair<Projectable, Expression<?>[]> p = createQuery(f);
        try{
            return p.getFirst().countDistinct();
        }finally{
            close(p.getFirst());
        }
    }

    private int runFilter(Predicate f) { 
        Pair<Projectable, Expression<?>[]> p = createQuery(f);
        try{
            return p.getFirst().list(p.getSecond()).size();
        }finally{
            close(p.getFirst());
        }
    }

    private int runFilterDistinct(Predicate f) {
        Pair<Projectable, Expression<?>[]> p = createQuery(f);
        try{
            return p.getFirst().listDistinct(p.getSecond()).size();
        }finally{
            close(p.getFirst());
        }
    }

    private int runProjection(Expression<?> pr) {
        Pair<Projectable, Expression<?>[]> p = createQuery();
        try{
            if (p.getSecond().length == 0) {
                return p.getFirst().list(pr).size();
            } else {
                Expression<?>[] projection = new Expression[p.getSecond().length + 1];
                projection[0] = pr;
                System.arraycopy(p.getSecond(), 0, projection, 1, p.getSecond().length);
                return p.getFirst().list(projection).size();
            }

        }finally{
            close(p.getFirst());
        }
    }

    private int runProjectionDistinct(Expression<?> pr) {
        Pair<Projectable, Expression<?>[]> p = createQuery();
        try{
            if (p.getSecond().length == 0) {
                return p.getFirst().listDistinct(pr).size();
            }else{
                Expression<?>[] projection = new Expression[p.getSecond().length + 1];
                projection[0] = pr;
                System.arraycopy(p.getSecond(), 0, projection, 1, p.getSecond().length);
                return p.getFirst().listDistinct(projection).size();
            }

        }finally{
            close(p.getFirst());
        }
    }

    private void close(Projectable p) {
        if (p instanceof Closeable) {
            try {
                ((Closeable)p).close();
            } catch (IOException e) {
                throw new QueryException(e);
            }
        }
    }

    public final void report() {
        if (!failures.isEmpty() || !errors.isEmpty()) {
            // System.err logging
            System.err.println(failures.size() + " failures");
            for (String f : failures) {
                System.err.println(f);
            }
            System.err.println();
            System.err.println(errors.size() + " errors");
            for (String e : errors) {
                System.err.println(e);
            }

            // construct String for Assert.fail()
            StringBuffer buffer = new StringBuffer("Failed with ");
            if (!failures.isEmpty()) {
                buffer.append(failures.size()).append(" failure(s) ");
                if (!errors.isEmpty()) {
                    buffer.append("and ");
                }
            }
            if (!errors.isEmpty()) {
                buffer.append(errors.size()).append(" error(s) ");
            }
            buffer.append("of ").append(total).append(" tests\n");
            for (String f : failures) {
                buffer.append(f + "\n");
            }
            for (String e : errors) {
                buffer.append(e + "\n");
            }
            Assert.fail(buffer.toString());
        }else{
            System.out.println("Success with " + total + " tests");
        }
    }

    public final QueryExecution noFilters() {
        runFilters = false;
        return this;
    }

    public final QueryExecution noProjections() {
        runProjections = false;
        return this;
    }

    public final QueryExecution noCounts() {
        counts = false;
        return this;
    }

    public final <A> void runArrayTests(ArrayExpression<A> expr, ArrayExpression<A> other, A knownElement, A missingElement) {
        runFilterQueries(matchers.array(expr, other, knownElement, missingElement), true);
        runFilterQueries(filters.array(expr, other, knownElement), false);
        runProjectionQueries(projections.array(expr, other, knownElement));
    }

    public final void runBooleanTests(BooleanExpression expr, BooleanExpression other) {
        runFilterQueries(filters.booleanFilters(expr, other), false);
    }

    public final <A> void runCollectionTests(CollectionExpressionBase<?,A> expr, CollectionExpression<?,A> other, A knownElement, A missingElement) {
        runFilterQueries(matchers.collection(expr, other, knownElement, missingElement), true);
        runFilterQueries(filters.collection(expr, other, knownElement), false);
        runProjectionQueries(projections.collection(expr, other, knownElement));
    }

    public final void runDateTests(DateExpression<java.sql.Date> expr, DateExpression<java.sql.Date> other, java.sql.Date knownValue) {
        runFilterQueries(matchers.date(expr, other, knownValue), true);
        runFilterQueries(filters.date(expr, other, knownValue), false);
        runProjectionQueries(projections.date(expr, other, knownValue));
    }

    public final void runDateTimeTests(DateTimeExpression<java.util.Date> expr, DateTimeExpression<java.util.Date> other, java.util.Date knownValue) {
        runFilterQueries(matchers.dateTime(expr, other, knownValue), true);
        runFilterQueries(filters.dateTime(expr, other, knownValue), false);
        runProjectionQueries(projections.dateTime(expr, other, knownValue));
    }

    public final <A, Q extends SimpleExpression<A>> void runListTests(ListPath<A,Q> expr, ListExpression<A,Q> other, A knownElement, A missingElement) {
        runFilterQueries(matchers.list(expr, other, knownElement, missingElement), true);
        runFilterQueries(filters.list(expr, other, knownElement), false);
        runProjectionQueries(projections.list(expr, other, knownElement));
    }

    public final <K,V> void runMapTests(MapExpressionBase<K,V,?> expr, MapExpression<K,V> other, K knownKey, V knownValue, K missingKey, V missingValue) {
        runFilterQueries(matchers.map(expr, other, knownKey, knownValue, missingKey, missingValue), true);
        runFilterQueries(filters.map(expr, other, knownKey, knownValue), false);
        runProjectionQueries(projections.map(expr, other, knownKey, knownValue));
    }

    public final <A extends Number & Comparable<A>> void runNumericCasts(NumberExpression<A> expr, NumberExpression<A> other, A knownValue) {
        runProjectionQueries(projections.numericCasts(expr, other, knownValue));
    }

    public final <A extends Number & Comparable<A>> void runNumericTests(NumberExpression<A> expr, NumberExpression<A> other, A knownValue) {
        runFilterQueries(matchers.numeric(expr, other, knownValue), true);
        runFilterQueries(filters.numeric(expr, other, knownValue), false);
        runProjectionQueries(projections.numeric(expr, other, knownValue, false));
    }

    public final void runStringTests(StringExpression expr, StringExpression other, String knownValue) {
        runFilterQueries(matchers.string(expr, other, knownValue), true);
        runFilterQueries(filters.string(expr, other, knownValue), false);
        runProjectionQueries(projections.string(expr, other, knownValue));
    }

    public final void runTimeTests(TimeExpression<java.sql.Time> expr, TimeExpression<java.sql.Time> other, java.sql.Time knownValue) {
        runFilterQueries(matchers.time(expr, other, knownValue), true);
        runFilterQueries(filters.time(expr, other, knownValue), false);
        runProjectionQueries(projections.time(expr, other, knownValue));
    }
}
