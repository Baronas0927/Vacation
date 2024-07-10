package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.net.URLDecoder;

import static org.example.Main.*;

public class Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        handleCORS(exchange);
        if (path.equals("/createVacation") && method.equals("POST")) {
            handleCreateVacation(exchange);
        }
        if (path.equals("/getVacation") && method.equals("GET")) {
            handleGetVacation(exchange);
        }
        if (path.equals("/getVacations") && method.equals("GET")) {
            handleGetVacations(exchange);
        }
        if (path.equals("/createVacation") && method.equals("POST")) {
            handleCreateVacation(exchange);
        }
        if (path.equals("/updateVacation") && method.equals("POST")) {
            handleUpdateVacation(exchange);
        }
        if (path.equals("/deleteVacation") && method.equals("POST")) {
            handleDeleteVacation(exchange);
        }

        exchange.sendResponseHeaders(400, -1);
        OutputStream os = exchange.getResponseBody();
        os.close();
    }

    private void handleCORS(HttpExchange exchange) {
        // Allow requests from all origins
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        // Allow specific methods
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        // Allow specific headers
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "*");
        // Allow credentials, if needed
        exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
    }

    private void handleGetVacations(HttpExchange exchange) throws IOException {
        String response = gson.toJson(vacations);
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void handleGetVacation(HttpExchange exchange) throws IOException {
        String response = gson.toJson(vacations);//reik get query padaryt
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void handleCreateVacation(HttpExchange exchange) throws IOException {
        System.out.println("handleCreateVacation");
            InputStream requestBody = exchange.getRequestBody();
            BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
            String  dataString = "";
            String line;
        try {
            while (true) {
                if (!((line = reader.readLine()) != null)) break;
                dataString += line;
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
            System.out.println(dataString);
            Vacation vacation = new Vacation();
            try {
            vacation = gson.fromJson(dataString.toString(), Vacation.class);
            }catch (Exception e){
                System.out.println("gson");
                e.printStackTrace();
            }
            vacations.add(vacation);
            saveVacations();
//            requestBody.close();
        String response = "Vacation has been created succesfully";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

//    private void handleUpdateVacation(HttpExchange exchange) throws IOException {
//        //update vacation
//        String query = exchange.getRequestURI().getQuery();
//        Map<String, String> params = queryToMap(query);
//        long id = Long.parseLong(params.get("id"));
//        String title = String.join(params.get("title"));
//        String country = String.join(params.get("country"));
//        String city = String.join(params.get("city"));
//        String season = String.join(params.get("season"));
//        String[] photos = params.get("photos").split(",");
//        double price = Double.parseDouble(params.get("price"));
//        String description = String.join(params.get("title"));
//        int[] rating = new int[]{Integer.parseInt(params.get(""))};
//        Vacation vacation = new Vacation(id, title, country, city, season, photos, price, title, rating);
//        vacations.stream()
//                .filter(vacation1 -> vacation1.getId() == vacation.getId())
//                .findFirst()
//                .map(existingVacation -> {
//                    vacations.set(vacations.indexOf(existingVacation), vacation);
//                    return true;
//                })
//                .orElseGet(() -> {
//                    vacations.add(vacation);
//                    return false;
//                });
//        saveVacations();
//        String response = "Vacation has been updated succesfully";
//        exchange.sendResponseHeaders(200, response.getBytes().length);
//        OutputStream os = exchange.getResponseBody();
//        os.write(response.getBytes());
//        os.close();
//    }
private void handleUpdateVacation(HttpExchange exchange) throws IOException {
    Vacation vacationToUpdate = requestVacation(exchange);
    vacations.stream()
            .filter(v -> v.getId() == vacationToUpdate.getId())
            .findFirst()
            .map(existingVacation -> {
                vacations.set(vacations.indexOf(existingVacation), vacationToUpdate);
                return true;
            })
            .orElseGet(() -> {
                vacations.add(vacationToUpdate);
                return false;
            });
    saveVacations();
    String response = "Vacation has been updated successfully";
    exchange.sendResponseHeaders(200, response.getBytes().length);
    OutputStream os = exchange.getResponseBody();
    os.write(response.getBytes());
    os.close();
}

//    private void handleDeleteVacation(HttpExchange exchange) {
//        //delete vacation
//        System.out.println("delete vacation");
//        String query = exchange.getRequestURI().getQuery();
//        long id = Long.parseLong(query.split("=")[1]);
//        boolean removed = vacations.removeIf(vacation -> vacation.getId() == id);
//        System.out.println("blo");
//        try {
//        if (removed) {
//            saveVacations();
//            System.out.println("bla");
//            String response = "Vacation has been deleted successfully";
//            exchange.sendResponseHeaders(200, response.getBytes().length);
//            OutputStream os = exchange.getResponseBody();
//            os.write(response.getBytes());
//            os.close();
//        } else {
//            exchange.sendResponseHeaders(404, -1);
//        }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
private Vacation requestVacation(HttpExchange exchange) throws IOException {
    InputStream requestBody = exchange.getRequestBody();
    BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
    String dataString = "";
    String line;
    while ((line = reader.readLine()) != null) {
        dataString += line;
    }
    System.out.println(dataString);
    reader.close();
    Vacation vacation = gson.fromJson(dataString, Vacation.class);
    return vacation;
}

    private void handleDeleteVacation(HttpExchange exchange) throws IOException {
        Vacation vacationToDelete = requestVacation(exchange);
        boolean removed = vacations.removeIf(v -> v.getId() == vacationToDelete.getId());
        try {
            if (removed) {
                saveVacations();
                String response = "Vacation has been deleted successfully";
                System.out.println(response);
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(URLDecoder.decode(entry[0], StandardCharsets.UTF_8), URLDecoder.decode(entry[1], StandardCharsets.UTF_8));
            } else {
                result.put(URLDecoder.decode(entry[0], StandardCharsets.UTF_8), "");
            }
        }
        return result;
    }

}
