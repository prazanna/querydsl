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

import com.mysema.query.DefaultQueryMetadata;
import com.mysema.query.QueryMetadata;
import com.mysema.query.SimpleQuery;

/**
 * CollQuery is the default implementation of the {@link SimpleQuery} interface for collections
 *
 * @author tiwe
 *
 */
public final class CollQuery extends AbstractCollQuery<CollQuery> implements SimpleQuery<CollQuery>, Cloneable {

    /**
     * Create a new CollQuery instance
     */
    public CollQuery() {
        super(new DefaultQueryMetadata(), DefaultQueryEngine.getDefault());
    }

    /**
     * Creates a new CollQuery instance
     * 
     * @param templates
     */
    public CollQuery(CollQueryTemplates templates) {
        this(new DefaultQueryEngine(new DefaultEvaluatorFactory(templates)));
    }
    
    /**
     * Create a new CollQuery instance
     *
     * @param evaluatorFactory
     */
    public CollQuery(QueryEngine queryEngine) {
        super(new DefaultQueryMetadata(), queryEngine);
    }
    

    /**
     * Create a new CollQuery instance
     *
     * @param metadata
     * @param evaluatorFactory
     */
    public CollQuery(QueryMetadata metadata) {
        super(metadata, DefaultQueryEngine.getDefault());
    }

    /**
     * Create a new CollQuery instance
     *
     * @param metadata
     * @param evaluatorFactory
     */
    public CollQuery(QueryMetadata metadata, QueryEngine queryEngine) {
        super(metadata, queryEngine);
    }

    /**
     * Clone the state of this query to a new CollQuery instance
     */
    @Override
    public CollQuery clone() {
        return new CollQuery(queryMixin.getMetadata().clone(), getQueryEngine());
    }

    /**
     * @return
     */
    @Override
    public QueryMetadata getMetadata() {
        return queryMixin.getMetadata();
    }

}
