/*
 * Copyright 2013, Mysema Ltd
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
package com.mysema.query.jpa;

import java.util.Iterator;

import javax.annotation.Nullable;
import javax.persistence.Query;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.IteratorAdapter;
import com.mysema.query.types.FactoryExpression;

/**
 * @author tiwe
 *
 */
public final class DefaultQueryHandler implements QueryHandler {

    public static final QueryHandler DEFAULT = new DefaultQueryHandler();

    @Override
    public <T> CloseableIterator<T> iterate(Query query, @Nullable final FactoryExpression<?> projection) {
        Iterator<T> iterator = query.getResultList().iterator();
        if (projection != null) {
            return new TransformingIterator<T>(iterator, projection);                
        } else {
            return new IteratorAdapter<T>(iterator);
        }        
    }
    
    @Override
    public boolean transform(Query query, FactoryExpression<?> projection) {
        return false;
    }
    
    private DefaultQueryHandler() {}


}
