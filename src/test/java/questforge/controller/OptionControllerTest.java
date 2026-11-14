
package questforge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import questforge.dto.OptionRequestDTO;
import questforge.dto.OptionResponseDTO;
import questforge.service.OptionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OptionControllerTest {

    @Mock
    private OptionService optionService;

    @InjectMocks
    private OptionController optionController;

    private static final Long OPTION_ID = 1L;
    private static final Long QUESTION_ID = 10L;

    @Test
    void createOption_shouldReturnCreatedOption() {
        OptionRequestDTO request = mock(OptionRequestDTO.class);
        OptionResponseDTO option = mock(OptionResponseDTO.class);

        when(optionService.createOption(request)).thenReturn(option);

        ResponseEntity<OptionResponseDTO> response =
                optionController.createOption(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(option, response.getBody());

        verify(optionService).createOption(request);
    }

    @Test
    void getAllOptions_shouldReturnAllOptions() {
        List<OptionResponseDTO> options = List.of(
                mock(OptionResponseDTO.class),
                mock(OptionResponseDTO.class)
        );

        when(optionService.getAllOptions()).thenReturn(options);

        ResponseEntity<List<OptionResponseDTO>> response =
                optionController.getAllOptions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(options, response.getBody());

        verify(optionService).getAllOptions();
    }

    @Test
    void getAllOptions_shouldReturnEmptyList() {
        when(optionService.getAllOptions()).thenReturn(List.of());

        ResponseEntity<List<OptionResponseDTO>> response =
                optionController.getAllOptions();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getOptionById_shouldReturnOption() {
        OptionResponseDTO option = mock(OptionResponseDTO.class);

        when(optionService.getOptionById(OPTION_ID)).thenReturn(option);

        ResponseEntity<OptionResponseDTO> response =
                optionController.getOptionById(OPTION_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(option, response.getBody());

        verify(optionService).getOptionById(OPTION_ID);
    }

    @Test
    void getOptionsByQuestion_shouldReturnQuestionOptions() {
        List<OptionResponseDTO> options = List.of(
                mock(OptionResponseDTO.class),
                mock(OptionResponseDTO.class)
        );

        when(optionService.getOptionsByQuestion(QUESTION_ID))
                .thenReturn(options);

        ResponseEntity<List<OptionResponseDTO>> response =
                optionController.getOptionsByQuestion(QUESTION_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(options, response.getBody());

        verify(optionService).getOptionsByQuestion(QUESTION_ID);
    }

    @Test
    void getOptionsByQuestion_shouldReturnEmptyList() {
        when(optionService.getOptionsByQuestion(QUESTION_ID))
                .thenReturn(List.of());

        ResponseEntity<List<OptionResponseDTO>> response =
                optionController.getOptionsByQuestion(QUESTION_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void updateOption_shouldReturnUpdatedOption() {
        OptionRequestDTO request = mock(OptionRequestDTO.class);
        OptionResponseDTO updatedOption = mock(OptionResponseDTO.class);

        when(optionService.updateOption(OPTION_ID, request))
                .thenReturn(updatedOption);

        ResponseEntity<OptionResponseDTO> response =
                optionController.updateOption(OPTION_ID, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(updatedOption, response.getBody());

        verify(optionService).updateOption(OPTION_ID, request);
    }

    @Test
    void deleteOption_shouldReturnNoContent() {
        ResponseEntity<Void> response =
                optionController.deleteOption(OPTION_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(optionService).deleteOption(OPTION_ID);
    }
}
