package org.iffat.repositories;

import org.iffat.models.Exam;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExamRepositoryImpl implements ExamRepository {
    @Override
    public List<Exam> findAll() {
        return Collections.emptyList();
                /*Arrays.asList(
                new Exam(5L,"Mathematics"),
                new Exam(6L, "Language"),
                new Exam(7L, "History")); */
    }
}
