package org.iffat.repositories;

import org.iffat.models.Data;
import org.iffat.models.Exam;

import java.util.List;

public class QuestionRepositoryImpl implements QuestionRepository {
    @Override
    public List<String> findQuestionsByExamId(Long id) {
        return Data.QUESTIONS;
    }

    @Override
    public Exam saveVaried(List<String> questions) {
        return Data.EXAM;
    }
}
