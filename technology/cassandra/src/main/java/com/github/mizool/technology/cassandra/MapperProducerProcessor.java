package com.github.mizool.technology.cassandra;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

import org.kohsuke.MetaInfServices;
import org.stringtemplate.v4.ST;

import com.github.mizool.core.exception.CodeInconsistencyException;
import com.github.mizool.core.st4.TemplateLoader;
import com.google.common.collect.ImmutableSet;

@MetaInfServices(value = Processor.class)
public class MapperProducerProcessor extends AbstractProcessor
{
    private static final String SUFFIX = "MapperProducer";
    private Elements elementUtils;
    private Filer filer;

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        return ImmutableSet.of(MapperProducer.class.getCanonicalName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(MapperProducer.class))
        {
            verifyElement(annotatedElement);
            generateCode((TypeElement) annotatedElement, filer);
        }
        return true;
    }

    private void verifyElement(Element annotatedElement)
    {
        String annotationName = MapperProducer.class.getSimpleName();
        if (annotatedElement.getKind() != ElementKind.CLASS)
        {
            throw new CodeInconsistencyException("Only classes can be annotated with " + annotationName);
        }
        TypeElement classElement = (TypeElement) annotatedElement;
        if (!classElement.getModifiers().contains(Modifier.PUBLIC))
        {
            throw new CodeInconsistencyException("Only public classes can be annotated with " + annotationName);
        }
        if (classElement.getModifiers().contains(Modifier.ABSTRACT))
        {
            throw new CodeInconsistencyException("Abstract classes can't be annotated with " + annotationName);
        }
    }

    private void generateCode(TypeElement classElement, Filer filer)
    {
        String mapperProducerClassName = classElement.getQualifiedName() + SUFFIX;

        try
        {
            JavaFileObject javaFileObject = filer.createSourceFile(mapperProducerClassName);
            Writer writer = javaFileObject.openWriter();
            writer.write(assembleStringTemplate(classElement));
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    private String assembleStringTemplate(TypeElement classElement)
    {
        TemplateLoader templateLoader = new TemplateLoader();
        ST template = templateLoader.getGroupTemplate("mapperProducerGenerator.stg", getClass());
        String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
        String className = classElement.getSimpleName().toString();
        template.add("packageName", packageName);
        template.add("className", className);
        return template.render();
    }
}