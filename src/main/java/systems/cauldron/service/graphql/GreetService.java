package systems.cauldron.service.graphql;

import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

import javax.json.Json;
import javax.json.JsonObject;

public class GreetService implements Service {

    private static final Config CONFIG = Config.create().get("app");

    private static String greeting = CONFIG.get("greeting").asString("Ciao");

    @Override
    public final void update(final Routing.Rules rules) {
        rules.get("/", this::getDefaultMessage)
                .get("/{name}", this::getMessage)
                .put("/greeting/{greeting}", this::updateGreeting);
    }

    private void getDefaultMessage(final ServerRequest request, final ServerResponse response) {
        String msg = String.format("%s %s!", greeting, "World");

        JsonObject returnObject = Json.createObjectBuilder()
                .add("message", msg)
                .build();
        response.send(returnObject);
    }


    private void getMessage(final ServerRequest request, final ServerResponse response) {
        String name = request.path().param("name");
        String msg = String.format("%s %s!", greeting, name);

        JsonObject returnObject = Json.createObjectBuilder()
                .add("message", msg)
                .build();
        response.send(returnObject);
    }

    private void updateGreeting(final ServerRequest request, final ServerResponse response) {
        greeting = request.path().param("greeting");

        JsonObject returnObject = Json.createObjectBuilder()
                .add("greeting", greeting)
                .build();
        response.send(returnObject);
    }
}
