package org.iffat.services;

import org.iffat.models.Exam;
import org.iffat.repositories.ExamRepository;
import org.iffat.repositories.ExamRepositoryImpl;
import org.iffat.repositories.QuestionRepository;
import org.iffat.repositories.QuestionRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {

    // mock impl because use doCallRealMethod can't interface
    @Mock
    ExamRepositoryImpl examRepository;

    // spy must be implemented can't interface
//    @Spy
//    ExamRepositoryImpl examRepository;
    @Mock
    QuestionRepositoryImpl questionRepository;
//    @Spy
//    QuestionRepositoryImpl questionRepository;

    // spy and mock still use inject mock
    @InjectMocks
    ExamServiceImpl examService;

    @Captor
    ArgumentCaptor<Long> captor;

    @BeforeEach
    void setUp() {
        // MockitoAnnotations.openMocks(this);
        // examRepository = mock(ExamRepositoryImpl.class);
        // questionRepository = mock(QuestionRepositoryImpl.class);
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

    @Test
    void testArgumentMatchersCustom() {
        when(examRepository.findAll()).thenReturn(Data.EXAMS_ID_NEGATIVE);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);

        examService.findExamByNameWithQuestions("Mathematics");

        verify(examRepository).findAll();
        verify(questionRepository).findQuestionsByExamId(ArgumentMatchers.argThat(new MyArgsMatchers()));
    }

    public static class MyArgsMatchers implements ArgumentMatcher<Long> {

        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return "is for a custom error message that mockito prints in case the test fails " +
                    +argument + " must be a positive integer";
        }
    }

    @Test
    void testArgumentCaptor() {
        when(examRepository.findAll()).thenReturn(Data.EXAMS);
        // when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        examService.findExamByNameWithQuestions("Mathematics");

        // ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(questionRepository).findQuestionsByExamId(captor.capture());

        assertEquals(5L, captor.getValue());
    }

    @Test
    void testDoThrow() {
        Exam exam = Data.EXAM;
        exam.setQuestions(Data.QUESTIONS);
        doThrow(IllegalArgumentException.class).when(questionRepository).saveVaried(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
            examService.save(exam);
        });
    }

    @Test
    void testDoAnswer() {
        when(examRepository.findAll()).thenReturn(Data.EXAMS);
        // when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5L ? Data.QUESTIONS : null;
        }).when(questionRepository).findQuestionsByExamId(anyLong());

        Exam exam = examService.findExamByNameWithQuestions("Mathematics");
        assertEquals(5L, exam.getId());
        assertEquals(5, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("geometric"));
        assertEquals("Mathematics", exam.getName());

        verify(questionRepository).findQuestionsByExamId(anyLong());
    }

    @Test
    void testDoAnswerSaveExamIncrementalID() {
        // given
        Exam newExam = Data.EXAM;
        newExam.setQuestions(Data.QUESTIONS);

        doAnswer(new Answer<Exam>() {
            Long sequence = 8L;

            @Override
            public Exam answer(InvocationOnMock invocation) throws Throwable {
                Exam exam = invocation.getArgument(0);
                exam.setId(sequence++);
                return exam;
            }
        }).when(examRepository).save(any(Exam.class));

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
    void testDoCallRealMethod() {
        when(examRepository.findAll()).thenReturn(Data.EXAMS);
        // when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        doCallRealMethod().when(questionRepository).findQuestionsByExamId(anyLong());
        Exam exam = examService.findExamByNameWithQuestions("Mathematics");

        verify(examRepository).findAll();
        verify(questionRepository).findQuestionsByExamId(anyLong());
    }

    @Test
    void testSpy() {
        ExamRepository examRepository = spy(ExamRepositoryImpl.class);
        QuestionRepository questionRepository = spy(QuestionRepositoryImpl.class);
        ExamService examService = new ExamServiceImpl(examRepository, questionRepository);

        List<String> questions = Arrays.asList("geometric");
        // spy and mock differ will call real method, when have return mock will call real method with mock data
        // when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(questions);
        doReturn(questions).when(questionRepository).findQuestionsByExamId(anyLong());

        Exam exam = examService.findExamByNameWithQuestions("Mathematics");
        assertEquals(5L, exam.getId());
        assertEquals(1, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("geometric"));
        assertEquals("Mathematics", exam.getName());
    }

    @Test
    void testOrderInvocations() {
        when(examRepository.findAll()).thenReturn(Data.EXAMS);

        examService.findExamByNameWithQuestions("Mathematics");
        examService.findExamByNameWithQuestions("Language");

        InOrder inOrder = inOrder(examRepository, questionRepository);
        inOrder.verify(examRepository).findAll();
        inOrder.verify(questionRepository).findQuestionsByExamId(5L);

        inOrder.verify(examRepository).findAll();
        inOrder.verify(questionRepository).findQuestionsByExamId(6L);
    }

    @Test
    void testNumberInvocations() {
        when(examRepository.findAll()).thenReturn(Data.EXAMS);
        examService.findExamByNameWithQuestions("Mathematics");

        verify(questionRepository).findQuestionsByExamId(5L);
        verify(questionRepository, times(1)).findQuestionsByExamId(5L);
        verify(questionRepository, atLeast(1)).findQuestionsByExamId(5L);
        verify(questionRepository, atLeastOnce()).findQuestionsByExamId(5L);
        verify(questionRepository, atMost(1)).findQuestionsByExamId(5L);
        verify(questionRepository, atMostOnce()).findQuestionsByExamId(5L);
    }

    @Test
    void testNumberInvocation() {
        when(examRepository.findAll()).thenReturn(Collections.emptyList());
        examService.findExamByNameWithQuestions("Mathematics");

        verify(questionRepository, never()).findQuestionsByExamId(5L);
        verifyNoInteractions(questionRepository);

        verify(examRepository).findAll();
        verify(examRepository, times(1)).findAll();
    }
}