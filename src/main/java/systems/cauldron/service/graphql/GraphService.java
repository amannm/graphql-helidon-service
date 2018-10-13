package systems.cauldron.service.graphql;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.helidon.common.http.Http;
import io.helidon.common.http.MediaType;
import io.helidon.common.http.Parameters;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.io.StringReader;
import java.util.Map;
import java.util.Optional;

public class GraphService implements Service {

    private static final GraphQL graph;

    static {
        String schema = "type Query{hello: String}";

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> builder.dataFetcher("hello", new StaticDataFetcher("world")))
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        graph = GraphQL.newGraphQL(graphQLSchema).build();
    }


    @Override
    public void update(Routing.Rules rules) {
        rules
                .get(this::get)
                .post(this::post);
    }

    public void get(ServerRequest request, ServerResponse response) {

        Parameters parameters = request.queryParams();
        Optional<String> queryParam = parameters.first("query");

        if (!queryParam.isPresent()) {
            response.status(Http.Status.BAD_REQUEST_400).send();
            return;
        }

        String queryString = queryParam.get();

        //TODO: handle these too
        Optional<String> operationNameParam = parameters.first("operationName");

        Optional<String> variablesParam = parameters.first("variables");

        JsonObject resultJson = processQuery(queryString);
        response.send(resultJson);

    }


    public void post(ServerRequest request, ServerResponse response) {
        request.queryParams()
                .first("query")
                .ifPresentOrElse(
                        queryString -> response.send(processQuery(queryString)),
                        () -> {
                            request.headers().contentType().ifPresentOrElse(
                                    mediaType -> {
                                        if (MediaType.APPLICATION_JSON.equals(mediaType)) {
                                            //TODO: handle remaining conditions
                                            request.content().as(JsonObject.class)
                                                    .thenAccept(jsonRequest -> {
                                                        String queryString = jsonRequest.getString("query");

                                                        //TODO: respect/validate these
                                                        if (jsonRequest.containsKey("operationName")) {
                                                            String operationName = jsonRequest.getString("operationName");
                                                        }

                                                        if (jsonRequest.containsKey("variables")) {
                                                            JsonObject variables = jsonRequest.getJsonObject("variables");
                                                        }

                                                        response.send(processQuery(queryString));
                                                    });
                                        } else {
                                            if ("application/graphql".equals(mediaType.toString())) {
                                                //TODO: handle remaining conditions
                                                request.content().as(String.class)
                                                        .thenAccept(queryString -> {
                                                            response.send(processQuery(queryString));
                                                        });
                                            } else {
                                                response.status(Http.Status.UNSUPPORTED_MEDIA_TYPE_415).send();
                                            }
                                        }
                                    },
                                    () -> response.status(Http.Status.UNSUPPORTED_MEDIA_TYPE_415).send()
                            );
                        });
    }

    private static JsonObject processQuery(String queryString) {
        ExecutionResult executionResult = graph.execute(queryString);
        Map<String, Object> resultMap = executionResult.toSpecification();
        //TODO: avoid unnecessary reserialization
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        resultMap.forEach((k, v) -> {
            Jsonb jsonb = JsonbBuilder.create();
            String result = jsonb.toJson(v);
            JsonObject jsonResult;
            try (JsonReader reader = Json.createReader(new StringReader(result))) {
                jsonResult = reader.readObject();
            }
            objectBuilder.add(k, jsonResult);
        });
        return objectBuilder.build();

    }


}
