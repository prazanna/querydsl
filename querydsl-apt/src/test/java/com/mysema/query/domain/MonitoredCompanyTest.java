package com.mysema.query.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class MonitoredCompanyTest {
    
    @Test
    public void test() {
        QMonitoredCompany monitoredCompany = QMonitoredCompany.monitoredCompany; 
        assertNotNull(monitoredCompany.companyGroup);
        assertNotNull(monitoredCompany.companyGroup.mainCompany);
    }

}
