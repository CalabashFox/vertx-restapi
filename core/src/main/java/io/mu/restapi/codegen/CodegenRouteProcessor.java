package io.mu.restapi.codegen;

import io.mu.restapi.AbstractRouteProcessor;

import javax.lang.model.element.Element;
import java.lang.reflect.Constructor;
import java.util.Set;

public class CodegenRouteProcessor extends AbstractRouteProcessor {


    public CodegenRouteProcessor(Set<? extends Element> elements) {
        super(elements);
    }

    @Override
    protected Object getProxyInstance(Class<?> type) {
        return null;
    }

    @Override
    protected <T> T getControllerInstance(Class<T> type) {
        try {
            Constructor<T> constructor = type.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
