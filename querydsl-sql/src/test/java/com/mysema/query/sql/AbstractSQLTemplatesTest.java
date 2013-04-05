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
package com.mysema.query.sql;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.mysema.query.sql.domain.QSurvey;
import com.mysema.query.types.Path;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.path.SimplePath;
import com.mysema.query.types.template.NumberTemplate;

public abstract class AbstractSQLTemplatesTest {
    
    protected static final QSurvey survey1 = new QSurvey("survey1");
    
    protected static final QSurvey survey2 = new QSurvey("survey2");
    
    protected SQLQuery query;
    
    protected abstract SQLTemplates createTemplates();

    @Before
    public void setUp() {
        SQLTemplates templates = createTemplates();
        templates.newLineToSingleSpace();
        query = new SQLQuery(templates);
    }
    
    @Test
    public void NoFrom() {
        query.getMetadata().addProjection(NumberTemplate.ONE);
        assertEquals("select 1 from dual", query.toString());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void Union() {        
        NumberExpression<Integer> one = NumberTemplate.ONE;
        NumberExpression<Integer> two = NumberTemplate.TWO;
        NumberExpression<Integer> three = NumberTemplate.THREE;
        Path<Integer> col1 = new SimplePath<Integer>(Integer.class,"col1");
        Union union = query.union(
            sq().unique(one.as(col1)),
            sq().unique(two),
            sq().unique(three));
        assertEquals(
                "(select 1 as col1 from dual)\n" +
        	"union\n" +
        	"(select 2 from dual)\n" +
        	"union\n" +
        	"(select 3 from dual)", union.toString());
    }
    
    @Test
    public void InnerJoin() {        
        query.from(survey1).innerJoin(survey2);
        assertEquals("from SURVEY survey1 inner join SURVEY survey2", query.toString());
    }
    
    protected SQLSubQuery sq() {
        return new SQLSubQuery();
    }
    
}
