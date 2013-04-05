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
package com.mysema.util;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

public class MathUtilsTest {

    @Test
    public void Cast() {
        Integer value = Integer.valueOf(1);
        assertEquals(BigDecimal.class, MathUtils.cast(value, BigDecimal.class).getClass());
        assertEquals(BigInteger.class, MathUtils.cast(value, BigInteger.class).getClass());
        assertEquals(Double.class, MathUtils.cast(value, Double.class).getClass());
        assertEquals(Float.class, MathUtils.cast(value, Float.class).getClass());
        assertEquals(Integer.class, MathUtils.cast(value, Integer.class).getClass());
        assertEquals(Long.class, MathUtils.cast(value, Long.class).getClass());
        assertEquals(Short.class, MathUtils.cast(value, Short.class).getClass());
        assertEquals(Byte.class, MathUtils.cast(value, Byte.class).getClass());
    }

}
