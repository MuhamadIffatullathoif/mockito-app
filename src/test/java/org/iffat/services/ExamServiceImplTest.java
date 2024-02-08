package org.iffat.services;

import org.iffat.models.Exam;
import org.iffat.repositories.ExamRepository;
import org.iffat.repositories.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {

    @Mock
    ExamRepository examRepository;
    @Mock
    QuestionRepository questionRepository;
    @InjectMocks
    ExamServiceImpl examService;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this);
        // examRepository = mock(ExamRepository.class);
        // questionRepository = mock(QuestionRepository.class);
        // examService = new ExamServiceImpl(examRepository, questionRepository);
    }

    @Test
    void findExamByName() {
        when(examRepository.findAll()).thenReturn(Data.EXAMS);
        Optional<Exam> exam = examService.findExamByName("Mathematics");

        assertTrue(exam.isPresent());
        assertEquals(5L, exam.get().getId());
        assertEquals("Mathematics", exam.get().getName());
    }

    @Test
    void findExamByNameEmptyList() {
        // given
        List<Exam> exams = Collections.emptyList();
        when(examRepository.findAll()).thenReturn(exams);

        // when
        Optional<Exam> exam = examService.findExamByName("Mathematics");

        // then
        assertFalse(exam.isPresent());
        assertTrue(exam.isEmpty());
    }

    @Test
    void testQuestionsExamVerify() {
        // given
        when(examRepository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        // when
        Exam exam = examService.findExamByNameWithQuestions("Mathematics");

        // then
        assertEquals(5, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Arithmetic"));
        verify(examRepository).findAll();
        verify(questionRepository).findQuestionsByExamId(5L);
    }

    @Test
    void testNoExistsExamVerify() {
        // given
        when(examRepository.findAll()).thenReturn(Collections.emptyList());
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        // when
        Exam exam = examService.findExamByNameWithQuestions("Mathematics");

        // then
        assertNull(exam);
        verify(examRepository).findAll();
        verify(questionRepository).findQuestionsByExamId(anyLong());
    }

    @Test
    void testSaveExam() {
        // given
        Exam newExam = Data.EXAM;
        newExam.setQuestions(Data.QUESTIONS);
        when(examRepository.save(any(Exam.class))).thenReturn(Data.EXAM);

        // when
        Exam exam = examService.save(newExam);

        // then
        assertNotNull(exam.getId());
        assertEquals(8L, exam.getId());
        assertEquals("Physics", exam.getName());
        verify(examRepository).save(any(Exam.class));
        verify(questionRepository).saveVaried(anyList());
    }

    @Test
    void testSaveExamIncrementalID() {
        // given
        Exam newExam = Data.EXAM;
        newExam.setQuestions(Data.QUESTIONS);
        when(examRepository.save(any(Exam.class))).then(new Answer<Exam>() {
            Long sequence = 8L;

            @Override
            public Exam answer(InvocationOnMock invocation) throws Throwable {
                Exam exam = invocation.getArgument(0);
                exam.setId(sequence++);
                return exam;
            }
        });

        // when
        Exam exam = examService.save(newExam);

        // then
        assertNotNull(exam.getId());
        assertEquals(8L, exam.getId());
        assertEquals("Physics", exam.getName());
        verify(examRepository).save(any(Exam.class));
        verify(questionRepository).saveVaried(anyList());
    }

    @Test
    void testHandlingException() {
        // given
        when(examRepository.findAll()).thenReturn(Data.EXAMS_ID_NULL);
        when(questionRepository.findQuestionsByExamId(isNull())).thenThrow(IllegalArgumentException.class);

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            examService.findExamByNameWithQuestions("Mathematics");
        });

        // then
        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(examRepository).findAll();
        verify(questionRepository).findQuestionsByExamId(isNull());
    }

    @Test
    void testArgumentMatchers() {
        when(examRepository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        examService.findExamByNameWithQuestions("Mathematics");

        verify(examRepository).findAll();
//        verify(questionRepository).findQuestionsByExamId(ArgumentMatchers.argThat(arg -> arg != null && arg.equals(5L)));
        verify(questionRepository).findQuestionsByExamId(ArgumentMatchers.argThat(arg -> arg != null && arg >= 5L));
//        verify(questionRepository).findQuestionsByExamId(ArgumentMatchers.eq(5L));
    }
}