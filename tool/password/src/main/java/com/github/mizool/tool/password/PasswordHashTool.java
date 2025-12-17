package com.github.mizool.tool.password;

import java.util.ServiceLoader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.github.mizool.core.password.PasswordHasher;

@Slf4j
@RequiredArgsConstructor
public class PasswordHashTool
{
    public static void main(String[] args)
    {
        PasswordHashToolParameters parameters = new PasswordHashToolParameters();
        JCommander jCommander = new JCommander(parameters);
        jCommander.setProgramName(PasswordHashTool.class.getName());
        try
        {
            jCommander.parse(args);

            if (parameters.isHelp())
            {
                jCommander.usage();
            }
            else
            {
                Iterable<PasswordHasher> instances = ServiceLoader.load(PasswordHasher.class);
                instances.forEach(hasher -> printDigest(hasher, parameters.getPassword()));
            }
        }
        catch (ParameterException e)
        {
            log.debug("Invalid parameter", e);
            jCommander.usage();
        }
    }

    static void printDigest(PasswordHasher hasher, char[] cleartextPassword)
    {
        String digest = hasher.hashPassword(cleartextPassword);
        log.info("Algorithm: {}  Digest: {}", hasher.getAlgorithmName(), digest);
    }
}
