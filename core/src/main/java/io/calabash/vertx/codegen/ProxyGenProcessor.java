package io.calabash.vertx.codegen;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.calabash.vertx.annotation.ApiGen;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private CompiledTemplate proxyTemplate;

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
        proxyTemplate = TemplateCompiler.compileTemplate("proxy.templ");
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

        Map<String, String> proxyOutputMap = annotatedClasses
                .stream()
                .map(ClassModel::new)
                .collect(Collectors.toMap(ClassModel::getFullQualifiedName, this::generateProxy));
        createOutputFolder();
        proxyOutputMap.forEach(this::writeProxyClasses);
        return false;
    }

    private String generateProxy(ClassModel classModel) {
        Map<String, Object> templateParams = Map.of("classModel", classModel);
        return (String) TemplateRuntime.execute(proxyTemplate, templateParams);
    }

    private void writeProxyClasses(String fqn, String proxyClass) {
        try {
            String fqnPath = fqn.replace(".", File.separator);
            Path pkgPath = Paths.get(fqn.substring(0, fqn.lastIndexOf(File.separator)));
            if (!Files.exists(pkgPath)) {
                Files.createDirectories(pkgPath);
            }
            Path proxyPath = Paths.get(fqnPath);
            if (Files.exists(proxyPath)) {
                Files.delete(proxyPath);
            }
            Files.write(proxyPath, proxyClass.getBytes());
        } catch (IOException e) {
            log.error(e);
        }
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
