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
package com.mysema.query.apt.jdo;

import java.lang.annotation.Annotation;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.Embedded;

import com.mysema.query.annotations.QueryEntities;
import com.mysema.query.apt.AbstractQuerydslProcessor;
import com.mysema.query.apt.Configuration;
import com.mysema.query.apt.DefaultConfiguration;
import com.mysema.query.codegen.Keywords;

/**
 * AnnotationProcessor for JDO which takes {@link PersistenceCapable}, {@link EmbeddedOnly} and 
 * {@link NotPersistent} into account
 * 
 * @author tiwe
 *
 */
@SupportedAnnotationTypes({"com.mysema.query.annotations.*","javax.jdo.annotations.*"})
public class JDOAnnotationProcessor extends AbstractQuerydslProcessor {

    @Override
    protected Configuration createConfiguration(RoundEnvironment roundEnv) {
        Class<? extends Annotation> entity = PersistenceCapable.class;
        Class<? extends Annotation> embeddable = EmbeddedOnly.class;
        Class<? extends Annotation> embedded = Embedded.class;
        Class<? extends Annotation> skip = NotPersistent.class;
        return new DefaultConfiguration(roundEnv, processingEnv.getOptions(), Keywords.JDO, 
                QueryEntities.class, entity, null, embeddable, embedded, skip);        
    }
}
