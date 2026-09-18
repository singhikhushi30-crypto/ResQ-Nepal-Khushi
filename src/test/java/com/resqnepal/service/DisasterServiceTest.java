package com.resqnepal.service;

import com.resqnepal.model.Disaster;
import com.resqnepal.repository.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class DisasterServiceTest {
    private DisasterService disasterService;

    @BeforeEach
    public void setUp() {
        DatabaseManager.setTestMode(true);
        DatabaseManager.initializeDatabase();
        disasterService = new DisasterService();
    }

    @AfterEach
    public void tearDown() {
        File file = new File("data/resqnepal_test.db");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testCreateDisaster() throws Exception {
        Disaster d = new Disaster("D1", "Nepal Flood Simulation", "Nepal", LocalDate.now(), "Test Simulation");
        disasterService.createDisaster(d);
        
        Disaster found = disasterService.findDisaster("D1");
        assertNotNull(found);
        assertEquals("Nepal Flood Simulation", found.getName());
    }

    @Test
    public void testRetrieveDisaster() throws Exception {
        Disaster d = new Disaster("D2", "Earthquake Sim", "KTM", LocalDate.now(), "Another Sim");
        disasterService.createDisaster(d);
        
        assertEquals(1, disasterService.getDisasters().size());
    }
}
