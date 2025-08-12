package org.ClipCloud.server;

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
                String response = "het is ontvangen!";
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


    static class GetHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException{

            //info opvragen
            String fileName = FilePath+"test"; // Zonder .json extensie
            String fileContent = getData(fileName);

            if (fileContent != null) {
                System.out.println("Inhoud van het bestand:");
                System.out.println(fileContent);
            } else {
                System.out.println("Kon het bestand niet lezen.");
            }
        }

    }

}
