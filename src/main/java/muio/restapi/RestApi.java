package muio.restapi;

import com.google.inject.Injector;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

public class RestApi {

    public static final String API_PACKAGES = "api.packages";
    public static final String API_CONSUMES = "api.consumes";
    public static final String API_PRODUCES = "api.produces";

    private static final String PROXY_EXT = "$Proxy";

    private Router router;

    private RestApi(Vertx vertx, JsonObject jsonObject, Injector injector) {
        this.router = Router.router(vertx);
        if (injector == null) {
            processRouter(new GuiceRouteProcessor(defaultConfig(jsonObject), injector));
        } else {
            processRouter(new RouteProcessor(defaultConfig(jsonObject)));
        }
    }

    public static Router route(Vertx vertx, JsonObject jsonObject) {
        return new RestApi(vertx, jsonObject, null).router;
    }

    public static Router route(Vertx vertx, JsonObject jsonObject, Injector injector) {
        return new RestApi(vertx, jsonObject, injector).router;
    }

    private void processRouter(AbstractRouteProcessor processor) {
        for (Map.Entry<RouteKey, Route> entry : processor.getRouteMap().entrySet()) {
            RouteKey key = entry.getKey();
            Route route = entry.getValue();
            if (key.regex) {
                if (key.methods == null) {
                    router.routeWithRegex(key.path);
                } else {
                    // TODO blockinghandler
                    Arrays.stream(key.methods)
                            .forEach(method -> router.routeWithRegex(method, key.path)
                                    .handler(ctx -> handler(ctx, route)));
                }
            } else {
                if (key.methods == null) {
                    router.route(key.path);
                } else {
                    Arrays.stream(key.methods)
                            .forEach(method -> router.route(method, key.path)
                                    .handler(ctx -> handler(ctx, route)));
                }
            }
            // TODO make handler afterwards
        }
    }

    private <T> void handler(RoutingContext routingContext, Route route) {
        /*ctx -> {
            //String proxyName = controller.getName() + PROXY_EXT;
            try {
                //Class proxyClass = Class.forName(proxyName);
                //Method proxyMethod = proxyClass.getMethod(method.toString(), RoutingContext.class);
            } catch (Exception e) {

            }
        };*/
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
