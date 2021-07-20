package com.github.mizool.core.st4;

import java.util.regex.Pattern;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import com.github.mizool.core.exception.CodeInconsistencyException;

public class TemplateLoader
{
    private static final Pattern DOT = Pattern.compile("\\.");

    public ST getGroupTemplate(String groupFileName, Class<?> generatorClass)
    {
        String generatorPackage = generatorClass.getPackage().getName();
        String fileNameWithPath = assembleResourcePath(groupFileName, generatorPackage);
        STGroupFile groupFile = createGroupFile(fileNameWithPath);

        return getMainTemplate(groupFile);
    }

    private String assembleResourcePath(String groupFileName, String generatorPackage)
    {
        String resourcePath = DOT.matcher(generatorPackage).replaceAll("/");
        return resourcePath + "/" + groupFileName;
    }

    private STGroupFile createGroupFile(String fileNameWithPath)
    {
        STGroupFile groupFile = new STGroupFile(fileNameWithPath);
        groupFile.setListener(new ErrorListener());
        groupFile.registerRenderer(String.class, new MizoolStringRenderer());
        return groupFile;
    }

    private ST getMainTemplate(STGroupFile groupFile)
    {
        String mainTemplateName = groupFile.getName();
        ST mainTemplate = groupFile.getInstanceOf(mainTemplateName);
        if (mainTemplate == null)
        {
            throw new CodeInconsistencyException("Main template named '" + mainTemplateName + "' not found");
        }
        return mainTemplate;
    }
}
