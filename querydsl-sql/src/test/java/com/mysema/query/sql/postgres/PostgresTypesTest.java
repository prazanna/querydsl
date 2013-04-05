package com.mysema.query.sql.postgres;

import java.lang.reflect.Field;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mysema.query.Connections;
import com.mysema.query.sql.JDBCTypeMapping;
import com.mysema.query.sql.PostgresTemplates;
import com.mysema.query.sql.SQLTemplates;

public class PostgresTypesTest {
    
    @Before
    public void setUp() throws SQLException, ClassNotFoundException {
        Connections.initPostgres();
    }
    
    @After
    public void tearDown() throws SQLException {
        Connections.close();
    }
    
    @Test
    public void test() throws SQLException, IllegalArgumentException, IllegalAccessException {
        SQLTemplates templates = new PostgresTemplates();
        Connections.dropTable(templates, "type_tests");
        Statement stmt = Connections.getStatement();
        stmt.execute("create table type_tests (" +
                "_numeric_1 numeric(1), " +
                "_numeric_2 numeric(2), " +
                "_numeric_3 numeric(3), " +
                "_numeric_4 numeric(3), " +
                "_smallint smallint, " + 
                "_integer integer, " +
                "_bigint bigint, " +
                "_decimal decimal, " +
                "_numeric numeric, " +
                "_real real, " +
                "_double_precision double precision, " +
                "_serial serial, " +
                "_bigserial bigserial)");
        
        Map<Integer, String> types = new HashMap<Integer, String>();
        for (Field field : java.sql.Types.class.getFields()) {
            types.put((Integer)field.get(null), field.getName());
        }
        
        JDBCTypeMapping jdbcTypeMapping = new JDBCTypeMapping();
        
        DatabaseMetaData metadata = Connections.getConnection().getMetaData();
        ResultSet rs = metadata.getColumns(null, null, "type_tests", null);
        try {
            while (rs.next()) {
                System.out.println(rs.getString("COLUMN_NAME"));
                System.out.println(types.get(rs.getInt("DATA_TYPE")));
                System.out.println(rs.getInt("COLUMN_SIZE"));
                System.out.println(rs.getInt("DECIMAL_DIGITS"));
                System.out.println(jdbcTypeMapping.get(
                        rs.getInt("DATA_TYPE"), 
                        rs.getInt("COLUMN_SIZE"), 
                        rs.getInt("DECIMAL_DIGITS")));
                System.out.println();
            }
        } finally {
            rs.close();
        }
    }

}
