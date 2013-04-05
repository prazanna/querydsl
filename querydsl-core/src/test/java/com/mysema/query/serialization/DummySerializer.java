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
package com.mysema.query.serialization;

import com.mysema.query.support.SerializerBase;
import com.mysema.query.types.FactoryExpression;
import com.mysema.query.types.ParamExpression;
import com.mysema.query.types.SubQueryExpression;
import com.mysema.query.types.Templates;

public class DummySerializer extends SerializerBase<DummySerializer>{

    public DummySerializer(Templates templates) {
        super(templates);
    }

    @Override
    public Void visit(SubQueryExpression<?> query, Void context) {
        return null;
    }

    @Override
    public Void visit(FactoryExpression<?> expr, Void context) {
        return null;
    }

    @Override
    public Void visit(ParamExpression<?> expr, Void context) {
        return null;
    }

}
