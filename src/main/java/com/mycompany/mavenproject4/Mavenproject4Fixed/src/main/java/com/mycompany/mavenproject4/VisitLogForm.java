/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject4;

/**
 *
 * @author User
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.*;

public class VisitLogForm extends JFrame {
    private JTextField tfStudentName = new JTextField();
    private JTextField tfStudentId = new JTextField();
    private JTextField tfStudyProgram = new JTextField();
    private JTextField tfPurpose = new JTextField();
    private JTextField tfVisitTime = new JTextField();
    private JTextArea outputArea = new JTextArea(10, 30);

    public VisitLogForm() {
        setTitle("GraphQL Visit Log Form");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        inputPanel.add(new JLabel("Student Name:"));
        inputPanel.add(tfStudentName);
        inputPanel.add(new JLabel("Student ID:"));
        inputPanel.add(tfStudentId);
        inputPanel.add(new JLabel("Study Program:"));
        inputPanel.add(tfStudyProgram);
        inputPanel.add(new JLabel("Purpose:"));
        inputPanel.add(tfPurpose);
        inputPanel.add(new JLabel("Visit Time:"));
        inputPanel.add(tfVisitTime);

        JButton btnAdd = new JButton("Add Visit Log");
        JButton btnFetch = new JButton("Show All");
        inputPanel.add(btnAdd);
        inputPanel.add(btnFetch);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> tambahVisitLog());
        btnFetch.addActionListener(e -> ambilSemuaVisitLog());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void tambahVisitLog() {
        try {
            String query = String.format(
                "mutation { addVisitLog(studentName: \"%s\", studentId: \"%s\", studyProgram: \"%s\", purpose: \"%s\", visitTime: \"%s\") { id studentName } }",
                tfStudentName.getText(),
                tfStudentId.getText(),
                tfStudyProgram.getText(),
                tfPurpose.getText(),
                tfVisitTime.getText()
            );
            String jsonRequest = new Gson().toJson(new GraphQLQuery(query));
            String response = sendGraphQLRequest(jsonRequest);
            outputArea.setText("Visit Log added!\n\n" + response);
        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private void ambilSemuaVisitLog() {
        try {
            String query = "query { allVisitLogs { id studentName studentId studyProgram purpose visitTime } }";
            String jsonRequest = new Gson().toJson(new GraphQLQuery(query));
            String response = sendGraphQLRequest(jsonRequest);

            JsonObject data = JsonParser.parseString(response).getAsJsonObject().getAsJsonObject("data");
            JsonArray arr = data.getAsJsonArray("allVisitLogs");

            List<VisitLog> logs = new ArrayList<>();
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                VisitLog v = new VisitLog(
                    obj.get("id").getAsInt(),
                    obj.get("studentName").getAsString(),
                    obj.get("studentId").getAsString(),
                    obj.get("studyProgram").getAsString(),
                    obj.get("purpose").getAsString(),
                    obj.get("visitTime").getAsString()
                );
                logs.add(v);
            }

            showVisitLogTable(logs);
        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private void showVisitLogTable(List<VisitLog> logs) {
        JFrame frame = new JFrame("Daftar Kunjungan");
        frame.setSize(900, 400);
        frame.setLocationRelativeTo(null);

        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "NIM", "Prodi", "Keperluan", "Waktu"}, 0);
        JTable table = new JTable(tableModel);
        for (VisitLog v : logs) {
            tableModel.addRow(new Object[]{
                v.getId(), v.getStudentName(), v.getStudentId(),
                v.getStudyProgram(), v.getPurpose(), v.getVisitTime()
            });
        }

        JTextField nameField = new JTextField(10);
        JTextField idField = new JTextField(10);
        JTextField programField = new JTextField(10);
        JTextField purposeField = new JTextField(10);
        JTextField timeField = new JTextField(10);

        JPanel formPanel = new JPanel();
        formPanel.add(new JLabel("Nama:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("NIM:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Prodi:"));
        formPanel.add(programField);
        formPanel.add(new JLabel("Keperluan:"));
        formPanel.add(purposeField);
        formPanel.add(new JLabel("Waktu:"));
        formPanel.add(timeField);

        JButton addBtn = new JButton("Tambah");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Hapus");

        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        table.getSelectionModel().addListSelectionListener(e -> {
            int i = table.getSelectedRow();
            if (i != -1) {
                nameField.setText(table.getValueAt(i, 1).toString());
                idField.setText(table.getValueAt(i, 2).toString());
                programField.setText(table.getValueAt(i, 3).toString());
                purposeField.setText(table.getValueAt(i, 4).toString());
                timeField.setText(table.getValueAt(i, 5).toString());
            }
        });

        addBtn.addActionListener(e -> {
            try {
                String query = String.format(
                    "mutation { addVisitLog(studentName: \"%s\", studentId: \"%s\", studyProgram: \"%s\", purpose: \"%s\", visitTime: \"%s\") { id } }",
                    nameField.getText(), idField.getText(), programField.getText(), purposeField.getText(), timeField.getText()
                );
                String json = new Gson().toJson(new GraphQLQuery(query));
                String response = sendGraphQLRequest(json);
                outputArea.setText("Ditambahkan:\n" + response);
                frame.dispose();
                ambilSemuaVisitLog();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                try {
                    String id = table.getValueAt(row, 0).toString();
                    String query = String.format(
                        "mutation { updateVisitLog(id: %s, studentName: \"%s\", studentId: \"%s\", studyProgram: \"%s\", purpose: \"%s\", visitTime: \"%s\") { id } }",
                        id, nameField.getText(), idField.getText(), programField.getText(), purposeField.getText(), timeField.getText()
                    );
                    String json = new Gson().toJson(new GraphQLQuery(query));
                    String response = sendGraphQLRequest(json);
                    outputArea.setText("Diedit:\n" + response);
                    frame.dispose();
                    ambilSemuaVisitLog();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                try {
                    String id = table.getValueAt(row, 0).toString();
                    String query = String.format("mutation { deleteVisitLog(id: %s) }", id);
                    String json = new Gson().toJson(new GraphQLQuery(query));
                    String response = sendGraphQLRequest(json);
                    outputArea.setText("Dihapus!\n" + response);
                    frame.dispose();
                    ambilSemuaVisitLog();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.add(formPanel, BorderLayout.NORTH);
        frame.add(btnPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private String sendGraphQLRequest(String json) throws Exception {
        URL url = new URL("http://localhost:4567/graphql");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes());
        }
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append("\n");
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VisitLogForm::new);
    }

    class GraphQLQuery {
        String query;
        GraphQLQuery(String query) {
            this.query = query;
        }
    }

}
