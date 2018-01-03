package io.mu.restapi;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.lang.reflect.Constructor;

public class RouteProcessor extends AbstractRouteProcessor {

    private static final Logger log = LoggerFactory.getLogger(RouteProcessor.class);

    RouteProcessor(JsonObject config) {
        super(config);
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
