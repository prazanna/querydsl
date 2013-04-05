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
package com.mysema.query.jpa.hibernate.sql;

import org.hibernate.Session;
import org.hibernate.StatelessSession;

import com.mysema.query.QueryMetadata;
import com.mysema.query.jpa.hibernate.DefaultSessionHolder;
import com.mysema.query.jpa.hibernate.SessionHolder;
import com.mysema.query.sql.SQLCommonQuery;
import com.mysema.query.sql.SQLTemplates;

/**
 * HibernateSQLQuery is an SQLQuery implementation that uses Hibernate's Native SQL functionality
 * to execute queries
 *
 * @author tiwe
 *
 */
public final class HibernateSQLQuery extends AbstractHibernateSQLQuery<HibernateSQLQuery> implements SQLCommonQuery<HibernateSQLQuery> {

    public HibernateSQLQuery(Session session, SQLTemplates sqlTemplates) {
        super(session, sqlTemplates);
    }

    public HibernateSQLQuery(StatelessSession session, SQLTemplates sqlTemplates) {
        super(session, sqlTemplates);
    }
    
    public HibernateSQLQuery(SessionHolder session, SQLTemplates sqlTemplates, QueryMetadata metadata) {
        super(session, sqlTemplates, metadata);
    }
    
    public HibernateSQLQuery clone(Session session) {
        HibernateSQLQuery q = new HibernateSQLQuery(new DefaultSessionHolder(session), templates, getMetadata().clone());
        q.cacheable = cacheable;
        q.cacheRegion = cacheRegion;
        q.fetchSize = fetchSize;
        q.readOnly = readOnly;
        q.timeout = timeout;
        return q;
    }

}
