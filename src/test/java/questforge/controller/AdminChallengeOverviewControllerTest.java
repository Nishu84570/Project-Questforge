
package questforge.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import questforge.dto.AdminChallengeOverviewDTO;
import questforge.service.AdminChallengeOverviewService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminChallengeOverviewControllerTest {

    @Mock
    private AdminChallengeOverviewService adminChallengeOverviewService;

    @InjectMocks
    private AdminChallengeOverviewController adminChallengeOverviewController;

    private List<AdminChallengeOverviewDTO> overviewList;

    @BeforeEach
    void setUp() {
        overviewList = Arrays.asList(
                mock(AdminChallengeOverviewDTO.class),
                mock(AdminChallengeOverviewDTO.class)
        );
    }

  
    @Test
    void getChallengeOverview_ShouldReturnOverviewList_WhenDataExists() {

        when(adminChallengeOverviewService.getChallengeOverview())
                .thenReturn(overviewList);

        ResponseEntity<List<AdminChallengeOverviewDTO>> response =
                adminChallengeOverviewController.getChallengeOverview();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertSame(overviewList, response.getBody());

        verify(adminChallengeOverviewService, times(1))
                .getChallengeOverview();
    }

    @Test
    void getChallengeOverview_ShouldReturnEmptyList_WhenNoChallengesExist() {

        when(adminChallengeOverviewService.getChallengeOverview())
                .thenReturn(Collections.emptyList());

        ResponseEntity<List<AdminChallengeOverviewDTO>> response =
                adminChallengeOverviewController.getChallengeOverview();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(adminChallengeOverviewService, times(1))
                .getChallengeOverview();
    }

 
    @Test
    void getChallengeOverview_ShouldCallServiceOnlyOnce() {

        when(adminChallengeOverviewService.getChallengeOverview())
                .thenReturn(overviewList);

        adminChallengeOverviewController.getChallengeOverview();

        verify(adminChallengeOverviewService, times(1))
                .getChallengeOverview();
    }


    @Test
    void getChallengeOverview_ShouldPropagateException_WhenServiceFails() {

        when(adminChallengeOverviewService.getChallengeOverview())
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> adminChallengeOverviewController.getChallengeOverview()
        );

        assertEquals("Database error", exception.getMessage());

        verify(adminChallengeOverviewService, times(1))
                .getChallengeOverview();
    }
}
