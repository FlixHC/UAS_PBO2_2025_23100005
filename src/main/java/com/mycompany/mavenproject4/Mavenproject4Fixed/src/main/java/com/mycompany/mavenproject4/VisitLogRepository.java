/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject4;

/**
 *
 * @author User
 */
import java.util.ArrayList;
import java.util.List;

public class VisitLogRepository {
    private static List<VisitLog> visitLogList = new ArrayList<>();
    private static int idCounter = 1;

    public static VisitLog add(String studentName, String studentId, String studyProgram, String purpose, String visitTime) {
        VisitLog visitLog = new VisitLog(idCounter++, studentName, studentId, studyProgram, purpose, visitTime);
        visitLogList.add(visitLog);
        return visitLog;
    }

    public static List<VisitLog> findAll() {
        return visitLogList;
    }

    public static VisitLog findById(int id) {
        return visitLogList.stream()
                .filter(v -> v.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public static boolean delete(int id) {
        return visitLogList.removeIf(v -> v.getId() == id);
    }
}

