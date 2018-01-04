package io.calabash.vertx.codegen;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class ParameterModel {

    private String name;
    private String type;
    private String annotationType;
    private String parameterType;

    public ParameterModel(VariableElement variable) {
        name = variable.getSimpleName().toString();
        TypeMirror typeMirror = variable.asType();
        type = typeMirror.toString();
    }

}
