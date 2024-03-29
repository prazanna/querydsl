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
package com.mysema.query.jpa;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mysema.query.DefaultQueryMetadata;
import com.mysema.query.JoinType;
import com.mysema.query.QueryMetadata;
import com.mysema.query.domain.QCat;
import com.mysema.query.jpa.domain.Location;
import com.mysema.query.jpa.domain.QEmployee;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

public class JPQLSerializerTest {
        
    @Test
    public void FromWithCustomEntityName() {
        JPQLSerializer serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
        EntityPath<Location> entityPath = new EntityPathBase<Location>(Location.class, "entity");
        QueryMetadata md = new DefaultQueryMetadata();
        md.addJoin(JoinType.DEFAULT, entityPath);
        serializer.serialize(md, false, null);
        assertEquals("select entity\nfrom Location2 entity", serializer.toString());
    }
    
    @Test
    public void Join_With() {
        QCat cat = QCat.cat;
        JPQLSerializer serializer = new JPQLSerializer(HQLTemplates.DEFAULT);        
        QueryMetadata md = new DefaultQueryMetadata();
        md.addJoin(JoinType.DEFAULT, cat);
        md.addJoin(JoinType.INNERJOIN, cat.mate);
        md.addJoinCondition(cat.mate.alive);
        serializer.serialize(md, false, null);
        assertEquals("select cat\nfrom Cat cat\n  inner join cat.mate with cat.mate.alive", serializer.toString());
    }

    @Test
    public void NormalizeNumericArgs() {
        JPQLSerializer serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
        NumberPath<Double> doublePath = new NumberPath<Double>(Double.class, "doublePath");
        serializer.handle(doublePath.add(1));
        serializer.handle(doublePath.between((float)1.0, 1l));
        serializer.handle(doublePath.lt((byte)1));
        for (Object constant : serializer.getConstantToLabel().keySet()) {
            assertEquals(Double.class, constant.getClass());
        }
    }

    @Test
    public void Delete_Clause_Uses_DELETE_FROM() {
        QEmployee employee = QEmployee.employee;
        JPQLSerializer serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
        QueryMetadata md = new DefaultQueryMetadata();
        md.addJoin(JoinType.DEFAULT, employee);
        md.addWhere(employee.lastName.isNull());
        serializer.serializeForDelete(md);
        assertEquals("delete from Employee employee\nwhere employee.lastName is null", serializer.toString());
    }
    
    @Test
    public void Delete_With_SubQuery() {
        QCat parent = QCat.cat;
        QCat child = new QCat("kitten");
        
        JPQLSerializer serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
        QueryMetadata md = new DefaultQueryMetadata();
        md.addJoin(JoinType.DEFAULT, child);
        md.addWhere(
            child.id.eq(1)
            .and(new JPASubQuery()
                .from(parent)
                .where(parent.id.eq(2), child.in(parent.kittens)).exists()));
        serializer.serializeForDelete(md);
        assertEquals("delete from Cat kitten\n" +
                "where kitten.id = ?1 and exists (select 1\n" +
        	"from Cat cat\nwhere cat.id = ?2 and kitten in elements(cat.kittens))", serializer.toString());
    }
    
    @Test
    public void In() {
        //$.parameterRelease.id.eq(releaseId).and($.parameterGroups.any().id.in(filter.getGroups()));
    }
    
    @Test
    public void Like() {
        JPQLSerializer serializer = new JPQLSerializer(HQLTemplates.DEFAULT);
        serializer.handle(new StringPath("str").contains("abc!"));
        assertEquals("str like ?1 escape '!'", serializer.toString());
        assertEquals("%abc!!%", serializer.getConstantToLabel().keySet().iterator().next().toString());
    }
}
 