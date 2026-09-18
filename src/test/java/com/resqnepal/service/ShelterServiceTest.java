package com.resqnepal.service;

import com.resqnepal.exception.InvalidRequestException;
import com.resqnepal.exception.ShelterFullException;
import com.resqnepal.model.Shelter;
import com.resqnepal.repository.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ShelterServiceTest {
    private ShelterService shelterService;

    @BeforeEach
    public void setUp() {
        DatabaseManager.setTestMode(true);
        DatabaseManager.initializeDatabase();
        shelterService = new ShelterService();
    }

    @AfterEach
    public void tearDown() {
        File file = new File("data/resqnepal_test.db");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testAddShelter() throws Exception {
        Shelter s = new Shelter("S1", "Main Camp", 0, 0, 100, 0, true, true, true);
        shelterService.addShelter(s);
        
        Shelter found = shelterService.findShelter("S1");
        assertNotNull(found);
        assertEquals(100, found.getMaxCapacity());
    }

    @Test
    public void testAdmitPeopleWhenCapacityExists() throws Exception {
        Shelter s = new Shelter("S2", "Camp 2", 0, 0, 50, 10, true, true, true);
        shelterService.addShelter(s);
        
        shelterService.admitRescuedPeople("S2", 20);
        
        Shelter found = shelterService.findShelter("S2");
        assertEquals(30, found.getCurrentOccupancy());
    }

    @Test
    public void testRejectAdmissionWhenCapacityExceeded() throws Exception {
        Shelter s = new Shelter("S3", "Camp 3", 0, 0, 50, 40, true, true, true);
        shelterService.addShelter(s);
        
        assertThrows(ShelterFullException.class, () -> {
            shelterService.admitRescuedPeople("S3", 20);
        });
    }
}
