package io.mu.restapi.codegen;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.mu.restapi.annotations.ApiGen;
import io.mu.restapi.annotations.Path;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@SupportedOptions({ "codegen.output" })
//@SupportedSourceVersion(SourceVersion.RELEASE_9)
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
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Path.class);
        elements.forEach(System.out::println);
        System.out.println("???");
        //CodegenRouteProcessor processor = new CodegenRouteProcessor(elements);
        //Set<Endpoint> endpoints = processor.getEndpoints();
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
