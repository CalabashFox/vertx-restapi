package io.calabash.vertx;

import com.google.inject.Injector;

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
