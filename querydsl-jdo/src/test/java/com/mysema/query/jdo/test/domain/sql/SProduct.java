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
package com.mysema.query.jdo.test.domain.sql;

import static com.mysema.query.types.PathMetadataFactory.forVariable;

import com.mysema.query.sql.ForeignKey;
import com.mysema.query.sql.PrimaryKey;
import com.mysema.query.sql.RelationalPathBase;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.BeanPath;
import com.mysema.query.types.path.DatePath;
import com.mysema.query.types.path.DateTimePath;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;
import com.mysema.query.types.path.TimePath;


/**
 * SProduct is a Querydsl query type for SProduct
 */
//@Table(value="PRODUCT")
public class SProduct extends RelationalPathBase<SProduct> {

    private static final long serialVersionUID = -590374403;

    public static final SProduct product = new SProduct("PRODUCT");

    public final NumberPath<Integer> amount = createNumber("AMOUNT", Integer.class);

    public final DatePath<java.sql.Date> datefield = createDate("DATEFIELD", java.sql.Date.class);

    public final StringPath description = createString("DESCRIPTION");

    public final StringPath name = createString("NAME");

    public final NumberPath<Double> price = createNumber("PRICE", Double.class);

    public final NumberPath<Long> productId = createNumber("PRODUCT_ID", Long.class);

    public final DateTimePath<java.util.Date> publicationdate = createDateTime("PUBLICATIONDATE", java.util.Date.class);

    public final TimePath<java.sql.Time> timefield = createTime("TIMEFIELD", java.sql.Time.class);

    public final PrimaryKey<SProduct> sysIdx47 = createPrimaryKey(productId);

    public final ForeignKey<SStoreProducts> _storeProductsFk2 = new ForeignKey<SStoreProducts>(this, productId, "PRODUCT_ID_EID");

    public final ForeignKey<SBook> _bookFk1 = new ForeignKey<SBook>(this, productId, "BOOK_ID");

    public final ForeignKey<SStoreProductsbyname> _storeProductsbynameFk2 = new ForeignKey<SStoreProductsbyname>(this, productId, "PRODUCT_ID_VID");

    public SProduct(String variable) {
        super(SProduct.class, forVariable(variable), null, "PRODUCT");
    }

    public SProduct(BeanPath<? extends SProduct> entity) {
        super(entity.getType(),entity.getMetadata(), null, "PRODUCT");
    }

    public SProduct(PathMetadata<?> metadata) {
        super(SProduct.class, metadata, null, "PRODUCT");
    }
    
}

