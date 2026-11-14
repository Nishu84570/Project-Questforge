
package questforge.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import questforge.dto.OptionRequestDTO;
import questforge.dto.OptionResponseDTO;
import questforge.exception.ResourceNotFoundException;
import questforge.model.Option;
import questforge.repository.OptionRepository;
import questforge.repository.QuestionRepository;
import questforge.service.OptionService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OptionServiceTest {

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private OptionService optionService;

    private OptionRequestDTO request;
    private Option option;

    @BeforeEach
    void setUp() {

        request = new OptionRequestDTO();
        request.setQuestionId(1L);
        request.setOptionText("  Java Programming  ");
        request.setIsCorrect(true);

        option = new Option();
        option.setId(10L);
        option.setQuestionId(1L);
        option.setOptionText("Java Programming");
        option.setIsCorrect(true);
    }

    
    @Test
    void createOption_Success() {

        when(questionRepository.existsById(1L))
                .thenReturn(true);

        when(optionRepository.save(any(Option.class)))
                .thenReturn(option);

        OptionResponseDTO result =
                optionService.createOption(request);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(1L, result.getQuestionId());
        assertEquals("Java Programming", result.getOptionText());
        assertEquals(true, result.getIsCorrect());

        verify(questionRepository).existsById(1L);
        verify(optionRepository).save(any(Option.class));
    }

    @Test
    void createOption_QuestionNotFound() {

        when(questionRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> optionService.createOption(request)
        );

        verify(questionRepository).existsById(1L);
        verify(optionRepository, never()).save(any(Option.class));
    }

    @Test
    void getAllOptions_Success() {

        Option option2 = new Option();
        option2.setId(11L);
        option2.setQuestionId(1L);
        option2.setOptionText("Python Programming");
        option2.setIsCorrect(false);

        when(optionRepository.findAll())
                .thenReturn(List.of(option, option2));

        List<OptionResponseDTO> result =
                optionService.getAllOptions();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(10L, result.get(0).getId());
        assertEquals("Java Programming", result.get(0).getOptionText());

        assertEquals(11L, result.get(1).getId());
        assertEquals("Python Programming", result.get(1).getOptionText());

        verify(optionRepository).findAll();
    }


    @Test
    void getAllOptions_EmptyList() {

        when(optionRepository.findAll())
                .thenReturn(List.of());

        List<OptionResponseDTO> result =
                optionService.getAllOptions();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(optionRepository).findAll();
    }

    @Test
    void getOptionById_Success() {

        when(optionRepository.findById(10L))
                .thenReturn(Optional.of(option));

        OptionResponseDTO result =
                optionService.getOptionById(10L);

        assertNotNull(result);

        assertEquals(10L, result.getId());
        assertEquals(1L, result.getQuestionId());
        assertEquals("Java Programming", result.getOptionText());
        assertEquals(true, result.getIsCorrect());

        verify(optionRepository).findById(10L);
    }

 
    @Test
    void getOptionById_NotFound() {

        when(optionRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> optionService.getOptionById(10L)
        );

        verify(optionRepository).findById(10L);
    }


    @Test
    void getOptionsByQuestion_Success() {

        when(questionRepository.existsById(1L))
                .thenReturn(true);

        when(optionRepository.findByQuestionId(1L))
                .thenReturn(List.of(option));

        List<OptionResponseDTO> result =
                optionService.getOptionsByQuestion(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(10L, result.get(0).getId());
        assertEquals("Java Programming", result.get(0).getOptionText());

        verify(questionRepository).existsById(1L);
        verify(optionRepository).findByQuestionId(1L);
    }

    
    @Test
    void getOptionsByQuestion_QuestionNotFound() {

        when(questionRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> optionService.getOptionsByQuestion(1L)
        );

        verify(questionRepository).existsById(1L);

        verify(optionRepository, never())
                .findByQuestionId(anyLong());
    }


    @Test
    void updateOption_Success() {

        OptionRequestDTO updateRequest =
                new OptionRequestDTO();

        updateRequest.setQuestionId(2L);
        updateRequest.setOptionText("  Spring Boot  ");
        updateRequest.setIsCorrect(false);

        when(questionRepository.existsById(2L))
                .thenReturn(true);

        when(optionRepository.findById(10L))
                .thenReturn(Optional.of(option));

        when(optionRepository.save(any(Option.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OptionResponseDTO result =
                optionService.updateOption(10L, updateRequest);

        assertNotNull(result);

        assertEquals(10L, result.getId());
        assertEquals(2L, result.getQuestionId());
        assertEquals("Spring Boot", result.getOptionText());
        assertEquals(false, result.getIsCorrect());

        verify(questionRepository).existsById(2L);
        verify(optionRepository).findById(10L);
        verify(optionRepository).save(option);
    }


    @Test
    void updateOption_QuestionNotFound() {

        when(questionRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> optionService.updateOption(10L, request)
        );

        verify(questionRepository).existsById(1L);

        verify(optionRepository, never()).findById(anyLong());
        verify(optionRepository, never()).save(any(Option.class));
    }

  
    @Test
    void updateOption_OptionNotFound() {

        when(questionRepository.existsById(1L))
                .thenReturn(true);

        when(optionRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> optionService.updateOption(10L, request)
        );

        verify(questionRepository).existsById(1L);
        verify(optionRepository).findById(10L);

        verify(optionRepository, never()).save(any(Option.class));
    }

 
    @Test
    void deleteOption_Success() {

        when(optionRepository.findById(10L))
                .thenReturn(Optional.of(option));

        assertDoesNotThrow(
                () -> optionService.deleteOption(10L)
        );

        verify(optionRepository).findById(10L);
        verify(optionRepository).delete(option);
    }

    @Test
    void deleteOption_NotFound() {

        when(optionRepository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> optionService.deleteOption(10L)
        );

        verify(optionRepository).findById(10L);

        verify(optionRepository, never())
                .delete(any(Option.class));
    }


    @Test
    void createOption_NullQuestionId() {

        request.setQuestionId(null);

        assertThrows(
                ResourceNotFoundException.class,
                () -> optionService.createOption(request)
        );

        verifyNoInteractions(optionRepository);

        verify(questionRepository, never())
                .existsById(anyLong());
    }
}
