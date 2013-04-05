package com.mysema.query.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Enumerated;

import org.junit.Test;

public class Enum3Test {

    public interface BaseInterface extends Serializable {
        String getFoo();
        String getBar();
    }
    
    public enum EnumImplementation implements SpecificInterface {
        FOO,
        BAR;

        public EnumImplementation getValue() {
            return this;
        }
        public String getFoo() {
            return null;
        }
        public String getBar() {
            return name();
        }
    }
    
    public interface SpecificInterface extends BaseInterface {
        EnumImplementation getValue();
    }
    
    @Entity
    static class Entity1 {
        
        @Enumerated(javax.persistence.EnumType.STRING)
        private EnumImplementation value;
        
        public SpecificInterface getValue() {
            return value;
        }

    }
    
    @Entity
    static class Entity2 {
        
        private EnumImplementation value;
        
        @Enumerated(javax.persistence.EnumType.STRING)
        public SpecificInterface getValue() {
            return value;
        }

    }
    
    @Entity
    static class Entity3 {
        
        private EnumImplementation value;
        
        public SpecificInterface getValue() {
            return value;
        }

    }
    
    @Test
    public void test() {
        
    }
    
}
