package com.github.mizool.technology.cassandra;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

import org.kohsuke.MetaInfServices;

@SupportedSourceVersion(SourceVersion.RELEASE_11)
@MetaInfServices(value = Processor.class)
public class MapperFactoryProducerProcessor extends AbstractTemplateBasedProcessor
{
    public MapperFactoryProducerProcessor()
    {
        super(MapperFactoryProducer.class, "mapperFactoryProducerGenerator.stg");
    }
}
