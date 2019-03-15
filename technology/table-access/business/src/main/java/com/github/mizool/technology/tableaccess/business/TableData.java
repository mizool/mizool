package com.github.mizool.technology.tableaccess.business;

import java.util.stream.Stream;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class TableData implements AutoCloseable
{
    @NonNull
    Iterable<Column> columns;

    @NonNull
    Stream<Iterable<Cell>> rows;

    @Override
    public void close()
    {
        rows.close();
    }
}