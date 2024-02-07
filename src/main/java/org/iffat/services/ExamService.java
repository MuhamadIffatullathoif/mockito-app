package org.iffat.services;

import org.iffat.models.Exam;

import java.util.Optional;

public interface ExamService {
    Optional<Exam> findExamByName(String name);
    Exam findExamByNameWithQuestions(String name);
    Exam save(Exam exam);
}
