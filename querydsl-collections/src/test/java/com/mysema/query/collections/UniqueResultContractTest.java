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
package com.mysema.query.collections;

import org.junit.Test;

import com.mysema.query.NonUniqueResultException;
import com.mysema.query.types.Expression;

public class UniqueResultContractTest extends AbstractQueryTest{

    @Test(expected=NonUniqueResultException.class)
    public void Unique_Result_Throws_Exception_On_Multiple_Results() {
        CollQueryFactory.from(cat, cats).where(cat.name.isNotNull()).uniqueResult(cat);
    }
    
    @Test
    public void UniqueResult_With_Array() {
        CollQueryFactory.from(cat, cats).where(cat.name.isNotNull()).limit(1).uniqueResult(new Expression[]{cat});
    }
    
}
