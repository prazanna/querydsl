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
package com.mysema.query;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mysema.query.sql.RelationalPathBase;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.PathMetadataFactory;
import com.mysema.query.types.QBean;
import com.mysema.query.types.path.BeanPath;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

public class QBeanTest {

//    @Table("PERSON")
    public static class QPerson extends RelationalPathBase<QPerson> {
        private static final long serialVersionUID = 609527362;
        public static final QPerson person = new QPerson("PERSON");
        public final StringPath firstName = createString("FIRST_NAME");
        public final NumberPath<Integer> id = createNumber("ID", Integer.class);
        public final StringPath lastName = createString("LAST_NAME");

        public QPerson(String variable) {
           super(QPerson.class, PathMetadataFactory.forVariable(variable), null, "PERSON");
        }

        public QPerson(BeanPath<? extends QPerson> entity) {
            super(entity.getType(), entity.getMetadata(), null, "PERSON");
        }

        public QPerson(PathMetadata<?> metadata) {
            super(QPerson.class, metadata, null, "PERSON");
        }

    }

    public static class Person {
        private int id;
        private String firstName;
        private String lastName;
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getFirstName() {
            return firstName;
        }
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
        public String getLastName() {
            return lastName;
        }
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

    }

    @Test
    public void NewInstance() {
        QPerson p = QPerson.person;
        QBean<Person> projection = new QBean<Person>(Person.class, p.id, p.firstName.as("firstName"), p.lastName.as("lastName"));

        Person person = projection.newInstance(3, "John", "Doe");
        assertEquals(3,      person.getId());
        assertEquals("John", person.getFirstName());
        assertEquals("Doe",  person.getLastName());
    }

}
