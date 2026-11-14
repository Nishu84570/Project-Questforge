
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import questforge.dto.AdminDashboardResponseDTO;
import questforge.service.AdminDashboardService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardControllerTest {

    @Mock
    private AdminDashboardService adminDashboardService;

    @Mock
    private AdminDashboardResponseDTO dashboardDTO;

    @InjectMocks
    private AdminDashboardController adminDashboardController;


    @Test
    void getDashboardStats_ShouldReturnDashboardData_WhenDataExists() {

        when(adminDashboardService.getDashboardStats())
                .thenReturn(dashboardDTO);

        ResponseEntity<AdminDashboardResponseDTO> response =
                adminDashboardController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(dashboardDTO, response.getBody());

        verify(adminDashboardService, times(1))
                .getDashboardStats();
    }


    @Test
    void getDashboardStats_ShouldReturnOk_WhenServiceReturnsNull() {

        when(adminDashboardService.getDashboardStats())
                .thenReturn(null);

        ResponseEntity<AdminDashboardResponseDTO> response =
                adminDashboardController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        verify(adminDashboardService, times(1))
                .getDashboardStats();
    }

    @Test
    void getDashboardStats_ShouldCallServiceOnlyOnce() {

        when(adminDashboardService.getDashboardStats())
                .thenReturn(dashboardDTO);

        adminDashboardController.getDashboardStats();

        verify(adminDashboardService, times(1))
                .getDashboardStats();
    }

  
    @Test
    void getDashboardStats_ShouldPropagateException_WhenServiceFails() {

        when(adminDashboardService.getDashboardStats())
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminDashboardController.getDashboardStats()
        );

        assertEquals("Database error", exception.getMessage());

        verify(adminDashboardService, times(1))
                .getDashboardStats();
    }
}
