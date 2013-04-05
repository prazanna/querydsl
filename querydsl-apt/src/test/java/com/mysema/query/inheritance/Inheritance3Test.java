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
package com.mysema.query.inheritance;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.mysema.query.annotations.QueryEntity;
import com.mysema.query.domain.AbstractTest;
import com.mysema.query.types.path.SimplePath;
import com.mysema.query.types.path.StringPath;

public class Inheritance3Test extends AbstractTest {

    /*
     * TODO : map type variables to BeanModels
     */

    @QueryEntity
    public class GenericSupertype<A>{
        A field;
        Collection<A> fieldCol;
        Set<A> fieldSet;
        List<A> fieldList;
        Map<String,A> fieldMap1;
        Map<A,String> fieldMap2;

        String stringField;
    }

    @QueryEntity
    public class GenericSupertypeC<D extends Comparable<D>> extends GenericSupertype<D>{

    }

    @QueryEntity
    public class GenericSupertypeS extends GenericSupertypeC<String>{

    }
    
    @QueryEntity
    public class GenericSupertypeS2 extends GenericSupertype<String>{

    }

    @Test
    public void GenericSupertype() throws SecurityException, NoSuchFieldException {
        cl = QInheritance3Test_GenericSupertype.class;
        match(SimplePath.class, "field");    
    }
    
    @Test
    public void GenericSupertypeC() throws SecurityException, NoSuchFieldException {
        cl = QInheritance3Test_GenericSupertypeC.class;
        match(SimplePath.class, "field");    
    }
    
    @Test
    public void GenericSupertypeS() throws SecurityException, NoSuchFieldException {
        cl = QInheritance3Test_GenericSupertypeS.class;
        match(StringPath.class, "field");
    }
    
    @Test
    public void GenericSupertypeS2() throws SecurityException, NoSuchFieldException {
        cl = QInheritance3Test_GenericSupertypeS2.class;
        match(StringPath.class, "field");
    }

}
