package io.mu.restapi;

import com.google.inject.Injector;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Set;

public class GuiceRouteProcessor extends AbstractRouteProcessor {

    private Injector injector;

    GuiceRouteProcessor(Set<Class> controllers, Injector injector) {
        super(controllers);
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
