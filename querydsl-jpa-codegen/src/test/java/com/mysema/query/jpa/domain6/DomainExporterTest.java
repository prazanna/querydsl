package com.mysema.query.jpa.domain6;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.hibernate.cfg.Configuration;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.mysema.query.jpa.codegen.HibernateDomainExporter;
import com.mysema.util.FileUtils;

public class DomainExporterTest {
    
    @Test
    public void Execute() throws IOException {
        File gen = new File("target/" + getClass().getSimpleName());
        FileUtils.delete(gen);
        Configuration config = new Configuration();
        config.addFile(new File("src/test/resources/com/mysema/query/jpa/domain6/Contact.hbm.xml"));
        HibernateDomainExporter exporter = new HibernateDomainExporter("Q", gen, config);
        exporter.execute();

        File targetFile = new File(gen, "com/mysema/query/jpa/domain6/QContact.java");
        assertContains(targetFile, "ListPath<PhoneNumber, QPhoneNumber> phoneNumbers");
        
        targetFile = new File(gen, "com/mysema/query/jpa/domain6/QPhoneNumber.java");
        assertContains(targetFile, "QPhoneNumber extends BeanPath<PhoneNumber>", 
                                   "StringPath number = createString(\"number\")");
    }

    private static void assertContains(File file, String... strings) throws IOException {
        assertTrue(file.getPath() + " doesn't exist", file.exists());
        String result = Files.toString(file, Charsets.UTF_8);
        for (String str : strings) {
            assertTrue(str + " was not contained", result.contains(str));
        }
    }
    
}
