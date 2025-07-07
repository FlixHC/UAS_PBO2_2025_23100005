/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mavenproject4;

/**
 *
 * @author ASUS
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import com.google.gson.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import java.util.List;
import java.util.ArrayList;


public class Mavenproject4 extends JFrame {

    private JTable visitTable;
    private DefaultTableModel tableModel;
    private JTextField timeField;
    private JTextField nameField;
    private JTextField nimField;
    private JComboBox<String> studyProgramBox;
    private JComboBox<String> purposeBox;
    private JButton addButton;
    private JButton clearButton;
    
    private boolean actionColumnsAdded = false;

    public Mavenproject4() {
        setTitle("Library Visit Log");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        timeField = new JTextField();
        nameField = new JTextField();
        nimField = new JTextField();
        studyProgramBox = new JComboBox<>(new String[] {"Sistem dan Teknologi Informasi", "Bisnis Digital", "Kewirausahaan"});
        purposeBox = new JComboBox<>(new String[] {"Membaca", "Meminjam/Mengembalikan Buku", "Research", "Belajar"});
        addButton = new JButton("Add");
        clearButton = new JButton("Clear");

        inputPanel.setBorder(BorderFactory.createTitledBorder("Visit Entry Form"));
        inputPanel.add(new JLabel("Waktu Kunjungan"));
        inputPanel.add(timeField);
        inputPanel.add(new JLabel("NIM:"));
        inputPanel.add(nimField);
        inputPanel.add(new JLabel("Name Mahasiswa:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Program Studi:"));
        inputPanel.add(studyProgramBox);
        inputPanel.add(new JLabel("Tujuan Kunjungan:"));
        inputPanel.add(purposeBox);
        inputPanel.add(addButton);
        inputPanel.add(clearButton);

        add(inputPanel, BorderLayout.NORTH);

        String[] columns = {"Waktu Kunjungan", "NIM", "Nama", "Program Studi", "Tujuan Kunjungan"};
        tableModel = new DefaultTableModel(columns, 0);
        visitTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(visitTable);
        add(scrollPane, BorderLayout.CENTER);
        
        
        addButton.addActionListener(e -> {
        try {
            String visitTime = timeField.getText();
            String studentName = nameField.getText();
            String studentId = nimField.getText();
            String studyProgram = studyProgramBox.getSelectedItem().toString();
            String purpose = purposeBox.getSelectedItem().toString();
            
            

            String query = String.format("mutation { addVisit(studentName: \"%s\", studentId: \"%s\", studyProgram: \"%s\", purpose: \"%s\", visitTime: \"%s\") { id name } }",
                visitTime, studentName, studentId, studyProgram, purpose);
            String json = new Gson().toJson(new GraphQLQuery(query));
            String response = sendGraphQLRequest(json);
        } catch (Exception ex) {
        }
    });
        
        

        
        setVisible(true);
        
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("control G"), "showActions");

        getRootPane().getActionMap().put("showActions", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!actionColumnsAdded) {
                    addActionColumns();
                    actionColumnsAdded = true;
                }
            }
        });
    }
    
    private void addVisit() {
        try {
            String query = String.format(
            "mutation { addVisit(studentName: \"%s\", studentId: \"%s\", studyProgram: \"%s\", purpose: \"%s\", visitTime: \"%s\") { id name } }",
            timeField.getText(),
            nameField.getText(),
            nimField.getText(),
            studyProgramBox.getSelectedItem().toString(),
            purposeBox.getSelectedItem().toString()
            );
        String jsonRequest = new Gson().toJson(new GraphQLQuery(query));
        String response = sendGraphQLRequest(jsonRequest);
        } catch (Exception e) {
        }
    }
    
    private void getVisit() {
    try {
        String query = "query { allVisit { id studentName studentId studyProgram purpose } }";
        String jsonRequest = new Gson().toJson(new GraphQLQuery(query));
        String response = sendGraphQLRequest(jsonRequest);

        JsonObject data = JsonParser.parseString(response).getAsJsonObject().getAsJsonObject("data");
        JsonArray arr = data.getAsJsonArray("allVisit");

        List<VisitLog> visits = new ArrayList<>();
        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            VisitLog p = new VisitLog(
                obj.get("id").getAsInt(),
                obj.get("studentName").getAsString(),
                obj.get("studentId").getAsString(),
                obj.get("studyProgram").getAsString(),
                obj.get("purpose").getAsString(),
                obj.get("visitTime").getAsString()
            );
            visits.add(p);
        }
    } catch (Exception e) {
    }
}
    
    
    
    private void addActionColumns() {
        tableModel.addColumn("Action");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt("Action", i, tableModel.getColumnCount() - 2);
        }

        visitTable.getColumn("Action").setCellRenderer(new ButtonRenderer());

        visitTable.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox()));
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
        SwingUtilities.invokeLater(Mavenproject4::new);
    }
    
    class GraphQLQuery {
        String query;
        GraphQLQuery(String query) {
        this.query = query;
        }
    }
}
