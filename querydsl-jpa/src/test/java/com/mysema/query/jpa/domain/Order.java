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
package com.mysema.query.jpa.domain;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.IndexColumn;

/**
 * The Class Order.
 */
@Entity
@Table(name="order_")
public class Order {
    @ManyToOne
    Customer customer;

    @ElementCollection
    @IndexColumn(name = "_index")
    List<Integer> deliveredItemIndices;

    @Id
    long id;

    @OneToMany
    @IndexColumn(name = "_index")
    List<Item> items;
  
    @OneToMany
    @JoinTable(name = "LineItems")
    @IndexColumn(name = "_index")
    List<Item> lineItems;

    boolean paid;
}
