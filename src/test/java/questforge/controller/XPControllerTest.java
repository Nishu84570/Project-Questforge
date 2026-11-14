
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import questforge.model.User;
import questforge.service.XPService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class XPControllerTest {

    @Mock
    private XPService xpService;

    @InjectMocks
    private XPController xpController;

    @Test
    void addXp_ShouldReturnUpdatedUser() {

        User expected = mock(User.class);

        when(xpService.addXp(1L, 100L))
                .thenReturn(expected);

        ResponseEntity<User> response =
                xpController.addXp(1L, 100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());

        verify(xpService).addXp(1L, 100L);
    }

    @Test
    void awardChallengeXp_ShouldReturnUpdatedUser() {

        User expected = mock(User.class);

        when(xpService.awardChallengeXp(1L, "HARD"))
                .thenReturn(expected);

        ResponseEntity<User> response =
                xpController.awardChallengeXp(1L, "HARD");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());

        verify(xpService).awardChallengeXp(1L, "HARD");
    }

    @Test
    void awardDailyQuestXp_ShouldReturnUpdatedUser() {

        User expected = mock(User.class);

        when(xpService.awardDailyQuestXp(1L, 50L))
                .thenReturn(expected);

        ResponseEntity<User> response =
                xpController.awardDailyQuestXp(1L, 50L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());

        verify(xpService).awardDailyQuestXp(1L, 50L);
    }

    @Test
    void calculateLevel_ShouldReturnLevel() {

        when(xpService.calculateLevel(1000L))
                .thenReturn(6);

        ResponseEntity<Integer> response =
                xpController.calculateLevel(1000L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(6, response.getBody());

        verify(xpService).calculateLevel(1000L);
    }

    @Test
    void calculateChallengeXp_ShouldReturnReward() {

        when(xpService.calculateChallengeXp("HARD"))
                .thenReturn(150L);

        ResponseEntity<Long> response =
                xpController.calculateChallengeXp("HARD");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(150L, response.getBody());

        verify(xpService).calculateChallengeXp("HARD");
    }
}
