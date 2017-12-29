package muio.restapi;

import com.google.inject.Injector;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.lang.invoke.MethodHandle;
import java.util.HashMap;
import java.util.Map;

public class RestApi {

    public static final String API_PACKAGES = "api.packages";
    public static final String API_CONSUMES = "api.consumes";
    public static final String API_PRODUCES = "api.produces";

    private AbstractRouteProcessor routeProcessor;

    private InvocationHandler invocationHandler;

    private Router router;

    private RestApi(Vertx vertx, JsonObject jsonObject, Injector injector) {
        this.invocationHandler = new InvocationHandler();
        this.router = Router.router(vertx);
        if (injector == null) {
            routeProcessor = new GuiceRouteProcessor(defaultConfig(jsonObject), injector);
        } else {
            routeProcessor = new RouteProcessor(defaultConfig(jsonObject));
        }
        processRouter(routeProcessor);
    }

    public static Router route(Vertx vertx, JsonObject jsonObject) {
        return new RestApi(vertx, jsonObject, null).router;
    }

    public static Router route(Vertx vertx, JsonObject jsonObject, Injector injector) {
        return new RestApi(vertx, jsonObject, injector).router;
    }

    private void processRouter(AbstractRouteProcessor processor) {
        processor.getEndpoints().forEach(endpoint -> endpoint.parseRoute(router, invocationHandler));
    }

    private JsonObject defaultConfig(JsonObject config) {
        setDefaultValue(config, API_CONSUMES, "applications/json");
        setDefaultValue(config, API_PRODUCES, "applications/json");
        return config;
    }

    private void setDefaultValue(JsonObject config, String key, String value) {
        if (!config.containsKey(key)) {
            config.put(key, value);
        }
    }

}
