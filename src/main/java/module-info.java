module systems.cauldron.service.graphql {
    requires io.helidon.common;
    requires java.json;
    requires java.json.bind;
    requires io.helidon.webserver.json;
    requires java.logging;
    requires graphql.java;
    requires io.helidon.webserver;
    exports systems.cauldron.service.graphql;
}