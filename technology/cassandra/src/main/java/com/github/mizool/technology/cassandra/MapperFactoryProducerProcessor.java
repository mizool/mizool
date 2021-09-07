package com.github.mizool.technology.cassandra;

import javax.annotation.processing.Processor;

import org.kohsuke.MetaInfServices;

@MetaInfServices(value = Processor.class)
public class MapperFactoryProducerProcessor extends AbstractTemplateBasedProcessor
{
    public MapperFactoryProducerProcessor()
    {
        super(MapperFactoryProducer.class, "mapperFactoryProducerGenerator.stg");
    }
}
