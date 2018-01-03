package io.mu.restapi;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.mu.restapi.annotations.Blocking;
import io.mu.restapi.annotations.Delete;
import io.mu.restapi.annotations.HttpMethod;
import io.mu.restapi.annotations.Post;
import io.mu.restapi.annotations.Put;
import io.mu.restapi.annotations.Get;
import io.mu.restapi.annotations.Path;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractRouteProcessor {

    private static final Logger log = LoggerFactory.getLogger(AbstractRouteProcessor.class);

    private static final String DELIMITER = ";";

    private Set<Endpoint> endpoints = new HashSet<>();

    private JsonObject config;

    protected abstract Object getProxyInstance(Class<?> type);

    protected abstract <T> T getControllerInstance(Class<T> type);

    AbstractRouteProcessor(Set<Class> controllers) {
        controllers.forEach(this::processController);
    }

    private <T> void processController(Class<T> type) {
        if (!isController(type)) {
            return;
        }
        T instance = getControllerInstance(type);
        if (instance == null) {
            log.error("Could not instantiate {0}", type);
        }
        Blocking blocking = type.getAnnotation(Blocking.class);
        Path classLevelPath = type.getAnnotation(Path.class);
        Endpoint classLevelEndpoint = classLevelPath == null ? null :
                new Endpoint(classLevelPath.httpMethods(), classLevelPath.value(), blocking != null, classLevelPath.consumes(), classLevelPath.produces());
        endpoints = Arrays.stream(type.getMethods())
                .filter(this::containsEndpoint)
                .map(method -> getEndPoint(type, instance, method, classLevelEndpoint))
                .collect(Collectors.toCollection(HashSet::new));
        log.debug("Controller {0} loaded", type);
    }

    private <T> Endpoint getEndPoint(Class<T> type, T instance, Method method, Endpoint classLevelEndpoint) {
        Endpoint endpoint = null;
        for (Annotation annotation : method.getAnnotations()) {
            if (isEndpoint(annotation)) {
                if (endpoint != null) {
                    log.error("Duplicate endpoints found for {0}", method);
                    return endpoint;
                }
                endpoint = getEndpoint(type, instance, method, annotation, classLevelEndpoint);
            }
        }
        return endpoint;
    }

    private <T> Endpoint getEndpoint(Class<T> type, T instance, Method method, Annotation annotation, Endpoint classLevelEndpoint) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        io.vertx.core.http.HttpMethod[] methods;
        String path = classLevelEndpoint.getPath() + ReflectionUtils.getAnnotationValue(annotation, "path", String.class, "");
        boolean blocking = method.getAnnotation(Blocking.class) != null || classLevelEndpoint.isBlocking();
        boolean regex = ReflectionUtils.getAnnotationValue(annotation, "regex", boolean.class, false);
        int order = ReflectionUtils.getAnnotationValue(annotation, "order", int.class, 0);
        String consumes = ReflectionUtils.getAnnotationValue(annotation, "consumes", String.class, classLevelEndpoint.getConsumes());
        String produces = ReflectionUtils.getAnnotationValue(annotation, "produces", String.class, classLevelEndpoint.getProduces());

        if (annotation instanceof Path) {
            Path pathAnnotation = (Path) annotation;
            methods = pathAnnotation.httpMethods();
        } else {
            HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
            methods = new io.vertx.core.http.HttpMethod[]{ io.vertx.core.http.HttpMethod.valueOf(httpMethod.value()) };
        }
        return new Endpoint(type, method, instance, methods, path, blocking, regex, order, consumes, produces);
    }

    private <T> boolean isController(Class<T> type) {
        return type.getAnnotation(Path.class) != null
                || Arrays.stream(type.getMethods()).anyMatch(this::containsEndpoint);
    }

    private boolean containsEndpoint(Method method) {
        return Arrays.stream(method.getAnnotations()).anyMatch(this::isEndpoint);
    }

    private boolean isEndpoint(Annotation annotation) {
        Class<?> type = annotation.annotationType();
        return type.equals(Get.class)
                || type.equals(Put.class)
                || type.equals(Post.class)
                || type.equals(Path.class)
                || type.equals(Delete.class);
        //|| type.equals(Fallback.class)
        //|| type.equals(FailureHandler.class);
    }

    public Set<Endpoint> getEndpoints() {
        return endpoints;
    }
}
