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
package com.mysema.query.apt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.AbstractProcessor;
import javax.tools.JavaCompiler;

import junit.framework.Assert;

import com.mysema.codegen.SimpleCompiler;
import com.mysema.util.FileUtils;

public abstract class AbstractProcessorTest {

    protected static List<String> getFiles(String path) {
        List<String> classes = new ArrayList<String>();
        for (File file : new File(path).listFiles()) {
            if (file.getName().endsWith(".java")) {
                classes.add(file.getPath());
            }else if (file.isDirectory() && !file.getName().startsWith(".")) {
                classes.addAll(getFiles(file.getAbsolutePath()));
            }
        }
        return classes;
    }

    protected void process(Class<? extends AbstractProcessor> processorClass, List<String> classes, String target) throws IOException {
        File out = new File("target/" + target);
        FileUtils.delete(out);
        if (!out.mkdirs()) {
            Assert.fail("Creation of " + out.getPath() + " failed");
        }
        compile(processorClass, classes, target);
    } 
    
    protected void compile(Class<? extends AbstractProcessor> processorClass, List<String> classes, String target) throws IOException {
        JavaCompiler compiler = new SimpleCompiler();
        System.out.println(compiler.getClass().getName());
        List<String> options = new ArrayList<String>(classes.size() + 3);
        options.add("-s");
        options.add("target/" + target);
        options.add("-proc:only");
        options.add("-processor");
        options.add(processorClass.getName());
        options.add("-sourcepath");
        options.add("src/test/java");
        options.addAll(getAPTOptions());
        options.addAll(classes);
        
        System.out.println();
        int compilationResult = compiler.run(null, System.out, System.err, options.toArray(new String[options.size()]));

//        Processor.elementCache.clear();
        if (compilationResult == 0) { 
            System.out.println("Compilation is successful");
        } else {
            Assert.fail("Compilation Failed");
        }
        
    }
    
    protected Collection<String> getAPTOptions() {
        return Collections.emptyList();
    }

}
