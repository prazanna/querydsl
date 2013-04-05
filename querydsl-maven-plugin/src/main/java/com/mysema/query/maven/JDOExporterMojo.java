/*
 * Copyright 2012, Mysema Ltd
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
package com.mysema.query.maven;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.Embedded;

import com.mysema.query.codegen.GenericExporter;

/**
 * JDOExporterMojo calls the GenericExporter tool using the classpath of the module
 * 
 * @goal jdo-export
 * @requiresDependencyResolution test
 * @author tiwe
 */
public class JDOExporterMojo extends AbstractExporterMojo {

    @Override
    protected void configure(GenericExporter exporter) {
        exporter.setEmbeddableAnnotation(EmbeddedOnly.class);
        exporter.setEmbeddedAnnotation(Embedded.class);
        exporter.setEntityAnnotation(PersistenceCapable.class);
        exporter.setSkipAnnotation(NotPersistent.class);
    }

}
