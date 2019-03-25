open module systems.cauldron.service.graphql {
    requires io.helidon.webserver;
    requires io.helidon.media.jsonp.server;
    requires java.json;
    requires java.json.bind;
    requires java.logging;
    requires graphql.java;
    exports systems.cauldron.service.graphql;
}