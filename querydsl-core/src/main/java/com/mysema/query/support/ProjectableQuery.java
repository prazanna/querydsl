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
package com.mysema.query.support;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.IteratorAdapter;
import com.mysema.query.NonUniqueResultException;
import com.mysema.query.Projectable;
import com.mysema.query.ResultTransformer;
import com.mysema.query.SearchResults;
import com.mysema.query.Tuple;
import com.mysema.query.types.Expression;

/**
 * ProjectableQuery extends the {@link QueryBase} class to provide default
 * implementations of the methods of the {@link Projectable} interface
 *
 * @author tiwe
 */
public abstract class ProjectableQuery<Q extends ProjectableQuery<Q>>
        extends QueryBase<Q> implements Projectable {

    public ProjectableQuery(QueryMixin<Q> queryMixin) {
        super(queryMixin);
    }

    @Override
    public final long countDistinct() {
        queryMixin.setDistinct(true);
        return count();
    }

    @Override
    public final CloseableIterator<Tuple> iterateDistinct(Expression<?>... args) {
        queryMixin.setDistinct(true);
        return iterate(args);
    }

    @Override
    public final <RT> CloseableIterator<RT> iterateDistinct(Expression<RT> projection) {
        queryMixin.setDistinct(true);
        return iterate(projection);
    }

    @Override
    public List<Tuple> list(Expression<?>... args) {
        return IteratorAdapter.asList(iterate(args));
    }

    @Override
    public <RT> List<RT> list(Expression<RT> projection) {
        return IteratorAdapter.asList(iterate(projection));
    }

    public final List<Tuple> listDistinct(Expression<?>... args) {
        queryMixin.setDistinct(true);
        return list(args);
    }

    @Override
    public final <RT> List<RT> listDistinct(Expression<RT> projection) {
        queryMixin.setDistinct(true);
        return list(projection);
    }

    @Override
    public final <RT> SearchResults<RT> listDistinctResults(Expression<RT> projection) {
        queryMixin.setDistinct(true);
        return listResults(projection);
    }

    @Override
    public final <K, V> Map<K, V> map(Expression<K> key, Expression<V> value) {
        List<Tuple> list = list(key, value);
        Map<K, V> results = new LinkedHashMap<K, V>(list.size());
        for (Tuple row : list) {
            results.put(row.get(key), row.get(value));
        }
        return results;
    }

    @Override
    public final boolean notExists() {
        return !exists();
    }

    @Override
    public final Tuple singleResult(Expression<?>... args) {
        return limit(1).uniqueResult(args);
    }

    @Override
    public final <RT> RT singleResult(Expression<RT> expr) {
        return limit(1).uniqueResult(expr);
    }

    @Override
    public <T> T transform(ResultTransformer<T> transformer) {
        return transformer.transform(this);
    }
    
    @Nullable
    protected <T> T uniqueResult(CloseableIterator<T> it) {
        try{
            if (it.hasNext()) {
                T rv = it.next();
                if (it.hasNext()) {
                    throw new NonUniqueResultException();
                }
                return rv;
            }else{
                return null;
            }
        }finally{
            it.close();
        }
    }


}
