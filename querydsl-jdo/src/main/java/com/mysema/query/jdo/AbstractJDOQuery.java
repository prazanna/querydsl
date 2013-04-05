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
package com.mysema.query.jdo;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.IteratorAdapter;
import com.mysema.query.DefaultQueryMetadata;
import com.mysema.query.NonUniqueResultException;
import com.mysema.query.QueryException;
import com.mysema.query.QueryMetadata;
import com.mysema.query.QueryModifiers;
import com.mysema.query.SearchResults;
import com.mysema.query.Tuple;
import com.mysema.query.support.ProjectableQuery;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Expression;
import com.mysema.query.types.FactoryExpression;
import com.mysema.query.types.QTuple;

/**
 * Abstract base class for custom implementations of the JDOCommonQuery interface.
 *
 * @author tiwe
 *
 * @param <Q>
 */
public abstract class AbstractJDOQuery<Q extends AbstractJDOQuery<Q>> extends ProjectableQuery<Q> {

    private static final Logger logger = LoggerFactory.getLogger(JDOQuery.class);

    private final Closeable closeable = new Closeable() {
        @Override
        public void close() throws IOException {
            AbstractJDOQuery.this.close();
        }
    };

    private final boolean detach;

    private List<Object> orderedConstants = new ArrayList<Object>();

    @Nullable
    private final PersistenceManager persistenceManager;

    private final List<Query> queries = new ArrayList<Query>(2);

    private final JDOQLTemplates templates;

    protected final Set<String> fetchGroups = new HashSet<String>();

    @Nullable
    protected Integer maxFetchDepth;
    
    @Nullable
    private FactoryExpression<?> projection;

    public AbstractJDOQuery(@Nullable PersistenceManager persistenceManager) {
        this(persistenceManager, JDOQLTemplates.DEFAULT, new DefaultQueryMetadata(), false);
    }

    @SuppressWarnings("unchecked")
    public AbstractJDOQuery(
            @Nullable PersistenceManager persistenceManager,
            JDOQLTemplates templates,
            QueryMetadata metadata, boolean detach) {
        super(new JDOQueryMixin<Q>(metadata));
        this.queryMixin.setSelf((Q) this);
        this.templates = templates;
        this.persistenceManager = persistenceManager;
        this.detach = detach;
    }

    /**
     * Add the fetch group to the set of active fetch groups.
     * 
     * @param string
     * @return
     */
    public Q addFetchGroup(String fetchGroupName) {
        fetchGroups.add(fetchGroupName);
        return (Q)this;
    }

    /**
     * Close the query and related resources
     */
    public void close() {
        for (Query query : queries) {
            query.closeAll();
        }
    }

    @Override
    public long count() {
        Query query = createQuery(true);
        query.setUnique(true);
        reset();
        Long rv = (Long) execute(query, true);
        if (rv != null) {
            return rv.longValue();
        } else {
            throw new QueryException("Query returned null");
        }
    }

    @Override
    public boolean exists() {
        boolean rv = limit(1).uniqueResult(getSource()) != null;
        close();
        return rv;
    }

    private Expression<?> getSource() {
        return queryMixin.getMetadata().getJoins().get(0).getTarget();
    }
    
    private Query createQuery(boolean forCount) {
        Expression<?> source = getSource();

        // serialize
        JDOQLSerializer serializer = new JDOQLSerializer(getTemplates(), source);
        serializer.serialize(queryMixin.getMetadata(), forCount, false);

        logQuery(serializer.toString());

        // create Query
        Query query = persistenceManager.newQuery(serializer.toString());
        orderedConstants = serializer.getConstants();
        queries.add(query);

        if (!forCount) {
            List<? extends Expression<?>> projection = queryMixin.getMetadata().getProjection();
            if (projection.get(0) instanceof FactoryExpression) {
                this.projection = (FactoryExpression<?>)projection.get(0);
            }
            if (!fetchGroups.isEmpty()) {
                query.getFetchPlan().setGroups(fetchGroups);
            }
            if (maxFetchDepth != null) {
                query.getFetchPlan().setMaxFetchDepth(maxFetchDepth);
            }
        }

        return query;
    }

