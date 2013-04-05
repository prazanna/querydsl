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
package com.mysema.query.sql.oracle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.mysema.query.types.path.NumberPath;

public class OracleGrammarTest {

    @Test
    public void Constants() {
        assertNotNull(OracleGrammar.level);
        assertNotNull(OracleGrammar.rownum);
        assertNotNull(OracleGrammar.sysdate);
    }

    @Test
    public void SumOver() {
        NumberPath<Integer> intPath = new NumberPath<Integer>(Integer.class, "intPath");
        SumOver<Integer> sumOver = OracleGrammar.sumOver(intPath).orderBy(intPath).partition(intPath);
        assertEquals("sum(intPath) over (partition by intPath order by intPath)", sumOver.toString());
    }

}
