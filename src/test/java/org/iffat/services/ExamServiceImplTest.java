package org.iffat.services;

import org.iffat.models.Exam;
import org.iffat.repositories.ExamRepository;
import org.iffat.repositories.ExamRepositoryImpl;
import org.iffat.repositories.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExamServiceImplTest {

    ExamRepository examRepository;
    QuestionRepository questionRepository;
    ExamService examService;

    @BeforeEach
    void setUp() {
        examRepository = mock(ExamRepository.class);
        questionRepository = mock(QuestionRepository.class);
        examService = new ExamServiceImpl(examRepository, questionRepository);
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
    void testQuestionsExam() {
        when(examRepository.findAll()).thenReturn(Data.EXAMS);
        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        Exam exam = examService.findExamByNameWithQuestions("Mathematics");
        assertEquals(5, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Arithmetic"));
    }
}