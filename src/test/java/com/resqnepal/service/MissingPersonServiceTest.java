package com.resqnepal.service;

import com.resqnepal.model.MissingPerson;
import com.resqnepal.model.enums.MissingPersonStatus;
import com.resqnepal.repository.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MissingPersonServiceTest {
    private MissingPersonService personService;

    @BeforeEach
    public void setUp() {
        DatabaseManager.setTestMode(true);
        DatabaseManager.initializeDatabase();
        personService = new MissingPersonService();
    }

    @AfterEach
    public void tearDown() {
        File file = new File("data/resqnepal_test.db");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testRegisterMissingPerson() throws Exception {
        MissingPerson p = new MissingPerson("P1", "John Doe", "KTM", MissingPersonStatus.MISSING);
        personService.registerMissingPerson(p);
        
        MissingPerson found = personService.findById("P1");
        assertNotNull(found);
        assertEquals("John Doe", found.getName());
    }

    @Test
    public void testUpdateMissingPersonStatus() throws Exception {
        MissingPerson p = new MissingPerson("P2", "Jane Doe", "PKR", MissingPersonStatus.MISSING);
        personService.registerMissingPerson(p);
        
        personService.updateStatus("P2", MissingPersonStatus.LOCATED);
        
        MissingPerson found = personService.findById("P2");
        assertEquals(MissingPersonStatus.LOCATED, found.getStatus());
    }

    @Test
    public void testSearchMissingPersonByName() throws Exception {
        personService.registerMissingPerson(new MissingPerson("P3", "Alice Smith", "KTM", MissingPersonStatus.MISSING));
        personService.registerMissingPerson(new MissingPerson("P4", "Bob Smith", "KTM", MissingPersonStatus.MISSING));
        
        List<MissingPerson> results = personService.searchByName("Smith");
        assertEquals(2, results.size());
    }
}
