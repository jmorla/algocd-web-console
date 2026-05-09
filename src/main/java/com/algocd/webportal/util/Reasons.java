package com.algocd.webportal.util;

import com.algocd.webportal.exceptions.AlgocdException;
import com.algocd.webportal.exceptions.ErrorReason;

public class Reasons {

    public static <T> Result<T> illegalArg(String message) {
        return Result.failure(new IllegalArgumentException(message));
    }

    public static <T> Result<T> internalFailure(String message) {
        return Result.failure(new AlgocdException(ErrorReason.INTERNAL_SERVER_ERROR, message));
    }

    public static <T> Result<T> internalFailure(String message, Throwable ex) {
        return Result.failure(new AlgocdException(ErrorReason.INTERNAL_SERVER_ERROR, message, ex));
    }
}
