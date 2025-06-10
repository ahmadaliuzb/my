import com.sun.net.httpserver.HttpServer;
import connection.PostgresConnection;
import handler.AuthMiddlewareHandler;
import handler.LoginHandler;
import handler.UserMeHandler;
import reflection.ModelTableCreator;
import repository.UserRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(UserRepository.class.getName());

    public static void main(String[] args) throws IOException {
        runPostgresReflection();
        runHttpServer();
    }

    private static void runPostgresReflection() {
        try (Connection conn = PostgresConnection.connect()) {
            ModelTableCreator creator = new ModelTableCreator(conn);
            creator.createTables("model");
        } catch (Exception e) {
            logger.info(Arrays.toString(e.getStackTrace()));
        }
    }

    private static void runHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        Set<String> openPaths = Set.of("/api/login", "/api/register");
        server.createContext("/api/login", new AuthMiddlewareHandler(new LoginHandler(), openPaths));
        server.createContext("/api/register", new AuthMiddlewareHandler(new LoginHandler(), openPaths));
        server.createContext("/api/user", new AuthMiddlewareHandler(new UserMeHandler(), openPaths));
        server.setExecutor(null);
        server.start();
        logger.info("Server started on http://localhost:8080");
    }


}

