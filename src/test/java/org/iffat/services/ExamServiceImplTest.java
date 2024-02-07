package org.iffat.services;

import org.iffat.models.Exam;
import org.iffat.repositories.ExamRepository;
import org.iffat.repositories.ExamRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExamServiceImplTest {

    @Test
    void findExamByName() {
        ExamRepository examRepository = mock(ExamRepository.class);
        ExamService examService = new ExamServiceImpl(examRepository);
        List<Exam> exams = Arrays.asList(
                new Exam(5L, "Mathematics"),
                new Exam(6L, "Language"),
                new Exam(7L, "History"));
        when(examRepository.findAll()).thenReturn(exams);
        Optional<Exam> exam = examService.findExamByName("Mathematics");
        assertTrue(exam.isPresent());
        assertEquals(5L,exam.get().getId());
        assertEquals("Mathematics",exam.get().getName());
    }

    @Test
    void findExamByNameEmptyList() {
        ExamRepository examRepository = mock(ExamRepository.class);
        ExamService examService = new ExamServiceImpl(examRepository);
        List<Exam> exams = Collections.emptyList();
        when(examRepository.findAll()).thenReturn(exams);
        Optional<Exam> exam = examService.findExamByName("Mathematics");
        assertFalse(exam.isPresent());
        assertTrue(exam.isEmpty());
    }
}