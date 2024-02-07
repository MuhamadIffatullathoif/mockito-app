package org.iffat.services;

import org.iffat.models.Exam;

public interface ExamService {
    Exam findExamByName(String name);
}
