package com.github.mizool.technology.jdbc.business;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Cell
{
    @NonNull
    Column column;

    Object value;
}