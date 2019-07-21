package com.github.mizool.core.validation.dto;

import java.time.ZonedDateTime;

public interface Dto
{
    String getId();

    default ZonedDateTime getTimestamp()
    {
        return null;
    }

    default ZonedDateTime getCreationTimestamp()
    {
        return null;
    }

    default ZonedDateTime getModificationTimestamp()
    {
        return null;
    }
}
