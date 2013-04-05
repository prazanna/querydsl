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
package com.mysema.query.sql.dml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.mysema.query.QueryException;
import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.types.Null;
import com.mysema.query.types.Path;
import com.mysema.util.ReflectionUtils;

/**
 * Creates the mapping by inspecting the RelationalPath and Object via reflection. 
 * Given bean doesn't need to have @Column metadata, but the fields need to have the same 
 * name as in the given relational path. 
 * 
 * @author tiwe
 *
 */
public class DefaultMapper extends AbstractMapper<Object> {

    public static final DefaultMapper DEFAULT = new DefaultMapper(false);
    
    public static final DefaultMapper WITH_NULL_BINDINGS = new DefaultMapper(true);
    
    private final boolean withNullBindings;
    
    public DefaultMapper() {
        this(false);
    }
    
    public DefaultMapper(boolean withNullBindings) {
        this.withNullBindings = withNullBindings;
    }
    
    @Override
    public Map<Path<?>, Object> createMap(RelationalPath<?> entity, Object bean) {
        try {
            Map<Path<?>, Object> values = new HashMap<Path<?>, Object>();
            Class<?> beanClass = bean.getClass();
            Map<String, Field> fields = getPathFields(entity.getClass());
            for (Field beanField : ReflectionUtils.getFields(beanClass)) {
                if (!Modifier.isStatic(beanField.getModifiers()) && fields.containsKey(beanField.getName())) {
                    Field field = fields.get(beanField.getName());
                    @SuppressWarnings("rawtypes")
                    Path path = (Path<?>) field.get(entity);
                    beanField.setAccessible(true);
                    Object propertyValue = beanField.get(bean);
                    if (propertyValue != null) {
                        values.put(path, propertyValue);
                    } else if (withNullBindings && !isPrimaryKeyColumn(entity, path)) {
                        values.put(path, Null.DEFAULT);
                    }
                }
            }
            return values;    
        } catch (IllegalAccessException e) {
            throw new QueryException(e);
        }
    }
    


}
