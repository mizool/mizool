package com.github.mizool.core.st4;

import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.misc.STMessage;

import com.github.mizool.core.exception.CodeInconsistencyException;

public class ErrorListener implements STErrorListener
{
    @Override
    public void compileTimeError(STMessage msg)
    {
        throw new CodeInconsistencyException(msg.toString(), msg.cause);
    }

    @Override
    public void runTimeError(STMessage msg)
    {
        throw new CodeInconsistencyException(msg.toString(), msg.cause);
    }

    @Override
    public void IOError(STMessage msg)
    {
        throw new CodeInconsistencyException(msg.toString(), msg.cause);
    }

    @Override
    public void internalError(STMessage msg)
    {
        throw new CodeInconsistencyException(msg.toString(), msg.cause);
    }
}

