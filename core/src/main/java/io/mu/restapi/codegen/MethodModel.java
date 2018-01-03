package io.mu.restapi.codegen;

import javax.lang.model.element.ExecutableElement;
import java.util.ArrayList;
import java.util.List;

public class MethodModel {

    private String name;
    private List<ParameterModel> parameters = new ArrayList<>();

    MethodModel(ExecutableElement method) {

    }

}
