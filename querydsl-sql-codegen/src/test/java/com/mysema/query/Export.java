package com.mysema.query;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import com.mysema.query.sql.codegen.MetaDataExporter;


public class Export {
    
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/querydsl";
        Connection conn = DriverManager.getConnection(url, "querydsl", "querydsl");
        
        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setNamePrefix("S");
        exporter.setPackageName("com.mysema.query.jpa.domain.sql");
        exporter.setTargetFolder(new File("../querydsl-jpa/src/test/java"));
//        exporter.setLowerCase(true);
        exporter.export(conn.getMetaData());
        
        conn.close();
    }

}
