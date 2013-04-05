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

import com.mysema.query.annotations.QueryEmbedded;
import com.mysema.query.annotations.QueryEntity;
import com.mysema.query.annotations.QueryInit;

public class QueryEmbedded4Test {

    @QueryEntity
    public static class User {
    
        @QueryEmbedded
        @QueryInit("city.name")
        Address address;
     
        @QueryEmbedded
        Complex<String> complex;
    }
    
    public static class Address {
        
        @QueryEmbedded
        City city;
        
        String name;
    }
    
    public static class City {
     
        String name;
        
    }
    
    public static class Complex<T extends Comparable<T>> implements Comparable<Complex<T>> {

        T a;
        
        @Override
        public int compareTo(Complex<T> arg0) {
            return 0;
        }
        
        public boolean equals(Object o) {
            return o == this;
        }
        
    }
    
    @Test
    public void User_Address_City() {
        assertNotNull(QQueryEmbedded4Test_User.user.address.city);
    }
    
    @Test
    public void User_Address_Name() {
        assertNotNull(QQueryEmbedded4Test_User.user.address.name);
    }
    
    @Test
    public void User_Address_City_Name() {
        assertNotNull(QQueryEmbedded4Test_User.user.address.city.name);
    }
    
    @Test
    public void User_Complex_a() {
        assertNotNull(QQueryEmbedded4Test_User.user.complex.a);
    }
    
}
