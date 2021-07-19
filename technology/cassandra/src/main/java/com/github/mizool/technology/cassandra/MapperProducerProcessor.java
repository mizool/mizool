package com.github.mizool.technology.cassandra;

import javax.annotation.processing.Processor;

import org.kohsuke.MetaInfServices;

@MetaInfServices(value = Processor.class)
public class MapperProducerProcessor extends AbstractTemplateBasedProcessor
{
    public MapperProducerProcessor()
    {
        super(MapperProducer.class, "mapperProducerGenerator.stg");
    }
}
