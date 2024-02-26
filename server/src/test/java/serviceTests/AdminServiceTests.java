package serviceTests;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import service.AdminService;

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
}

