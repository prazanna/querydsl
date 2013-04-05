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

import com.mysema.query.types.Expression;
import com.mysema.query.types.Predicate;

/**
 * Query defines the main query interface of the fluent query language.
 *
 * <p>Note that the from method has been left out, since there are implementation
 * specific variants of it.</p>
 *
 * @author tiwe
 * @see SimpleQuery
 */
public interface Query<Q extends Query<Q>> extends SimpleQuery<Q> {

    /**
     * Defines the grouping/aggregation expressions
     *
     * @param o
     * @return
     */
    Q groupBy(Expression<?>... o);

    /**
     * Defines the filters for aggregation
     *
     * @param o
     * @return
     */
    Q having(Predicate... o);
   
}
