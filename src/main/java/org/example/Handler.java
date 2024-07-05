package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
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
        if (path.equals("/createVacation") && method.equals("POST")) {
            handleCreateVacation(exchange);
        }
        if (path.equals("/getVacation") && method.equals("GET")){
            handleGetVacation(exchange);
        }
        if (path.equals("/getVacations") && method.equals("GET")){
            handleGetVacations(exchange);
        }
        if (path.equals("/createVacation") && method.equals("POST")){
            handleCreateVacation(exchange);
        }
        if (path.equals("/updateVacation") && method.equals("POST")){
            handleUpdateVacation(exchange);
        }
        if (path.equals("/deleteVacation") && method.equals("POST")){
            handleDeleteVacation(exchange);
        }

        exchange.sendResponseHeaders(400, -1);
        OutputStream os = exchange.getResponseBody();
        os.close();
    }


    private void handleCreateVacation(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = queryToMap(query);
        String title = String.valueOf(params.get("title"));
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
    private void handleGetVacation(HttpExchange exchange) throws IOException {
        // if(exchange.getRequestMethod().equals("GET")) {
        String response = gson.toJson(vacations);
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();

    }
    private void handleGetVacations(HttpExchange exchange)throws IOException{
        String response = gson.toJson(vacations);
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    private void handleDeleteVacation(HttpExchange exchange)throws IOException{
        //delete vacation
        String query = exchange.getRequestURI().getQuery();
        long id = Long.parseLong(query.split("=")[1]);
            boolean removed  = vacations.removeIf(vacation -> vacation.getId() ==id);
            if (removed){
                saveVacations();
                String response = "Vacation has been deleted successfully";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
    }
    private void handleUpdateVacation(HttpExchange exchange) throws IOException{
        //update vacation
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = queryToMap(query);
        long id = Long.parseLong(params.get("id"));
        String title = String.join(params.get("title"));
        String country = String.join(params.get("country"));
        String city = String.join(params.get("city"));
        String season = String.join(params.get("season"));
        String[] photos = params.get("photos").split(",");
        double price = Double.parseDouble(params.get("price"));
        String description = String.join(params.get("title"));
        int[] rating = new int[]{Integer.parseInt(params.get(""))};

        Vacation vacation = new Vacation(id, title, country, city, season, photos, price, title, rating);
        vacations.stream()
                .filter(vacation1 -> vacation1.getId() == vacation.getId())
                .findFirst()
                .map(existingVacation -> {
                    vacations.set(vacations.indexOf(existingVacation), vacation);
                    return true;
                        })
                .orElseGet(()->{
                    vacations.add(vacation);
                    return false;
                });
        saveVacations();
        String response = "Vacation has been updated succesfully";
        exchange.sendResponseHeaders(200,response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
