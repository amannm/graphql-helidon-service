package systems.cauldron.service.graphql;

import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerConfiguration;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.json.JsonSupport;

import java.io.IOException;
import java.util.logging.LogManager;

public class Main {

    public static void main(final String[] args) throws IOException {
        startServer();
    }

    protected static WebServer startServer() throws IOException {
        LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
        WebServer server = WebServer.create(createConfig(), createRouting());
        server.start().thenAccept(ws -> System.out.println("WEB server is up! http://localhost:" + ws.port()));
        server.whenShutdown().thenRun(() -> System.out.println("WEB server is DOWN. Good bye!"));
        return server;
    }

    private static Routing createRouting() {
        return Routing.builder()
                .register(JsonSupport.get())
                .register("/graphql", new GraphQueryService())
                .build();
    }

    private static ServerConfiguration createConfig() {
        Config config = Config.create();
        return ServerConfiguration.fromConfig(config.get("server"));
    }

}
