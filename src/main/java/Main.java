import com.sun.net.httpserver.HttpServer;
import connection.PostgresConnection;
import handler.AuthMiddlewareHandler;
import handler.LoginHandler;
import handler.UserMeHandler;
import reflection.ModelTableCreator;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Connection conn = PostgresConnection.connect()) {
            ModelTableCreator creator = new ModelTableCreator(conn);
            creator.createTables("model");
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        Set<String> openPaths = Set.of("/api/login", "/api/register");
        server.createContext("/api/login", new AuthMiddlewareHandler(new LoginHandler(), openPaths));
        server.createContext("/api/register", new AuthMiddlewareHandler(new LoginHandler(), openPaths));
        server.createContext("/api/user", new AuthMiddlewareHandler(new UserMeHandler(), openPaths));
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8080");
    }
}

