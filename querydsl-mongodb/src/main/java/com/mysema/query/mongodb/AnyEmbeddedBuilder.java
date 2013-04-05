/*
 * Copyright 2012, Mysema Ltd
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
package com.mysema.query.mongodb;

import java.util.Collection;

import com.mysema.query.support.QueryMixin;
import com.mysema.query.types.ExpressionUtils;
import com.mysema.query.types.Path;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.PredicateOperation;

/**
 * AnyEmbeddedBuilder is a builder for constraints on embedded objects
 * 
 * @author tiwe
 *
 * @param <K>
 * @param <T>
 */
public class AnyEmbeddedBuilder<K> {

    private final QueryMixin<MongodbQuery<K>> queryMixin;
    
    private final Path<? extends Collection<?>> collection;
    
    public AnyEmbeddedBuilder(QueryMixin<MongodbQuery<K>> queryMixin,
            Path<? extends Collection<?>> collection) {
        this.queryMixin = queryMixin;
        this.collection = collection;        
    }

    public MongodbQuery<K> on(Predicate... conditions) {
        return queryMixin.where(PredicateOperation.create(
                MongodbExpressions.ELEM_MATCH, collection, ExpressionUtils.allOf(conditions)));
    }
    
}
