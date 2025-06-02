package handler;

import com.sun.net.httpserver.*;
import model.User;
import parser.JsonParser;
import repository.UserRepository;
import security.JwtUtil;

import java.io.IOException;
import java.io.OutputStream;

public class UserMeHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String auth = exchange.getRequestHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            exchange.sendResponseHeaders(401, -1);
            return;
        }
        String token = auth.substring(7);
        String username = JwtUtil.validateAndGetUsername(token);
        if (username == null) {
            exchange.sendResponseHeaders(401, -1);
            return;
        }
        User user = UserRepository.findByUsername(username);
        if (user == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }
        String response =JsonParser.objectToJson(user);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
