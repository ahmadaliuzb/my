package handler;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.User;
import parser.JsonParser;
import repository.UserRepository;
import security.JwtUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            String body = new String(exchange.getRequestBody().readAllBytes());
            System.out.println(body);
            Map<String, String> form = JsonParser.jsonToMap(body);
            String username = form.get("username");
            String password = form.get("password");
            System.out.println(username);
            System.out.println(password);
            User user = UserRepository.findByUsername(username);
            if (user != null && user.password.equals(password)) {
                String token = JwtUtil.generateToken(user.username);
                String response = JsonParser.mapToJson(Map.of("token", token));
                sendJson(exchange, 200, response);
            } else {
                sendJson(exchange, 401, JsonParser.mapToJson(Map.of("error", "Invalid username or password")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            String response = JsonParser.mapToJson(Map.of("error", "Internal server error"));
            sendJson(exchange, 500, response);
        }
    }


    private void sendJson(HttpExchange exchange, int status, String body) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

}
