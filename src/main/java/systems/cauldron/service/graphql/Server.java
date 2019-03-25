package systems.cauldron.service.graphql;

import io.helidon.config.Config;
import io.helidon.media.jsonp.server.JsonSupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerConfiguration;
import io.helidon.webserver.WebServer;

import java.io.IOException;
import java.util.logging.LogManager;

import static io.helidon.config.ConfigSources.classpath;

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
        return ServerConfiguration.create(Config.builder()
                .sources(classpath("application.yaml"))
                .build());
    }

    private static Routing getRouting() {
        return Routing.builder()
                .register(JsonSupport.create())
                .register("/graphql", new GraphService())
                .build();
    }

}
