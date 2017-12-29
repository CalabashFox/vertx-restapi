package muio.restapi;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

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

    public Endpoint(HttpMethod[] methods, String path, Boolean blocking,
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

    public Endpoint(Class<?> controller, Method method, Object instance,
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

    public Route parseRoute(Router router) {
		/*RouteKey key = entry.getKey();
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
		}*/
        // TODO make handler afterwards
        return null;
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
