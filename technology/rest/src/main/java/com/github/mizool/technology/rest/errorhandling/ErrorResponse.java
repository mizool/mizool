package com.github.mizool.technology.rest.errorhandling;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ErrorResponse
{
    private final int statusCode;
    private final ErrorMessageDto body;
}
