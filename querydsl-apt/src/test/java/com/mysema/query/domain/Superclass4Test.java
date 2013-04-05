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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.mysema.query.annotations.QueryEntity;

public class Superclass4Test {
    
    public static class SuperClass {
        
        String superClassProperty;
        
    }
    
    @QueryEntity
    public static class Entity extends SuperClass {
        
        String entityProperty;
        
    }

    @Test
    public void SuperClass_Properties() {
        assertNotNull(QSuperclass4Test_SuperClass.superClass.superClassProperty);
    }
    
    @Test
    public void Entity_Properties() {
        assertNotNull(QSuperclass4Test_Entity.entity.entityProperty);
        assertNotNull(QSuperclass4Test_Entity.entity.superClassProperty);
    }
    
}
