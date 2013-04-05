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
package com.mysema.query.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.IteratorAdapter;
import com.mysema.query.JoinType;
import com.mysema.query.QueryException;
import com.mysema.query.QueryMetadata;
import com.mysema.query.SearchResults;
import com.mysema.query.Tuple;
import com.mysema.query.support.ProjectableQuery;
import com.mysema.query.types.Expression;
import com.mysema.query.types.MapExpression;
import com.mysema.query.types.OperationImpl;
import com.mysema.query.types.Ops;
import com.mysema.query.types.Path;
import com.mysema.query.types.QTuple;

/**
 * AbstractCollQuery provides a base class for Collection query implementations.
 * Extend it like this
 *
 * <pre>
 * public class MyType extends AbstractColQuery&lt;MyType&gt;{
 *   ...
 * }
 * </pre>
 *
 * @see CollQuery
 *
 * @author tiwe
 */
public abstract class AbstractCollQuery<Q extends AbstractCollQuery<Q>>  extends ProjectableQuery<Q> {

    private final Map<Expression<?>, Iterable<?>> iterables = new HashMap<Expression<?>, Iterable<?>>();

    private final QueryEngine queryEngine;

    @SuppressWarnings("unchecked")
    public AbstractCollQuery(QueryMetadata metadata, QueryEngine queryEngine) {
        super(new CollQueryMixin<Q>(metadata));
        this.queryMixin.setSelf((Q) this);
        this.queryEngine = queryEngine;
    }

    @Override
    public long count() {
        try {
            return queryEngine.count(getMetadata(), iterables);
        } catch (Exception e) {
            throw new QueryException(e.getMessage(), e);
        } finally {
            reset();
        }
    }

    @Override
    public boolean exists() {
        try {
            return queryEngine.exists(getMetadata(), iterables);
        } catch (Exception e) {
            throw new QueryException(e.getMessage(), e);
        } finally {
            reset();
        }
    }

    private <D> Expression<D> createAlias(Path<? extends Collection<D>> target, Path<D> alias) {
        return OperationImpl.create(alias.getType(), Ops.ALIAS, target, alias);
    }

    private <D> Expression<D> createAlias(MapExpression<?,D> target, Path<D> alias) {
        return OperationImpl.create(alias.getType(), Ops.ALIAS, target, alias);
    }

    /**
     * Add a query source
     *
     * @param <A>
     * @param entity Path for the source
     * @param col content of the source
     * @return
     */
    public <A> Q from(Path<A> entity, Iterable<? extends A> col) {
        iterables.put(entity, col);
        getMetadata().addJoin(JoinType.DEFAULT, entity);
        return (Q)this;
    }
    
    /**
     * Bind the given collection to an already existing query source
     *
     * @param <A>
     * @param entity Path for the source
     * @param col content of the source
     * @return
     */
    public <A> Q bind(Path<A> entity, Iterable<? extends A> col) {
        iterables.put(entity, col);
        return (Q)this;
    }

    public abstract QueryMetadata getMetadata();

    protected QueryEngine getQueryEngine() {
        return queryEngine;
    }

    /**
     * Define an inner join from the Collection typed path to the alias
     *
     * @param <P>
     * @param collectionPath
     * @param alias
     * @return
     */
    public <P> Q innerJoin(Path<? extends Collection<P>> target, Path<P> alias) {
        getMetadata().addJoin(JoinType.INNERJOIN, createAlias(target, alias));
        return (Q)this;
    }

    /**
     * Define an inner join from the Map typed path to the alias
     *
     * @param <P>
     * @param mapPath
     * @param alias
     * @return
     */
    public <P> Q innerJoin(MapExpression<?,P> target, Path<P> alias) {
        getMetadata().addJoin(JoinType.INNERJOIN, createAlias(target, alias));
        return (Q)this;
    }

    @Override
    public CloseableIterator<Tuple> iterate(Expression<?>... args) {
        return iterate(new QTuple(args));
    }

    @Override
    public <RT> CloseableIterator<RT> iterate(Expression<RT> projection) {
        try {
            projection = queryMixin.addProjection(projection);
            return new IteratorAdapter<RT>(queryEngine.list(getMetadata(), iterables, projection).iterator());
        } finally {
            reset();
        }
    }

    @Override
    public List<Tuple> list(Expression<?>... args) {
        return list(new QTuple(args));
    }

    @Override
    public <RT> List<RT> list(Expression<RT> projection) {
        try {
            projection = queryMixin.addProjection(projection);
            return queryEngine.list(getMetadata(), iterables, projection);
        } finally {
            reset();
        }
    }
    
    @Override
    public SearchResults<Tuple> listResults(Expression<?>... args) {
        return listResults(new QTuple(args));
    }

    @Override
    public <RT> SearchResults<RT> listResults(Expression<RT> projection) {
        projection = queryMixin.addProjection(projection);
        long count = queryEngine.count(getMetadata(), iterables);
        if (count > 0l) {
            List<RT> list = queryEngine.list(getMetadata(), iterables, projection);
            reset();
            return new SearchResults<RT>(list, getMetadata().getModifiers(), count);
        } else {
            reset();
            return SearchResults.<RT>emptyResults();
        }

    }
    
    @Override
    public Tuple uniqueResult(Expression<?>... args) {
        return uniqueResult(new QTuple(args));
    }

    @Override
    public <RT> RT uniqueResult(Expression<RT> expr) {
        queryMixin.setUnique(true);
        if (queryMixin.getMetadata().getModifiers().getLimit() == null) {
            limit(2l);
        }
        return uniqueResult(iterate(expr));
    }

    private void reset() {
        getMetadata().reset();
    }

}
