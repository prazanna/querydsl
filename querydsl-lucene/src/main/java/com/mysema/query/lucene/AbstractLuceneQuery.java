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
package com.mysema.query.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.MapFieldSelector;
import org.apache.lucene.search.DuplicateFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Sort;

import com.google.common.base.Function;
import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.EmptyCloseableIterator;
import com.mysema.commons.lang.IteratorAdapter;
import com.mysema.query.DefaultQueryMetadata;
import com.mysema.query.NonUniqueResultException;
import com.mysema.query.QueryException;
import com.mysema.query.QueryMetadata;
import com.mysema.query.QueryModifiers;
import com.mysema.query.SearchResults;
import com.mysema.query.SimpleProjectable;
import com.mysema.query.SimpleQuery;
import com.mysema.query.support.QueryMixin;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.ParamExpression;
import com.mysema.query.types.Path;
import com.mysema.query.types.Predicate;

/**
 * AbstractLuceneQuery is an abstract super class for Lucene query implementations
 *
 * @author tiwe
 *
 * @param <T> projection type
 * @param <Q> concrete subtype of query
 */
public abstract class AbstractLuceneQuery<T,Q extends AbstractLuceneQuery<T,Q>> implements SimpleQuery<Q>,
SimpleProjectable<T> {

    private final QueryMixin<Q> queryMixin;

    private final IndexSearcher searcher;

    private final LuceneSerializer serializer;

    private final Function<Document, T> transformer;

    @Nullable
    private FieldSelector fieldSelector;

    @Nullable
    private Filter filter;
    
    @Nullable
    private Sort querySort;

    @SuppressWarnings("unchecked")
    public AbstractLuceneQuery(LuceneSerializer serializer, IndexSearcher searcher,
            Function<Document, T> transformer) {
        queryMixin = new QueryMixin<Q>((Q) this, new DefaultQueryMetadata().noValidate());
        this.serializer = serializer;
        this.searcher = searcher;
        this.transformer = transformer;
    }

    public AbstractLuceneQuery(IndexSearcher searcher, Function<Document, T> transformer) {
        this(LuceneSerializer.DEFAULT, searcher, transformer);
    }

    @Override
    public boolean exists() {
        return innerCount() > 0;
    }

    @Override
    public boolean notExists() {
        return innerCount() == 0;
    }

    private long innerCount() {
        try {
            final int maxDoc = searcher.maxDoc();
            if (maxDoc == 0) {
                return 0;
            }
            return searcher.search(createQuery(), filter, maxDoc).totalHits;
        } catch (IOException e) {
            throw new QueryException(e);
        } catch (IllegalArgumentException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public long count() {
        return innerCount();
    }

    @Override
    public long countDistinct() {
        throw new UnsupportedOperationException("use distinct(path) instead");
    }

    private Query createQuery() {
        if (queryMixin.getMetadata().getWhere() == null) {
            return new MatchAllDocsQuery();
        }
        return serializer.toQuery(queryMixin.getMetadata().getWhere(), queryMixin.getMetadata());
    }

    @Override
    public Q distinct() {
        throw new UnsupportedOperationException("use distinct(path) instead");
    }

    /**
     * Add a DuplicateFilter for the field of the given property path
     *
     * @param property
     * @return
     */
    public Q distinct(Path<?> property) {
        return filter(new DuplicateFilter(serializer.toField(property)));
    }

    /**
     * Apply the given Lucene filter to the search results
     *
     * @param filter
     * @return
     */
    @SuppressWarnings("unchecked")
    public Q filter(Filter filter) {
        this.filter = filter;
        return (Q)this;
    }

    @Override
    public Q limit(long limit) {
        return queryMixin.limit(limit);
    }

    @Override
    public CloseableIterator<T> iterate() {
        final QueryMetadata metadata = queryMixin.getMetadata();
        final List<OrderSpecifier<?>> orderBys = metadata.getOrderBy();
        final Long queryLimit = metadata.getModifiers().getLimit();
        final Long queryOffset = metadata.getModifiers().getOffset();
        Sort sort = querySort;
        int limit;
        final int offset = queryOffset != null ? queryOffset.intValue() : 0;
        try {
            limit = maxDoc();
            if (limit == 0) {
                return new EmptyCloseableIterator<T>();
            }
        } catch (IOException e) {
            throw new QueryException(e);
        } catch (IllegalArgumentException e) {
            throw new QueryException(e);
        }
        if (queryLimit != null && queryLimit.intValue() < limit) {
            limit = queryLimit.intValue();
        }
        if (sort == null && !orderBys.isEmpty()) {
            sort = serializer.toSort(orderBys);
        }

        try {
            ScoreDoc[] scoreDocs;
            int sumOfLimitAndOffset = limit + offset;
            if (sumOfLimitAndOffset < 1) {
                throw new QueryException("The given limit (" + limit + ") and offset (" + offset + ") cause an integer overflow.");
            }
            if (sort != null) {
                scoreDocs = searcher.search(createQuery(), filter, sumOfLimitAndOffset, sort).scoreDocs;
            } else {
                scoreDocs = searcher.search(createQuery(), filter, sumOfLimitAndOffset).scoreDocs;
            }
            if (offset < scoreDocs.length) {
                return new ResultIterator<T>(scoreDocs, offset, searcher, fieldSelector, transformer);
            }
            return new EmptyCloseableIterator<T>();
        } catch (final IOException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public CloseableIterator<T> iterateDistinct() {
        throw new UnsupportedOperationException("use distinct(path) instead");
    }

    private List<T> innerList() {
        return new IteratorAdapter<T>(iterate()).asList();
    }

    @Override
    public List<T> list() {
        return innerList();
    }

    /**
     * Set the given FieldSelector to the query
     *
     * @param fieldSelector
     * @return
     */
    @SuppressWarnings("unchecked")
    public Q load(FieldSelector fieldSelector) {
        this.fieldSelector = fieldSelector;
        return (Q)this;
    }

    /**
     * Load only the fields of the given paths
     *
     * @param paths
     * @return
     */
    @SuppressWarnings("unchecked")
    public Q load(Path<?>... paths) {
        List<String> fields = new ArrayList<String>(paths.length);
        for (Path<?> path : paths) {
            fields.add(serializer.toField(path));
        }
        this.fieldSelector = new MapFieldSelector(fields);
        return (Q)this;
    }

    @Override
    public List<T> listDistinct() {
        throw new UnsupportedOperationException("use distinct(path) instead");
    }

    @Override
    public SearchResults<T> listDistinctResults() {
        throw new UnsupportedOperationException("use distinct(path) instead");
    }

    @Override
    public SearchResults<T> listResults() {
        List<T> documents = innerList();
        /*
         * TODO Get rid of count(). It could be implemented by iterating the
         * list results in list* from n to m.
         */
        return new SearchResults<T>(documents, queryMixin.getMetadata().getModifiers(), innerCount());
    }

    @Override
    public Q offset(long offset) {
        return queryMixin.offset(offset);
    }

    public Q orderBy(OrderSpecifier<?> o) {
        return queryMixin.orderBy(o);
    }
    
    @Override
    public Q orderBy(OrderSpecifier<?>... o) {
        return queryMixin.orderBy(o);
    }

    @Override
    public Q restrict(QueryModifiers modifiers) {
        return queryMixin.restrict(modifiers);
    }

    @Override
    public <P> Q set(ParamExpression<P> param, P value) {
        return queryMixin.set(param, value);
    }
    
    @SuppressWarnings("unchecked")
    public Q sort(Sort sort) {
        this.querySort = sort;
        return (Q)this;
    }

    @Nullable
    private T oneResult(boolean unique) {
        try {
            int maxDoc = maxDoc();
            if (maxDoc == 0) {
                return null;
            }
            final ScoreDoc[] scoreDocs = searcher.search(createQuery(), filter, maxDoc).scoreDocs;
            int index = 0;
            QueryModifiers modifiers = queryMixin.getMetadata().getModifiers();
            Long offset = modifiers.getOffset();
            if (offset != null) {
                index = offset.intValue();
            }
            Long limit = modifiers.getLimit();
            if (unique && (limit == null ? scoreDocs.length - index > 1 :
                                           limit > 1 && scoreDocs.length > 1)) {
                throw new NonUniqueResultException("Unique result requested, but " + scoreDocs.length + " found.");
            } else if (scoreDocs.length > index) {
                Document document;
                if (fieldSelector != null) {
                    document = searcher.doc(scoreDocs[index].doc, fieldSelector);
                } else {
                    document = searcher.doc(scoreDocs[index].doc);
                }
                return transformer.apply(document);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new QueryException(e);
        }  catch (IllegalArgumentException e) {
            throw new QueryException(e);
        }
    }

    @Override
    public T singleResult() {
        return oneResult(false);
    }

    @Override
    public T uniqueResult() {
        return oneResult(true);
    }
    
    public Q where(Predicate e) {
        return queryMixin.where(e);
    }

    @Override
    public Q where(Predicate... e) {
        return queryMixin.where(e);
    }

    @Override
    public String toString() {
        return createQuery().toString();
    }

    private int maxDoc() throws IOException {
        return searcher.maxDoc();
    }
}
