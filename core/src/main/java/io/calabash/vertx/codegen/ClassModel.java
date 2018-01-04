package io.calabash.vertx.codegen;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassModel {

    private final List<String> importedTypes = new ArrayList<>();
    private final String name;
    private final String pkg;
    private final String superClass;
    private final List<String> interfaces = new ArrayList<>();
    private final List<MethodModel> methods = new ArrayList<>();

    //TODO filter imported types which are in same package
    ClassModel(Element element) {
        name = element.getSimpleName().toString();
        pkg = element.getEnclosingElement().getSimpleName().toString();
        TypeElement typeElement = (TypeElement) element;
        superClass = typeElement.getSuperclass().toString();
        if (!superClass.equals(Object.class.getName())) {
            importedTypes.add(superClass);
        }
        interfaces.addAll(typeElement.getInterfaces()
                .stream()
                .map(TypeMirror::toString)
                .collect(Collectors.toList()));
        methods.addAll(ElementFilter.methodsIn(Set.of(element))
                .stream()
                .map(MethodModel::new)
                .collect(Collectors.toList()));
        /*
        for (ExecutableElement method : methods) {
            Name methodName = method.getSimpleName();
            List<? extends VariableElement> parameters = method.getParameters();
            for (VariableElement parameter : parameters) {
                TypeMirror type = parameter.asType();
                String fullTypeClassName = type.toString();
                PathVariable pathVariable = type.getAnnotation(PathVariable.class);
            }
        }*/
    }

    public String getFullQualifiedName() {
        return pkg + "." + name;
    }

    public List<String> getImportedTypes() {
        return importedTypes;
    }

    public String getName() {
        return name;
    }

    public String getPkg() {
        return pkg;
    }

    public String getSuperClass() {
        return superClass;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public List<MethodModel> getMethods() {
        return methods;
    }
}
