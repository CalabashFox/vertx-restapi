package io.mu.restapi.codegen;

import io.mu.restapi.Endpoint;
import io.mu.restapi.annotations.PathVariable;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.mu.restapi.annotations.ApiGen;
import io.mu.restapi.annotations.Path;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SupportedOptions({ "apigen.output" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ProxyGenProcessor extends AbstractProcessor {

    private static final Logger log = LoggerFactory.getLogger(ProxyGenProcessor.class);

    private File outputDirectory;
    private Map<String, String> generatedProxies = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Stream.of(
                ApiGen.class
        ).map(Class::getName).collect(Collectors.toSet());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        System.out.println("???");
        generatedProxies.clear();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<? extends Element> annotatedClasses = roundEnv.getElementsAnnotatedWith(ApiGen.class)
                .stream()
                .filter(e -> e.getKind() == ElementKind.PACKAGE)
                .map(e -> (PackageElement) e)
                .map(PackageElement::getEnclosedElements)
                .flatMap(List::stream)
                .filter(e -> e.getKind() == ElementKind.CLASS)
                .collect(Collectors.toList());
        List<ClassModel> classModels = annotatedClasses.stream().map(ClassModel::new).collect(Collectors.toList());
        classModels.forEach(System.out::println);
        return false;
    }

    private void createOutputFolder() {
        String outputDirectoryOption = processingEnv.getOptions().get("codegen.output");
        if (outputDirectoryOption == null) {
            outputDirectoryOption = processingEnv.getOptions().get("outputDirectory");
            if (outputDirectoryOption != null) {
                log.warn("Please use 'codegen.output' option instead of 'outputDirectory' option");
            }
        }
        if (outputDirectoryOption != null) {
            outputDirectory = new File(outputDirectoryOption);
            if (!outputDirectory.exists()) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Output directory " + outputDirectoryOption + " does not exist");
            }
            if (!outputDirectory.isDirectory()) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Output directory " + outputDirectoryOption + " is not a directory");
            }
        }
    }
}
