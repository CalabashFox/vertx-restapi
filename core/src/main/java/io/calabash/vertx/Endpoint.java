package io.calabash.vertx;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Endpoint {

    private final HttpMethod[] methods;
    private final String path;
    private final Boolean blocking;
    private final boolean regex;
    private final int order;
    private final String consumes;
    private final String produces;
    private final Class<?> controller;
    private final Method method;
    private final Object instance;

    Endpoint(HttpMethod[] methods, String path, Boolean blocking,
                    String consumes, String produces) {
        this.controller = null;
        this.method = null;
        this.instance = null;
        this.methods = methods;
        this.path = path;
        this.blocking = blocking;
        this.regex = false;
        this.order = 0;
        this.consumes = consumes;
        this.produces = produces;
    }

    Endpoint(Class<?> controller, Method method, Object instance,
                    HttpMethod[] methods, String path, Boolean blocking, boolean regex,
                    int order, String consumes, String produces) {
        this.controller = controller;
        this.method = method;
        this.instance = instance;
        this.methods = methods;
        this.path = path;
        this.blocking = blocking;
        this.regex = regex;
        this.order = order;
        this.consumes = consumes;
        this.produces = produces;
    }

    public Set<Route> parseRoute(final Router router, final InvocationHandler invocationHandler) {
        if (path.isEmpty()) {
            return Set.of(parseRoute(router.route(), invocationHandler));
        } else if (methods.length == 0) {
            return Set.of(parseRoute(regex ? router.routeWithRegex(path) : router.route(path), invocationHandler));
        }
        return Arrays.stream(methods)
                .map(method -> regex ? router.routeWithRegex(method, path) : router.route(method, path))
                .map(route -> parseRoute(route, invocationHandler))
                .collect(Collectors.toSet());
    }

    private Route parseRoute(final Route route, final InvocationHandler invocationHandler) {
        Handler<RoutingContext> handler = routingContext -> {
            try {
                invocationHandler.invoke(controller.getName(), method.getName(), routingContext);
            } catch (Throwable t) {
                routingContext.fail(t);
            }
        };
        if (blocking) {
            route.blockingHandler(handler);
        } else {
            route.handler(handler);
        }
        return route;
    }

    public HttpMethod[] getMethods() {
        return methods;
    }

    public String getPath() {
        return path;
    }

    public Boolean isBlocking() {
        return blocking;
    }

    public boolean isRegex() {
        return regex;
    }

    public int getOrder() {
        return order;
    }

    public String getConsumes() {
        return consumes;
    }

    public String getProduces() {
        return produces;
    }

    public Class<?> getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }

    public Object getInstance() {
        return instance;
    }
}
