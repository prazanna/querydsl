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

import com.mysema.query.domain.QCat;
import com.mysema.query.jpa.domain.QEmployee;
import com.mysema.query.jpa.domain.QUser;

public class SubQueryTest extends AbstractQueryTest{

    @Test(expected=IllegalArgumentException.class)
    public void WrongUsage() {
        sub().exists();
    }

    @Test
    public void Single_Source() {
        JPASubQuery query = sub();
        query.from(cat);
        assertEquals("select cat\nfrom Cat cat", query.toString());
    }
    
    @Test
    public void Multiple_Sources() {
        JPASubQuery query = sub();
        query.from(cat);
        query.from(fatcat);
        assertEquals("select cat\nfrom Cat cat, Cat fatcat", 
                query.toString());
    }
    
    @Test
    public void In() {
        cat.in(sub().from(cat).list(cat));
    }
        
    @Test
    public void InnerJoin() {
        assertEquals("select cat\nfrom Cat cat\n  inner join cat.mate", 
                sub().from(cat).innerJoin(cat.mate).toString());
    }
    
    @Test
    public void InnerJoin2() {
        QEmployee employee = QEmployee.employee;
        QUser user = QUser.user;
        assertEquals("select employee\nfrom Employee employee\n  inner join employee.user as user", 
                sub().from(employee).innerJoin(employee.user, user).toString());
    }
        
    @Test
    public void LeftJoin() {
        assertEquals("select cat\nfrom Cat cat\n  left join cat.mate", 
                sub().from(cat).leftJoin(cat.mate).toString());
    }
    
    @Test
    public void FullJoin() {
        assertEquals("select cat\nfrom Cat cat\n  full join cat.mate", 
                sub().from(cat).fullJoin(cat.mate).toString());
    }
    
    @Test
    public void Join() {
        assertEquals("select cat\nfrom Cat cat\n  inner join cat.mate", 
                sub().from(cat).join(cat.mate).toString());
    }

    @Test
    public void UniqueProjection() {
        assertToString("(select cat from Cat cat)", 
                sub().from(cat).unique(cat));
    }

    @Test
    public void ListProjection() {
        assertToString("(select cat from Cat cat)", 
                sub().from(cat).list(cat));
    }
    
    @Test
    public void ListContains() {
        assertToString("cat1 in (select cat from Cat cat)", 
                sub().from(cat).list(cat).contains(cat1));
    }
    
    @Test
    public void Exists() {
        assertToString("exists (select 1 from Cat cat)",                        
                sub().from(cat).exists());
    }
    
    @Test
    public void Exists_Where() {
        assertToString("exists (select 1 from Cat cat where cat.weight < ?1)", 
                sub().from(cat).where(cat.weight.lt(1)).exists());
    }
    
    @Test
    public void Exists_Via_Unique() {
        assertToString("exists (select 1 from Cat cat where cat.weight < ?1)", 
                sub().from(cat).where(cat.weight.lt(1)).unique(cat).exists());
    }
    
    @Test
    public void NotExists() {
        assertToString("not exists (select 1 from Cat cat)",                        
                sub().from(cat).notExists());            
    }
    
    @Test
    public void NotExists_Where() {
        assertToString("not exists (select 1 from Cat cat where cat.weight < ?1)",
                sub().from(cat).where(cat.weight.lt(1)).notExists());    
    }
    
    @Test
    public void NotExists_Via_Unique() {
        assertToString("not exists (select 1 from Cat cat where cat.weight < ?1)", 
                sub().from(cat).where(cat.weight.lt(1)).unique(cat).notExists());   
    }

    @Test
    public void Count() {
        assertToString("(select count(cat) from Cat cat)",                        
                sub().from(cat).count());        
    }
    
    @Test
    public void Count_Via_List() {
        assertToString("(select count(cat) from Cat cat)",                        
                sub().from(cat).list(cat).count());
    }
    
    @Test
    public void Count_Name() {
        assertToString("(select count(cat.name) from Cat cat)",                   
                sub().from(cat).list(cat.name).count());
    }

    @Test
    public void Count_Multiple_Sources() {
        QCat other = new QCat("other");
        assertToString("(select count(cat, other) from Cat cat, Cat other)",     
                sub().from(cat, other).count());
    }
    
    @Test
    public void Count_Multiple_Sources_Via_List() {
        QCat other = new QCat("other");
        assertToString("(select count(cat, other) from Cat cat, Cat other)",      
                sub().from(cat, other).list(cat, other).count());
    }

    @Test
    public void Indexed_Access() {
        assertToString("(select count(cat) from Cat cat   " +
        		"left join cat.kittens as cat_kittens_0 " +
        		"where index(cat_kittens_0) = ?1 and cat_kittens_0.name = ?2)", 
        		                                                          
        		sub().from(cat).where(cat.kittens.get(0).name.eq("Kate")).count());
    }
    
    @Test
    public void Indexed_Access_Without_Constant() {
        assertToString("(select count(cat) from Cat cat   " +
                        "left join cat.kittens as cat_kittens_cat_id " +
                        "where index(cat_kittens_cat_id) = cat.id and cat_kittens_cat_id.name = ?1)", 
                                                                                  
                        sub().from(cat).where(cat.kittens.get(cat.id).name.eq("Kate")).count());
    }
    
    @Test
    public void IndexOf() {
        assertToString("(select count(cat) from Cat cat where locate(?1,cat.name)-1 = ?2)",                
                        sub().from(cat).where(cat.name.indexOf("a").eq(1)).count());
    }
    
}
