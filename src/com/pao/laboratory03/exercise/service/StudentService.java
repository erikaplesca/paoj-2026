package com.pao.laboratory03.exercise.service;

import com.pao.laboratory03.exercise.exception.StudentNotFoundException;
import com.pao.laboratory03.exercise.model.Student;
import com.pao.laboratory03.exercise.model.Subject;

import java.util.*;

public class StudentService {

    private static StudentService instance;
    private final List<Student> students;

    private StudentService() {
        students = new ArrayList<>();
    }

    public static StudentService getInstance() {
        if (instance == null) {
            instance = new StudentService();
        }
        return instance;
    }

    // a) Adaugă student
    public void addStudent(String name, int age) {
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name)) {
                throw new RuntimeException("Studentul '" + name + "' există deja.");
            }
        }
        students.add(new Student(name, age));
    }

    // b) Caută după nume
    public Student findByName(String name) {
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name)) return s;
        }
        throw new StudentNotFoundException("Studentul '" + name + "' nu a fost găsit.");
    }

    // c) Adaugă notă
    public void addGrade(String studentName, Subject subject, double grade) {
        findByName(studentName).addGrade(subject, grade);
    }

    // d) Afișează toți
    public void printAllStudents() {
        if (students.isEmpty()) {
            System.out.println("Nu există studenți înregistrați.");
            return;
        }
        for (Student s : students) {
            System.out.println(s);
            for (Map.Entry<Subject, Double> e : s.getGrades().entrySet()) {
                System.out.printf("   %-5s → %.2f%n", e.getKey().name(), e.getValue());
            }
        }
    }

    // e) Top studenți după medie
    public void printTopStudents() {
        if (students.isEmpty()) {
            System.out.println("Nu există studenți înregistrați.");
            return;
        }
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort((a, b) -> Double.compare(b.getAverage(), a.getAverage()));

        System.out.println("=== Top Studenți ===");
        int rank = 1;
        for (Student s : sorted) {
            System.out.printf("%d. %s%n", rank++, s);
        }
    }

    // f) Media pe materie
    public Map<Subject, Double> getAveragePerSubject() {
        Map<Subject, List<Double>> temp = new HashMap<>();

        for (Student s : students) {
            for (Map.Entry<Subject, Double> e : s.getGrades().entrySet()) {
                temp.computeIfAbsent(e.getKey(), k -> new ArrayList<>()).add(e.getValue());
            }
        }

        Map<Subject, Double> result = new HashMap<>();
        for (Map.Entry<Subject, List<Double>> e : temp.entrySet()) {
            double avg = e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            result.put(e.getKey(), avg);
        }
        return result;
    }
}