package muio.restapi;

import io.vertx.core.http.HttpMethod;

public class RouteKey {

    public final HttpMethod[] methods;
    public final String path;
    public final boolean blocking;
    public final boolean regex;
    public final int order;
    public final String consumes;
    public final String produces;

    public RouteKey(HttpMethod[] methods, String path, boolean blocking, boolean regex,
                    int order, String consumes, String produces) {
        this.methods = methods;
        this.path = path;
        this.blocking = blocking;
        this.regex = regex;
        this.order = order;
        this.consumes = consumes;
        this.produces = produces;
    }
}
