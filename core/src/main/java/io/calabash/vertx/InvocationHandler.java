package io.calabash.vertx;

import io.vertx.ext.web.RoutingContext;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class InvocationHandler {

    private static final String PROXY_EXT = "$Proxy";
    private static final String DELIMITER = "$$";

    private Map<String, MethodHandle> cache = new HashMap<>();

    InvocationHandler() {

    }

    // TODO handle method overloading
    private void cache(String controllerName, String methodName, Class<?> type, Method method) {
        String proxyName = controllerName + PROXY_EXT;
        try {
            Class proxyClass = Class.forName(proxyName);
            Method proxyMethod = proxyClass.getMethod(methodName);
            MethodHandle methodHandle = ReflectionUtils.getMethodHandleReference(proxyClass, proxyMethod);
            if (methodHandle != null) {
                cache.put(getKey(controllerName, methodName), methodHandle);
            }
        } catch (Exception e) {

        }
    }

    public void invoke(String controllerName, String methodName, RoutingContext routingContext) throws Throwable {
        String key = getKey(controllerName, methodName);
        if (!cache.containsKey(key)) {

        }
        MethodHandle methodHandle = cache.get(key);
        methodHandle.invokeExact(routingContext);
    }

    private String getKey(String controller, String method) {
        return controller + PROXY_EXT + DELIMITER + method;
    }

}
