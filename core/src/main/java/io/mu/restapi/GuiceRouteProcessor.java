package io.mu.restapi;

import com.google.inject.Injector;
import io.vertx.core.json.JsonObject;

public class GuiceRouteProcessor extends AbstractRouteProcessor {

    private Injector injector;

    GuiceRouteProcessor(JsonObject config, Injector injector) {
        super(config);
    }

    @Override
    protected Object getProxyInstance(Class<?> type) {
        return injector.getInstance(type);
    }

    @Override
    protected <T> T getControllerInstance(Class<T> type) {
        return injector.getInstance(type);
    }
}
