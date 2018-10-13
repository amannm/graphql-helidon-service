open module systems.cauldron.service.graphql {
    requires io.helidon.webserver;
    requires io.helidon.common;
    requires io.helidon.webserver.json;
    requires java.logging;
    requires java.json;
    requires java.json.bind;
    requires org.eclipse.yasson;
    requires graphql.java;
    exports systems.cauldron.service.graphql;
}