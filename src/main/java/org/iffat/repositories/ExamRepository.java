package org.iffat.repositories;

import org.iffat.models.Exam;

import java.util.List;

public interface ExamRepository {
    List<Exam> findAll();
}