    protected void logQuery(String queryString) {
        if (logger.isDebugEnabled()) {
            logger.debug(queryString.replace('\n', ' '));
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T detach(T results) {
        if (results instanceof Collection) {
            return (T) persistenceManager.detachCopyAll(results);
        } else {
            return persistenceManager.detachCopy(results);
        }
    }

    private Object project(FactoryExpression<?> expr, Object row) {
        if (row == null) {
            return null;
        } else if (row.getClass().isArray()) {
            return expr.newInstance((Object[])row);
        } else {
            return expr.newInstance(new Object[]{row});
        }
    }

    @Nullable
    private Object execute(Query query, boolean forCount) {
        Object rv;
        if (!orderedConstants.isEmpty()) {
            rv = query.executeWithArray(orderedConstants.toArray());
        } else {
            rv = query.execute();
        }
        if (isDetach()) {
            rv = detach(rv);
        }
        if (projection != null && !forCount) {
            if (rv instanceof List) {
                List<?> original = (List<?>)rv;
                rv = Lists.newArrayList();
                for (Object o : original) {
                    ((List)rv).add(project(projection, o));
                }
            } else {
                rv = project(projection, rv);
            }
        }
        
        return rv;
    }

    public Q from(EntityPath<?>... args) {
        return queryMixin.from(args);
    }

    public QueryMetadata getMetadata() {
        return queryMixin.getMetadata();
    }

    public JDOQLTemplates getTemplates() {
        return templates;
    }

    public boolean isDetach() {
        return detach;
    }

    public CloseableIterator<Tuple> iterate(Expression<?>... args) {
        return new IteratorAdapter<Tuple>(list(args).iterator(), closeable);
    }

    public <RT> CloseableIterator<RT> iterate(Expression<RT> projection) {
        return new IteratorAdapter<RT>(list(projection).iterator(), closeable);
    }

    @Override
    public List<Tuple> list(Expression<?>... args) {
        return list(new QTuple(args));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <RT> List<RT> list(Expression<RT> expr) {
        queryMixin.addProjection(expr);
        Object rv = execute(createQuery(false), false);
        reset();
        return rv instanceof List ? (List<RT>)rv : Collections.singletonList((RT)rv);
    }

    @Override
    public SearchResults<Tuple> listResults(Expression<?>... args) {
        return listResults(new QTuple(args));
    }
    
    @SuppressWarnings("unchecked")
    public <RT> SearchResults<RT> listResults(Expression<RT> expr) {
        queryMixin.addProjection(expr);
        Query countQuery = createQuery(true);
        countQuery.setUnique(true);
        countQuery.setResult("count(this)");
        long total = (Long) execute(countQuery, true);
        if (total > 0) {
            QueryModifiers modifiers = queryMixin.getMetadata().getModifiers();
            Query query = createQuery(false);
            reset();
            return new SearchResults<RT>((List<RT>) execute(query, false), modifiers, total);
        } else {
            reset();
            return SearchResults.emptyResults();
        }
    }

    private void reset() {
        queryMixin.getMetadata().reset();
    }

    /**
     * Set the maximum fetch depth when fetching. 
     * A value of 0 has no meaning and will throw a JDOUserException.
     * A value of -1 means that no limit is placed on fetching.
     * A positive integer will result in that number of references from the
     * initial object to be fetched.
     * 
     * @param maxFetchDepth
     * @return
     */
    public Q setMaxFetchDepth(int depth) {
        maxFetchDepth = depth;
        return (Q)this;
    }

    @Override
    public String toString() {
        if (!queryMixin.getMetadata().getJoins().isEmpty()) {
            Expression<?> source = getSource();
            JDOQLSerializer serializer = new JDOQLSerializer(getTemplates(), source);
            serializer.serialize(queryMixin.getMetadata(), false, false);
            return serializer.toString().trim();
        } else {
            return super.toString();
        }
    }

    @Override
    @Nullable
    public Tuple uniqueResult(Expression<?>... args) {
        return uniqueResult(new QTuple(args));        
    }
    
    @Override
    @SuppressWarnings("unchecked")
    @Nullable
    public <RT> RT uniqueResult(Expression<RT> expr) {
        queryMixin.addProjection(expr);
        return (RT)uniqueResult();
    }

    @Nullable
    private Object uniqueResult() {
        if (getMetadata().getModifiers().getLimit() == null) {
            limit(2);
        }
        Query query = createQuery(false);
        reset();
        Object rv = execute(query, false);
        if (rv instanceof List) {
            List<?> list = (List)rv;
            if (!list.isEmpty()) {
                if (list.size() > 1) {
                    throw new NonUniqueResultException();
                }
                return list.get(0);
            } else {
                return null;
            }
        } else {
            return rv;
        }
    }

}
