/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mavenproject4;

/**
 *
 * @author User
 */
import static spark.Spark.*;
import com.google.gson.*;
import graphql.*;

public class Mavenproject4 {

   public static void main(String[] args) throws Exception {
       GraphQL graphql = GraphQLConfig.init();
       Gson gson = new Gson();

       port(4567);
       post("/graphql", (req, res) -> {
           res.type("application/json");

           JsonObject request = gson.fromJson(req.body(), JsonObject.class);
           String query = request.get("query").getAsString();

           ExecutionResult result = graphql.execute(query);
           return gson.toJson(result.toSpecification());
       });
   }
}
