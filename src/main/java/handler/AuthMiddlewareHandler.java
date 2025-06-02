package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import security.JwtUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

public class AuthMiddlewareHandler implements HttpHandler {
    private final HttpHandler next;
    private final Set<String> openPaths;

    public AuthMiddlewareHandler(HttpHandler next, Set<String> openPaths) {
        this.next = next;
        this.openPaths = openPaths;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (openPaths.contains(path)) {
            next.handle(exchange);
            return;
        }
        String auth = exchange.getRequestHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            sendError(exchange, 401, "Missing or invalid Authorization header");
            return;
        }
        String token = auth.substring(7);
        String username = JwtUtil.validateAndGetUsername(token);
        if (username == null) {
            sendError(exchange, 401, "Invalid or expired token");
            return;
        }
        exchange.setAttribute("username", username);
        next.handle(exchange);
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String json = "{\"error\":\"" + message + "\"}";
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, json.length());
        OutputStream os = exchange.getResponseBody();
        os.write(json.getBytes());
        os.close();
    }
}

