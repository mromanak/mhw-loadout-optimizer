package com.mromanak.loadoutoptimizer.controller;

import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.core.NestedRuntimeException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(NestedRuntimeException.class)
    public void handleExceptionWithRootCause(NestedRuntimeException e) throws Throwable {
        if (e.getRootCause() == null) {
            throw new Exception("A nested runtime exception with no root cause was thrown");
        }
        throw e.getRootCause();
    }

}
