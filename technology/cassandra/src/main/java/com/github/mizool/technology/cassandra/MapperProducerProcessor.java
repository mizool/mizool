/**
 *  Copyright 2017 incub8 Software Labs GmbH
 *  Copyright 2017 protel Hotelsoftware GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
    public synchronized void init(ProcessingEnvironment processingEnvironment)
    {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment)
    {
        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(MapperProducer.class))
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
        String annotationName = MapperProducer.class.getSimpleName();
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
        String mapperProducerClassName = classElement.getQualifiedName() + SUFFIX;
        JavaFileObject javaFileObject = filer.createSourceFile(mapperProducerClassName);
        try (Writer writer = javaFileObject.openWriter();)
        {
            writer.write(assembleStringTemplate(classElement));
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