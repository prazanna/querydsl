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

import java.sql.Connection;
import java.util.List;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.query.sql.AbstractSQLQuery;
import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.SQLCommonQuery;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.types.Expression;
import com.mysema.query.types.FactoryExpression;
import com.mysema.query.types.QBean;

/**
 * @author tiwe
 *
 */
public class ExtendedSQLQuery extends AbstractSQLQuery<ExtendedSQLQuery> implements SQLCommonQuery<ExtendedSQLQuery> {

    public ExtendedSQLQuery(SQLTemplates templates) {
        super(null, new Configuration(templates), new DefaultQueryMetadata());
    }

    public ExtendedSQLQuery(Connection conn, SQLTemplates templates) {
        super(conn, new Configuration(templates), new DefaultQueryMetadata());
    }
    
    public ExtendedSQLQuery(Connection conn, Configuration configuration) {
        super(conn, configuration, new DefaultQueryMetadata());
    }

    public ExtendedSQLQuery(Connection conn, Configuration configuration, QueryMetadata metadata) {
        super(conn, configuration, metadata);
    }
    
    public <T> CloseableIterator<T> iterate(Class<T> type, Expression<?>... exprs) {
        return iterate(createProjection(type, exprs));
    }
    
    public <T> T uniqueResult(Class<T> type, Expression<?>... exprs) {
        return uniqueResult(createProjection(type, exprs));
    }
    
    public <T> List<T> list(Class<T> type, Expression<?>... exprs) {
        return list(createProjection(type, exprs));
    }
    
    public <T> SearchResults<T> listResults(Class<T> type, Expression<?>... exprs) {
        return listResults(createProjection(type, exprs));
    }
    
    private <T> FactoryExpression<T> createProjection(Class<T> type, Expression<?>... exprs) {
        return new QBean<T>(type, exprs);
    }
    
}
