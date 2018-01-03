package io.mu.restapi.codegen;

import io.mu.restapi.annotations.PathVariable;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassModel {

    private List<String> importedTypes = new ArrayList<>();
    private String name;
    private String pkg;
    private String inheritedClass;
    private List<String> interfaces = new ArrayList<>();
    private List<MethodModel> methods = new ArrayList<>();

    ClassModel(Element element) {
        Set<ExecutableElement> methods = ElementFilter.methodsIn(Set.of(element));
        for (ExecutableElement method : methods) {
            Name methodName = method.getSimpleName();
            List<? extends VariableElement> parameters = method.getParameters();
            for (VariableElement parameter : parameters) {
                TypeMirror type = parameter.asType();
                String fullTypeClassName = type.toString();
                PathVariable pathVariable = type.getAnnotation(PathVariable.class);
            }
        }
    }
}
