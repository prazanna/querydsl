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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.hibernate.Session;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mysema.query.jpa.domain.QCat;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.mysema.testutil.HibernateTestRunner;

@Ignore
@RunWith(HibernateTestRunner.class)
public class HibernateQueryMutabilityTest{

    private Session session;

    protected HibernateQuery query() {
        return new HibernateQuery(session);
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Test
    public void test() throws SecurityException, IllegalArgumentException,
            NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, IOException {
        QCat cat = QCat.cat;
        HibernateQuery query = query().from(cat);
        new QueryMutability(query).test(cat, cat.name);
    }

    @Test
    public void Clone() {
        QCat cat = QCat.cat;
        HibernateQuery query = query().from(cat).where(cat.name.isNotNull());
        HibernateQuery query2 = query.clone(session);
        assertEquals(query.getMetadata().getJoins(), query2.getMetadata().getJoins());
        assertEquals(query.getMetadata().getWhere(), query2.getMetadata().getWhere());
        query2.list(cat);
    }

}
