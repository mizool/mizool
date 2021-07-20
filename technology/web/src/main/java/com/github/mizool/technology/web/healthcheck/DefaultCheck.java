package com.github.mizool.technology.web.healthcheck;

class DefaultCheck implements Check
{
    @Override
    public CheckResult perform()
    {
        return CheckResult.builder().name("default").success(true).message("OK").build();
    }
}
