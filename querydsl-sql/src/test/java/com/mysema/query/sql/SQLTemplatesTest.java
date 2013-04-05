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
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

import com.mysema.query.types.ConstantImpl;
import com.mysema.query.types.Operation;
import com.mysema.query.types.OperationImpl;
import com.mysema.query.types.Template;
import com.mysema.query.types.TemplateFactory;
import com.mysema.query.types.template.SimpleTemplate;

public class SQLTemplatesTest {

    @Test
    public void test() {
        Template template = TemplateFactory.DEFAULT.create("fetch first {0s} rows only");
        assertTrue(template.getElements().get(1) instanceof Template.AsString);

        SQLSerializer serializer = new SQLSerializer(new DerbyTemplates());
        serializer.handle(SimpleTemplate.create(Object.class, template, ConstantImpl.create(5)));
        assertEquals("fetch first 5 rows only", serializer.toString());
    }
    
    @Test
    public void Quote() {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_\\-]+");
        assertTrue(pattern.matcher("a1").matches());
        assertTrue(pattern.matcher("a").matches());
    }
    
    @Test
    public void Quoting_Performance() {
        // 385 -> 63
        SQLTemplates templates = new H2Templates();
        long start = System.currentTimeMillis();
        int iterations = 1000000;
        for (int i = 0; i < iterations; i++) {
            templates.quoteIdentifier("companies");
        }
        System.err.println(System.currentTimeMillis() - start);
    }
    
    @Test
    public void NextVal() {
        Operation<String> nextval = OperationImpl.create(String.class, SQLTemplates.NEXTVAL, ConstantImpl.create("myseq"));
        assertEquals("nextval('myseq')", new SQLSerializer(SQLTemplates.DEFAULT).handle(nextval).toString());
        // Derby OK
        // H2 OK
        // HSQLDB OK
        // MSSQL OK
        // MySQL
        // Oracle OK
        // Postgres OK
        
    }
    
    
}
