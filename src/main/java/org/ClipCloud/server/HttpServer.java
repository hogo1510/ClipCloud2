package org.ClipCloud.server;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
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
    static class PostHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException{
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                // Lees de request body (data van PHP)
                InputStream requestBody = exchange.getRequestBody();
                String requestData = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);

                System.out.println("Ontvangen van PHP: " + requestData); // Log naar console

                // Stuur een response terug
                String response = "het is ontvangen! \n Link: ";
                exchange.sendResponseHeaders(200, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }

                //sla op in json
                Save2File.save(requestData);

            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }

    static String FilePath = "src/main/java/org/ClipCloud/berichten/";

    public static String getData(String fileName) {
        StringBuilder data = new StringBuilder();
        try {
            File myObj = new File(FilePath+fileName+".txt");
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
                String fileName = "test";
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
