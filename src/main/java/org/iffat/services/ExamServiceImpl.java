package org.iffat.services;

import org.iffat.models.Exam;
import org.iffat.repositories.ExamRepository;

import java.util.Optional;

public class ExamServiceImpl implements ExamService {

    private ExamRepository examRepository;

    public ExamServiceImpl(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    public Optional<Exam> findExamByName(String name) {
        return examRepository.findAll()
                .stream()
                .filter(exam -> exam.getName().contains(name))
                .findFirst();
    }
}
