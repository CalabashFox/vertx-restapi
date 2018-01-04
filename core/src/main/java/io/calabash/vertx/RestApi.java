package io.calabash.vertx;

import com.google.inject.Injector;
import io.calabash.vertx.annotation.ApiGen;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestApi {

    public static final String API_CONSUMES = "api.consumes";
    public static final String API_PRODUCES = "api.produces";

    private AbstractRouteProcessor routeProcessor;

    private InvocationHandler invocationHandler;

    private Router router;

    private RestApi(Vertx vertx, JsonObject jsonObject, Injector injector) {
        this.invocationHandler = new InvocationHandler();
        this.router = Router.router(vertx);
        Set<Class> controllers = getControllers();
        if (injector == null) {
            routeProcessor = new GuiceRouteProcessor(controllers, injector);
        } else {
            routeProcessor = new RouteProcessor(controllers);
        }
        processRouter(routeProcessor);
    }

    @SuppressWarnings("unchecked")
    private Set<Class> getControllers() {
        return Stream.of(Package.getPackages())
                .filter(pkg -> pkg.isAnnotationPresent(ApiGen.class))
                .map(pkg -> ReflectionUtils.getClassInPackage(pkg.getName()))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }


    public static Router route(Vertx vertx) {
        return new RestApi(vertx, new JsonObject(), null).router;
    }

    public static Router route(Vertx vertx, Injector injector) {
        return new RestApi(vertx, new JsonObject(), injector).router;
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
