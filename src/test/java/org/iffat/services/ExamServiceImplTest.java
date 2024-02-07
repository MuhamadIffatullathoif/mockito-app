package org.iffat.services;

import org.iffat.models.Exam;
import org.iffat.repositories.ExamRepository;
import org.iffat.repositories.ExamRepositoryImpl;
import org.iffat.repositories.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
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
        List<Exam> exams = Collections.emptyList();
        when(examRepository.findAll()).thenReturn(exams);
        Optional<Exam> exam = examService.findExamByName("Mathematics");
        assertFalse(exam.isPresent());
        assertTrue(exam.isEmpty());
    }

    @Test
    void testQuestionsExamVerify() {
        when(examRepository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        Exam exam = examService.findExamByNameWithQuestions("Mathematics");
        assertEquals(5, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Arithmetic"));
        verify(examRepository).findAll();
        verify(questionRepository).findQuestionsByExamId(5L);
    }

    @Test
    void testNoExistsExamVerify() {
        when(examRepository.findAll()).thenReturn(Collections.emptyList());
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        Exam exam = examService.findExamByNameWithQuestions("Mathematics");
        assertNull(exam);
        verify(examRepository).findAll();
        verify(questionRepository).findQuestionsByExamId(anyLong());
    }

    @Test
    void testSaveExam() {
        Exam newExam = Data.EXAM;
        newExam.setQuestions(Data.QUESTIONS);

        when(examRepository.save(any(Exam.class))).thenReturn(Data.EXAM);
        Exam exam = examService.save(newExam);

        assertNotNull(exam.getId());
        assertEquals(8L, exam.getId());
        assertEquals("Physics", exam.getName());

        verify(examRepository).save(any(Exam.class));
        verify(questionRepository).saveVaried(anyList());
    }
}