package muio.restapi;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import muio.restapi.annotations.Delete;
import muio.restapi.annotations.FailureHandler;
import muio.restapi.annotations.Fallback;
import muio.restapi.annotations.Get;
import muio.restapi.annotations.Path;
import muio.restapi.annotations.Post;
import muio.restapi.annotations.Put;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractRouteProcessor {

    private static final Logger log = LoggerFactory.getLogger(AbstractRouteProcessor.class);

    private static final String DELIMITER = ";";

    private Map<RouteKey, Route> routeMap = new HashMap<>();

    private JsonObject config;

    protected abstract <T> T getControllerInstance(Class<T> type);

    AbstractRouteProcessor(JsonObject config) {
        processPackages(getPackages(config));
    }

    private void processPackages(String[] packages) {
        Arrays.stream(packages)
                .map(ReflectionUtils::getClassInPackage)
                .flatMap(Set::stream)
                .forEach(this::processController);
    }

    private String[] getPackages(JsonObject config) {
        if (!config.containsKey(RestApi.API_PACKAGES)) {
            throw new RestApiException("api.packages not specified.");
        }
        return config.getString(RestApi.API_PACKAGES).split(DELIMITER);
    }

    protected <T> void processController(Class<T> type) {
        if (!isController(type)) {
            return;
        }
        T instance = getControllerInstance(type);
        if (instance == null) {
            log.error("Could not instantiate {0}", type);
        }
        String classLevelPath = "";
        routeMap.putAll(Arrays.stream(type.getMethods())
                .filter(this::containsEndPoint)
                .collect(Collectors.toMap(method -> getRouteKey(classLevelPath, method),
                        method -> getRoute(type, instance, classLevelPath, method))));
        log.debug("Controller {0} loaded", type);
    }

    private RouteKey getRouteKey(String classLevelPath, Method method) {
        RouteKey routeKey = null;
        for (Annotation annotation : method.getAnnotations()) {
            if (isEndPoint(annotation)) {
                if (routeKey != null) {
                    log.error("Duplicate endpoints found for {0}", method);
                    return routeKey;
                } else {
                    routeKey = getRouteKey(annotation);
                }
            }
        }
        return routeKey;
    }

    private RouteKey getRouteKey(Annotation annotation) {
        Class<?> type = annotation.annotationType();
        return null;
    }

    private <T> Route<T> getRoute(Class<T> type, T instance, String classLevelPath, Method method) {
        return null;
    }

    protected <T> boolean isController(Class<T> type) {
        return type.getAnnotation(Path.class) != null
                || Arrays.stream(type.getMethods()).anyMatch(this::containsEndPoint);
    }

    private boolean containsEndPoint(Method method) {
        return Arrays.stream(method.getAnnotations()).anyMatch(this::isEndPoint);
    }

    private boolean isEndPoint(Annotation annotation) {
        Class<?> type = annotation.annotationType();
        return type.equals(Get.class)
                || type.equals(Put.class)
                || type.equals(Post.class)
                || type.equals(Path.class)
                || type.equals(Delete.class)
                || type.equals(Fallback.class)
                || type.equals(FailureHandler.class);
    }

    public Map<RouteKey, Route> getRouteMap() {
        return routeMap;
    }
}
