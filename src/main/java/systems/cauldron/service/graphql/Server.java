package systems.cauldron.service.graphql;

import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerConfiguration;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.json.JsonSupport;

import java.io.IOException;
import java.util.logging.LogManager;

public class Server {

    public static void main(final String[] args) throws IOException {
        LogManager.getLogManager().readConfiguration();
        start();
    }

    protected static WebServer start() {
        WebServer server = WebServer.create(getConfig(), getRouting());
        server.start().thenAccept(ws -> System.out.println("server started @ http://localhost:" + ws.port()));
        server.whenShutdown().thenRun(() -> System.out.println("server stopped"));
        return server;
    }

    private static ServerConfiguration getConfig() {
        Config config = Config.create();
        return ServerConfiguration.fromConfig(config.get("server"));
    }

    private static Routing getRouting() {
        return Routing.builder()
                .register(JsonSupport.get())
                .register("/graphql", new GraphService())
                .build();
    }

}
