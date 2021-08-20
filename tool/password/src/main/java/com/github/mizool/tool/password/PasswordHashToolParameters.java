package com.github.mizool.tool.password;

import java.util.List;

import lombok.Getter;

import com.beust.jcommander.Parameter;

class PasswordHashToolParameters
{
    @Parameter(names = "-help", description = "Displays this help screen", help = true)
    @Getter
    private boolean help;

    @Parameter(description = "password", required = true)
    private List<String> password;

    public char[] getPassword()
    {
        return password.get(0)
            .toCharArray();
    }
}
