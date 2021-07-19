package com.github.mizool.technology.cassandra;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.stringtemplate.v4.ST;

import com.github.mizool.core.st4.TemplateLoader;
import com.google.common.collect.ImmutableSet;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class AbstractTemplateBasedProcessor extends AbstractProcessor
{
    private final Class<? extends Annotation> annotationClass;
    private final String templateName;

    private Elements elementUtils;
    private Filer filer;

    @Override
    public final Set<String> getSupportedAnnotationTypes()
    {
        return ImmutableSet.of(annotationClass.getCanonicalName());
    }

    @Override
    public final synchronized void init(ProcessingEnvironment processingEnvironment)
    {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment)
    {
        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(annotationClass))
        {
            verifyElement(annotatedElement);
            try
            {
                generateCode((TypeElement) annotatedElement);
            }
            catch (IOException e)
            {
                throw new UncheckedIOException(e);
            }
        }
        return true;
    }

    private void verifyElement(Element annotatedElement)
    {
        String annotationName = annotationClass.getSimpleName();
        if (annotatedElement.getKind() != ElementKind.CLASS)
        {
            throw new IllegalArgumentException("Only classes can be annotated with " + annotationName);
        }
        TypeElement classElement = (TypeElement) annotatedElement;
        if (!classElement.getModifiers().contains(Modifier.PUBLIC))
        {
            throw new IllegalArgumentException("Only public classes can be annotated with " + annotationName);
        }
        if (classElement.getModifiers().contains(Modifier.ABSTRACT))
        {
            throw new IllegalArgumentException("Abstract classes can't be annotated with " + annotationName);
        }
    }

    private void generateCode(TypeElement classElement) throws IOException
    {
        String className = classElement.getQualifiedName() + annotationClass.getSimpleName();
        JavaFileObject javaFileObject = filer.createSourceFile(className);
        try (Writer writer = javaFileObject.openWriter())
        {
            writer.write(assembleStringTemplate(classElement));
        }
    }

    private String assembleStringTemplate(TypeElement classElement)
    {
        TemplateLoader templateLoader = new TemplateLoader();
        ST template = templateLoader.getGroupTemplate(templateName, getClass());
        String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
        String className = classElement.getSimpleName().toString();
        template.add("packageName", packageName);
        template.add("className", className);
        return template.render();
    }
}
