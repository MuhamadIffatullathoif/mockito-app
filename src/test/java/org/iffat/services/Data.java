package org.iffat.services;

import org.iffat.models.Exam;

import java.util.Arrays;
import java.util.List;

public class Data {
    public final static List<Exam> EXAMS = Arrays.asList(
            new Exam(5L, "Mathematics"),
            new Exam(6L, "Language"),
            new Exam(7L, "History"));

    public final static List<Exam> EXAMS_ID_NULL = Arrays.asList(
            new Exam(null, "Mathematics"),
            new Exam(null, "Language"),
            new Exam(null, "History"));

    public final static List<String> QUESTIONS = Arrays.asList(
            "Arithmetic", "Integrals", "derivatives", "trigonometric", "geometric"
    );

    public final static Exam EXAM = new Exam(8L,"Physics");
}
