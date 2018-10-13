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

import javax.json.JsonObject;
import java.util.Optional;

public class GraphQueryService implements Service {

    private static final GraphQL build;

    static {
        String schema = "type Query{hello: String}";

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> builder.dataFetcher("hello", new StaticDataFetcher("world")))
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        build = GraphQL.newGraphQL(graphQLSchema).build();
    }


    @Override
    public void update(Routing.Rules rules) {
        //graphql server reference @ https://graphql.org/learn/serving-over-http/
        rules
                .get("/graphql", this::get)
                .post("/graphql", this::post);
    }

    public void get(ServerRequest request, ServerResponse response) {

        Parameters parameters = request.queryParams();
        Optional<String> queryParam = parameters.first("query");
        if (queryParam.isPresent()) {

            String queryString = queryParam.get();

            //TODO: handle these too
            Optional<String> operationNameParam = parameters.first("operationName");

            Optional<String> variablesParam = parameters.first("variables");

            String resultString = handleQuery(queryString);
            response.send(resultString);

        } else {

            response.status(Http.Status.BAD_REQUEST_400);

        }

    }


    public void post(ServerRequest request, ServerResponse response) {

        Parameters parameters = request.queryParams();
        Optional<String> queryParam = parameters.first("query");
        if (queryParam.isPresent()) {
            String queryString = queryParam.get();
            String resultString = handleQuery(queryString);
            response.send(resultString);
        } else {
            Optional<MediaType> mediaTypeResult = request.headers().contentType();
            if (mediaTypeResult.isPresent()) {
                MediaType mediaType = mediaTypeResult.get();
                if (MediaType.APPLICATION_JSON.equals(mediaType)) {
                    request.content().as(JsonObject.class).thenAccept(jsonRequest -> {

                        String queryString = jsonRequest.getString("query");

                        //TODO: handle these too
                        if (jsonRequest.containsKey("operationName")) {
                            String operationName = jsonRequest.getString("operationName");
                        }

                        if (jsonRequest.containsKey("variables")) {
                            JsonObject variables = jsonRequest.getJsonObject("variables");
                        }

                        String resultString = handleQuery(queryString);
                        response.send(resultString);

                    });
                } else {
                    if ("application/graphql".equals(mediaType.toString())) {
                        request.content().as(String.class).thenAccept(queryString -> {

                            String resultString = handleQuery(queryString);
                            response.send(resultString);

                        });
                    } else {

                        response.status(Http.Status.UNSUPPORTED_MEDIA_TYPE_415);

                    }
                }
            }
        }

    }

    private String handleQuery(String queryString) {
        ExecutionResult executionResult = build.execute(queryString);
        return executionResult.getData().toString();
    }
}
