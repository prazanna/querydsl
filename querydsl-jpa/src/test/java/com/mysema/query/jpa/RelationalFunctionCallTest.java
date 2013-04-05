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

import com.mysema.query.jpa.NativeSQLSerializer;
import com.mysema.query.sql.RelationalFunctionCall;
import com.mysema.query.sql.RelationalPathBase;
import com.mysema.query.sql.SQLSerializer;
import com.mysema.query.sql.SQLServerTemplates;
import com.mysema.query.sql.SQLSubQuery;
import com.mysema.query.types.PathMetadataFactory;
import com.mysema.query.types.SubQueryExpression;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.StringPath;

public class RelationalFunctionCallTest {
    
//    @Schema("PUBLIC")
//    @Table("SURVEY")
    public class QSurvey extends RelationalPathBase<QSurvey>{

        private static final long serialVersionUID = -7427577079709192842L;
     
        public final StringPath name = createString("NAME");

        public QSurvey(String path) {
            super(QSurvey.class, PathMetadataFactory.forVariable(path), "PUBLIC", "SURVEY");
        }
        
    }
    
    @Test
    public void FunctionCall() {
        //select tab.col from Table tab join TableValuedFunction('parameter') func on tab.col not like func.col

        QSurvey table = new QSurvey("SURVEY");
        RelationalFunctionCall<String> func = RelationalFunctionCall.create(String.class, "TableValuedFunction", "parameter");
        PathBuilder<String> funcAlias = new PathBuilder<String>(String.class, "tokFunc");
        SQLSubQuery sq = new SQLSubQuery();
        SubQueryExpression<?> expr = sq.from(table)
            .join(func, funcAlias).on(table.name.like(funcAlias.getString("prop")).not()).list(table.name);
        
        SQLSerializer serializer = new NativeSQLSerializer(new SQLServerTemplates());
        serializer.serialize(expr.getMetadata(), false);
        assertEquals("select SURVEY.NAME\n" +
                "from SURVEY SURVEY\n" +
                "join TableValuedFunction(?1) as tokFunc\n" +
                "on not SURVEY.NAME like tokFunc.prop escape '\\'", serializer.toString());

    }

}
