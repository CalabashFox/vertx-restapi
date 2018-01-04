package io.calabash.vertx.codegen;

import io.vertx.ext.web.RoutingContext;

import javax.lang.model.element.ExecutableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MethodModel {

    private final String name;
    private final String routingContextType;
    private final List<ParameterModel> parameters = new ArrayList<>();

    MethodModel(ExecutableElement method) {
        name = method.getSimpleName().toString();
        routingContextType = RoutingContext.class.getName();
        parameters.addAll(method.getParameters()
                .stream()
                .map(ParameterModel::new)
                .collect(Collectors.toList()));
    }

    public String getName() {
        return name;
    }

    public List<ParameterModel> getParameters() {
        return parameters;
    }
}
