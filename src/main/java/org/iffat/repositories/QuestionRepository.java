package org.iffat.repositories;

import org.iffat.models.Exam;

import java.util.List;

public interface QuestionRepository {
    List<String> findQuestionsByExamId(Long id);
    Exam saveVaried(List<String> questions);
}
