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
package com.mysema.query.domain;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mysema.query.annotations.PropertyType;
import com.mysema.query.annotations.QueryEntity;
import com.mysema.query.annotations.QueryTransient;
import com.mysema.query.annotations.QueryType;

public class BlockingTest extends AbstractTest {

    @QueryEntity
    public static class Entity {

        Entity field1;

        @QueryTransient
        @QueryType(PropertyType.ENTITY)
        Entity field2;

        @QueryTransient
        Entity blockedField;
    }

    @QueryEntity
    public static abstract class Entity2 {

        @QueryTransient
        @QueryType(PropertyType.ENTITY)
        public abstract Entity getField2();

        @QueryTransient
        public abstract Entity getBlockedField();
    }

    @Test
    public void Entity_Fields_are_available() {
        assertTrue(QBlockingTest_Entity.entity.field1 != null);

        cl = QBlockingTest_Entity.class;
        assertMissing("blockedField");
    }
    
}
