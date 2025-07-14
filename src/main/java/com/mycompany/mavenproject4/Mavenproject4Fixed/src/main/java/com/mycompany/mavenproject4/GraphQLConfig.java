/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mavenproject4;

/**
 *
 * @author User
 */
import graphql.*;
import graphql.schema.idl.*;
import java.io.*;
import java.util.Objects;
import graphql.schema.GraphQLSchema;

public class GraphQLConfig {
    public static GraphQL init() throws IOException {
        InputStream schemaStream = GraphQLConfig.class.getClassLoader().getResourceAsStream("schema.graphqls");

        if (schemaStream == null) {
            throw new RuntimeException("schema.graphqls not found in classpath.");
        }

        String schema = new String(Objects.requireNonNull(schemaStream).readAllBytes());

        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schema);
        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
            .type("Query", builder -> builder
                .dataFetcher("allVisitLogs", env -> VisitLogRepository.findAll())
                .dataFetcher("visitLogById", env -> {
                    int id = env.getArgument("id");
                    return VisitLogRepository.findById(id);
                })
            )
            .type("Mutation", builder -> builder
                .dataFetcher("addVisitLog", env -> VisitLogRepository.add(
                    env.getArgument("studentName"),
                    env.getArgument("studentId"),
                    env.getArgument("studyProgram"),
                    env.getArgument("purpose"),
                    env.getArgument("visitTime")
                ))
                .dataFetcher("deleteVisitLog", env -> {
                    int id = Integer.parseInt(env.getArgument("id").toString());
                    return VisitLogRepository.delete(id);
                })
                .dataFetcher("updateVisitLog", env -> {
                    int id = Integer.parseInt(env.getArgument("id").toString());
                    String studentName = env.getArgument("studentName");
                    String studentId = env.getArgument("studentId");
                    String studyProgram = env.getArgument("studyProgram");
                    String purpose = env.getArgument("purpose");
                    String visitTime = env.getArgument("visitTime");

                    VisitLog visitLog = VisitLogRepository.findById(id);
                    if (visitLog == null) return null;

                    visitLog.setStudentName(studentName);
                    visitLog.setStudentId(studentId);
                    visitLog.setStudyProgram(studyProgram);
                    visitLog.setPurpose(purpose);
                    visitLog.setVisitTime(visitTime);

                    return visitLog;
                })
            )
            .build();

        GraphQLSchema schemaFinal = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
        return GraphQL.newGraphQL(schemaFinal).build();
    }
}