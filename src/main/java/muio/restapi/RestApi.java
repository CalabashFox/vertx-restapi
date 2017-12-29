package muio.restapi;

import com.google.inject.Injector;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class RestApi {

    public static final String API_PACKAGES = "api.packages";
    public static final String API_CONSUMES = "api.consumes";
    public static final String API_PRODUCES = "api.produces";

    private static final String PROXY_EXT = "$Proxy";

    private AbstractRouteProcessor routeProcessor;

    private Router router;

    private RestApi(Vertx vertx, JsonObject jsonObject, Injector injector) {
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
        processor.getEndpoints().forEach(endpoint -> endpoint.parseRoute(router));
    }

    private <T> void handler(RoutingContext routingContext, Endpoint endpoint) {
        String proxyName = endpoint.getClass().getName() + PROXY_EXT;
        try {
            Class proxyClass = Class.forName(proxyName);
            Object proxy = routeProcessor.getProxyInstance(proxyClass);
            //Method proxyMethod = proxyClass.getMethod(method.toString(), RoutingContext.class);
        } catch (Exception e) {

        }
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
