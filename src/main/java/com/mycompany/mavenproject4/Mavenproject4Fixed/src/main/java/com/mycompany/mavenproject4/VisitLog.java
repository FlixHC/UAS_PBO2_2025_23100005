/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject4;

/**
 *
 * @author User
 */
public class VisitLog {
    private int id;
    private String studentName;
    private String studentId;
    private String studyProgram;
    private String purpose;
    private String visitTime;

    public VisitLog(int id, String studentName, String studentId, String studyProgram, String purpose, String visitTime) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studyProgram = studyProgram;
        this.purpose = purpose;
        this.visitTime = visitTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudyProgram() { return studyProgram; }
    public void setStudyProgram(String studyProgram) { this.studyProgram = studyProgram; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getVisitTime() { return visitTime; }
    public void setVisitTime(String visitTime) { this.visitTime = visitTime; }
}
