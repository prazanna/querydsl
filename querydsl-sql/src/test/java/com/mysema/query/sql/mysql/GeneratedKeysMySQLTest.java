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
package com.mysema.query.sql.mysql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mysema.query.QGeneratedKeysEntity;
import com.mysema.query.sql.H2Templates;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.testutil.FilteringTestRunner;

@RunWith(FilteringTestRunner.class)
public class GeneratedKeysMySQLTest {

    private Connection conn;

    private Statement stmt;

    @Before
    public void setUp() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/querydsl";
        conn = DriverManager.getConnection(url, "querydsl", "querydsl");
        stmt = conn.createStatement();
    }

    @After
    public void tearDown() throws SQLException{
        try{
            stmt.close();
        }finally{
            conn.close();
        }
    }

    @Test
    public void test() throws SQLException{
        stmt.execute("drop table if exists GENERATED_KEYS");
        stmt.execute("create table GENERATED_KEYS(" +
                 "ID int AUTO_INCREMENT PRIMARY KEY, " +
                 "NAME varchar(30))");

        QGeneratedKeysEntity entity = new QGeneratedKeysEntity("entity");
        SQLInsertClause insertClause = new SQLInsertClause(conn, new H2Templates(), entity);
        ResultSet rs = insertClause.set(entity.name, "Hello").executeWithKeys();
        ResultSetMetaData md = rs.getMetaData();
        System.out.println(md.getColumnName(1));
        
        assertTrue(rs.next());
        assertEquals(1, rs.getInt(1));
        assertFalse(rs.next());

        insertClause = new SQLInsertClause(conn, new H2Templates(), entity);
        rs = insertClause.set(entity.name, "World").executeWithKeys();
        assertTrue(rs.next());
        assertEquals(2, rs.getInt(1));
        assertFalse(rs.next());

    }

}
