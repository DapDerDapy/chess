package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import service.AdminService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AdminServiceTests {

    @Mock
    private UserDAO userDAO;

    @Mock
    private AuthDAO authDAO;

    @Mock
    private GameDAO gameDAO;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminService = new AdminService(userDAO, authDAO, gameDAO);
    }

    @Test
    void testClearApplicationData() {
        // Execute the clearApplicationData method
        adminService.clearApplicationData();

        // Verify that each DAO's clear method is called exactly once
        verify(userDAO, times(1)).clear();
        verify(authDAO, times(1)).clearAll();
        verify(gameDAO, times(1)).clearAll();
    }
    @Test
    void testClearApplicationData_WithDaoFailure() {
        // Simulate a RuntimeException when attempting to clear user data
        doThrow(new RuntimeException("Failed to clear user data")).when(userDAO).clear();

        // Execute the clearApplicationData method
        Exception exception = assertThrows(RuntimeException.class, () -> adminService.clearApplicationData());

        // Verify the exception message
        assertEquals("Failed to clear user data", exception.getMessage());

        // Verify that the clear method was attempted on userDAO
        verify(userDAO, times(1)).clear();

        // Verify that the clearAll methods on authDAO and gameDAO were not called due to the failure in userDAO
        verify(authDAO, never()).clearAll();
        verify(gameDAO, never()).clearAll();
    }

}

