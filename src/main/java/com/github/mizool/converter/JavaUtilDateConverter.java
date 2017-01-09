package com.github.mizool.converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JavaUtilDateConverter
{
    public Date fromPojo(LocalDateTime pojo)
    {
        Date record = null;

        if (pojo != null)
        {
            record = Date.from(pojo.atZone(ZoneId.systemDefault()).toInstant());
        }
        return record;
    }

    public LocalDateTime toPojo(Date record)
    {
        LocalDateTime pojo = null;

        if (record != null)
        {
            pojo = LocalDateTime.ofInstant(record.toInstant(), ZoneId.systemDefault());
        }
        return pojo;
    }
}