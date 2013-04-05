package com.mysema.query.domain;

import static org.junit.Assert.*;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;
import org.junit.Test;

public class JodaTest {

    @MappedSuperclass
    @Access(AccessType.FIELD)
    public abstract class BaseEntity {

        public abstract Long getId();

        @Temporal(TemporalType.TIMESTAMP)
        private Date createdDate;

        public boolean isNew() {
            return null == getId();
        }

        public DateTime getCreatedDate() {
            return null == createdDate ? null : new DateTime(createdDate);
        }

        public void setCreatedDate(DateTime creationDate) {
            this.createdDate = null == creationDate ? null : creationDate.toDate();
        }
    }
    
    @Test
    public void test() {
        assertEquals(Date.class, QJodaTest_BaseEntity.baseEntity.createdDate.getType());
    }
}
