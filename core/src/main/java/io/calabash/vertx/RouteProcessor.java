package io.calabash.vertx;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Set;

public class RouteProcessor extends AbstractRouteProcessor {

    private static final Logger log = LoggerFactory.getLogger(RouteProcessor.class);

    RouteProcessor(Set<Class> controllers) {
        super(controllers);
    }

    @Override
    protected Object getProxyInstance(Class<?> type) {
        try {
            Constructor constructor = type.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            log.error("constructor missing");
        } catch (Exception e) {
            log.error("instantiation failed.");
        }
        return null;
    }

    @Override
    protected <T> T getControllerInstance(Class<T> type) {
        try {
            Constructor<T> constructor = type.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            log.error("constructor missing");
        } catch (Exception e) {
            log.error("instantiation failed.");
        }
        return null;
    }

}
