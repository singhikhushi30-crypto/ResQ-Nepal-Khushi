package com.resqnepal.service;

import com.resqnepal.exception.InvalidRequestException;
import com.resqnepal.exception.ResourceUnavailableException;
import com.resqnepal.model.Resource;
import com.resqnepal.model.enums.ResourceStatus;
import com.resqnepal.repository.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceServiceTest {
    private ResourceService resourceService;

    @BeforeEach
    public void setUp() {
        DatabaseManager.setTestMode(true);
        DatabaseManager.initializeDatabase();
        resourceService = new ResourceService();
    }

    @AfterEach
    public void tearDown() {
        File file = new File("data/resqnepal_test.db");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testAddValidResource() throws Exception {
        Resource r = new Resource("R1", "Water", "Food", 100, 0, 0, ResourceStatus.AVAILABLE);
        resourceService.addResource(r);
        
        Resource found = resourceService.findResource("R1");
        assertNotNull(found);
        assertEquals(100, found.getQuantity());
    }

    @Test
    public void testAllocateValidQuantity() throws Exception {
        Resource r = new Resource("R2", "Blanket", "Supply", 50, 0, 0, ResourceStatus.AVAILABLE);
        resourceService.addResource(r);
        
        resourceService.allocateResource("R2", 10);
        
        Resource found = resourceService.findResource("R2");
        assertEquals(40, found.getQuantity());
    }

    @Test
    public void testPreventAllocationOfDamagedResource() throws Exception {
        Resource r = new Resource("R3", "Boat", "Transport", 1, 0, 0, ResourceStatus.DAMAGED);
        resourceService.addResource(r);
        
        assertThrows(ResourceUnavailableException.class, () -> {
            resourceService.allocateResource("R3", 1);
        });
    }

    @Test
    public void testPreventAllocationGreaterThanAvailable() throws Exception {
        Resource r = new Resource("R4", "Medkits", "Medical", 10, 0, 0, ResourceStatus.AVAILABLE);
        resourceService.addResource(r);
        
        assertThrows(ResourceUnavailableException.class, () -> {
            resourceService.allocateResource("R4", 20);
        });
    }

    @Test
    public void testReleaseResourceCorrectly() throws Exception {
        Resource r = new Resource("R5", "Food Rations", "Food", 10, 0, 0, ResourceStatus.IN_USE);
        resourceService.addResource(r);
        
        resourceService.releaseResource("R5", 5);
        
        Resource found = resourceService.findResource("R5");
        assertEquals(15, found.getQuantity());
        assertEquals(ResourceStatus.AVAILABLE, found.getStatus());
    }
}
