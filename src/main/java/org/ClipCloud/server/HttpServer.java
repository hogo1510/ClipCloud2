package org.ClipCloud.server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.InetSocketAddress;
import org.ClipCloud.Save2File;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class HttpServer {
    public static void main(String[] args)
    {
        try {
            // Create an HttpServer instance
            com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(8000), 0);

            // Create a context for a specific path and set the handler
            server.createContext("/", new TestHandler());
            server.createContext("/post", new PostHandler());
            server.createContext("/get", new GetHandler());

            // Start the server
            server.setExecutor(null); // Use the default executor
            server.start();

            System.out.println("Server is running on port http://localhost:8000");
        } catch (IOException e) {
            System.out.println("Error starting the server: " + e.getMessage());
        }
    }

    // Define a custom HttpHandler
    static class TestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Handle the request
            String response = "Hello, this is a test";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    static class PostHandler implements HttpHandler {
        private static final Gson gson = new Gson();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    // Lees de binnenkomende data
                    String requestData = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("Ontvangen data: " + requestData);

                    // Maak een nieuw bericht object
                    JsonObject newMessage = new JsonObject();
                    newMessage.addProperty("content", requestData);
                    newMessage.addProperty("timestamp", System.currentTimeMillis() / 1000);

                    // Lees bestaande berichten of maak nieuwe array
                    JsonArray messages;
                    String existingData = getData("test");
                    if (existingData == null || existingData.trim().isEmpty()) {
                        messages = new JsonArray();
                    } else {
                        try {
                            messages = gson.fromJson(existingData, JsonArray.class);
                        } catch (JsonSyntaxException e) {
                            System.out.println("Ongeldige JSON, nieuwe array aangemaakt");
                            messages = new JsonArray();
                        }
                    }

                    // Voeg nieuw bericht toe
                    messages.add(newMessage);

                    // Sla op als JSON
                    String jsonToSave = gson.toJson(messages);
                    Save2File.save(jsonToSave);

                    // Stuur response terug naar PHP
                    String response = "{\"status\":\"success\",\"message\":\"Bericht ontvangen\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }

                } catch (Exception e) {
                    String error = "{\"error\":\"" + e.getMessage() + "\"}";
                    exchange.sendResponseHeaders(500, error.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(error.getBytes());
                    }
                }
            } else {
                exchange.sendResponseHeaders(405, -1); // Method not allowed
            }
        }
    }

    static String FilePath = "src/main/java/org/ClipCloud/berichten/";

    public static String getData(String fileName) {
        StringBuilder data = new StringBuilder();
        try {
            File myObj = new File(FilePath+fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data.append(myReader.nextLine()).append("\n"); // Voeg elke regel toe aan de StringBuilder
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return null; // Retourneer null als er een fout optreedt
        }
        return data.toString(); // Retourneer de gelezen data als string
    }


    static class GetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // 1. Lees bestand
                String fileName = Save2File.getFilename();
                String fileContent = getData(fileName);

                // 2. Valideer en corrigeer content
                if (fileContent == null || fileContent.trim().isEmpty()) {
                    fileContent = "[]"; // Standaard lege array
                } else {
                    // Verwijder mogelijk BOM of extra karakters
                    fileContent = fileContent.trim();

                    // Zorg dat het met [ begint en eindigt met ]
                    if (!fileContent.startsWith("[")) {
                        fileContent = "[" + fileContent + "]";
                    }
                }

                // 3. Valideer JSON
                try {
                    JsonParser.parseString(fileContent);
                } catch (JsonSyntaxException e) {
                    System.err.println("Ongeldige JSON gevonden, reset naar lege array");
                    fileContent = "[]";
                }

                // 4. Stuur response
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

                byte[] responseBytes = fileContent.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, responseBytes.length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(responseBytes);
                }

            } catch (Exception e) {
                String error = "{\"error\":\"Server error\"}";
                exchange.sendResponseHeaders(500, error.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(error.getBytes());
                }
            }
        }
    }
}
