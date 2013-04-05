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

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.mysema.query.annotations.QueryEntity;
import com.mysema.query.types.path.ArrayPath;
import com.mysema.query.types.path.ListPath;
import com.mysema.query.types.path.MapPath;
import com.mysema.query.types.path.StringPath;

public class ArrayExtTest {

    private static final QArrayExtTest_BinaryFile binaryFile = QArrayExtTest_BinaryFile.binaryFile;
    
    @QueryEntity
    public static class BinaryFile {

        byte[] contentPart;
        
        List<byte[]> list;
        
        Map<String, byte[]> map1;
        
        Map<byte[], String> map2;
    }

    @Test
    public void BinaryFile_contentPart() {
        assertEquals(ArrayPath.class, binaryFile.contentPart.getClass());
        assertEquals(Byte[].class, binaryFile.contentPart.getType());
    }
    
    @Test
    public void BinaryFile_list() {
        assertEquals(ListPath.class, binaryFile.list.getClass());
        assertEquals(List.class, binaryFile.list.getType());        
        assertEquals(Byte[].class, binaryFile.list.getParameter(0));
        
        assertEquals(ArrayPath.class, binaryFile.list.get(0).getClass());
        assertEquals(Byte[].class, binaryFile.list.get(0).getType());
    }
    
    @Test
    public void BinaryFile_map1() {
        assertEquals(MapPath.class, binaryFile.map1.getClass());
        assertEquals(Map.class, binaryFile.map1.getType());
        assertEquals(String.class, binaryFile.map1.getParameter(0));
        assertEquals(Byte[].class, binaryFile.map1.getParameter(1));
        
        assertEquals(ArrayPath.class, binaryFile.map1.get("").getClass());
        assertEquals(Byte[].class, binaryFile.map1.get("").getType());
    }
    
    @Test
    public void BinaryFile_map2() {
        assertEquals(MapPath.class, binaryFile.map2.getClass());
        assertEquals(Map.class, binaryFile.map2.getType());
        assertEquals(Byte[].class, binaryFile.map2.getParameter(0));
        assertEquals(String.class, binaryFile.map2.getParameter(1));
        
        assertEquals(StringPath.class, binaryFile.map2.get(new Byte[0]).getClass());
        assertEquals(String.class, binaryFile.map2.get(new Byte[0]).getType());
    }
    
}
