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
package com.mysema.query.sql.oracle;

import java.sql.Connection;

import com.mysema.query.DefaultQueryMetadata;
import com.mysema.query.QueryFlag.Position;
import com.mysema.query.QueryMetadata;
import com.mysema.query.sql.AbstractSQLQuery;
import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.OracleTemplates;
import com.mysema.query.sql.SQLCommonQuery;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.types.Expression;
import com.mysema.query.types.Predicate;

/**
 * OracleQuery provides Oracle specific extensions to the base SQL query type
 *
 * @author tiwe
 */
public class OracleQuery extends AbstractSQLQuery<OracleQuery> implements SQLCommonQuery<OracleQuery> {    

    private static final String CONNECT_BY = "\nconnect by ";

    private static final String CONNECT_BY_NOCYCLE_PRIOR = "\nconnect by nocycle prior ";

    private static final String CONNECT_BY_PRIOR = "\nconnect by prior ";
    
    private static final String ORDER_SIBLINGS_BY = "\norder siblings by ";

    private static final String START_WITH = "\nstart with ";

    public OracleQuery(Connection conn) {
        this(conn, new OracleTemplates(), new DefaultQueryMetadata());
    }
    
    public OracleQuery(Connection conn, SQLTemplates templates) {
        this(conn, templates, new DefaultQueryMetadata());
    }
    
    public OracleQuery(Connection conn, Configuration configuration) {
        super(conn, configuration, new DefaultQueryMetadata());
    }
    
    public OracleQuery(Connection conn, Configuration configuration, QueryMetadata metadata) {
        super(conn, configuration, metadata);
    }

    protected OracleQuery(Connection conn, SQLTemplates templates, QueryMetadata metadata) {
        super(conn, new Configuration(templates), metadata);
    }
    
    /**
     * @param cond
     * @return
     */
    public OracleQuery connectByPrior(Predicate cond) {
        return addFlag(Position.BEFORE_ORDER, CONNECT_BY_PRIOR, cond);
    }

    /**
     * @param cond
     * @return
     */
    public OracleQuery connectBy(Predicate cond) {
        return addFlag(Position.BEFORE_ORDER, CONNECT_BY, cond);
    }

    /**
     * @param cond
     * @return
     */
    public OracleQuery connectByNocyclePrior(Predicate cond) {
        return addFlag(Position.BEFORE_ORDER, CONNECT_BY_NOCYCLE_PRIOR, cond);
    }
           
    /**
     * @param cond
     * @return
     */
    public <A> OracleQuery startWith(Predicate cond) {
        return addFlag(Position.BEFORE_ORDER, START_WITH, cond);
    }

    /**
     * @param path
     * @return
     */
    public OracleQuery orderSiblingsBy(Expression<?> path) {
        return addFlag(Position.BEFORE_ORDER, ORDER_SIBLINGS_BY, path);
    }
    
    // TODO : connect by root

    // TODO : connect by iscycle

    // TODO : connect by isleaf (pseudocolumn)

    // TODO : sys connect path
}
