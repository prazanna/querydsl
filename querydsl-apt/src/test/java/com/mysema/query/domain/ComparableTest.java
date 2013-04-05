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

import static org.junit.Assert.*;

import org.junit.Test;

import com.mysema.query.annotations.QueryEmbeddable;
import com.mysema.query.annotations.QueryEntity;

public class ComparableTest {
    
    @QueryEntity
    public static class CustomComparable implements Comparable<CustomComparable>{

        @Override
        public int compareTo(CustomComparable o) {
            return 0;
        }
        
        public boolean equals(Object o) {
            return o == this;
        }
        
    }
    
    @QueryEmbeddable
    public static class CustomComparable2 implements Comparable<CustomComparable2>{

        @Override
        public int compareTo(CustomComparable2 o) {
            return 0;
        }
        
        public boolean equals(Object o) {
            return o == this;
        }
        
    }
    
    @Test
    public void CustomComparable_is_Properly_Handled() {
        assertNotNull(QComparableTest_CustomComparable.customComparable.asc());
    }

}
