package com.algocd.webportal.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Result <T> {
    private final T value;
    private final Throwable error;
    private final boolean success;

    private Result(T value, Throwable error, boolean success) {
        this.value = value;
        this.error = error;
        this.success = success;
    }

    public static <T, E extends Throwable> Result<T> success(T value) {
        return new Result<>(Objects.requireNonNull(value), null, true);
    }

    public static <T, E extends Throwable> Result<T> failure(E error) {
        return new Result<>(null, Objects.requireNonNull(error), false);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public T getValue() {
        if (!success) {
            throw new IllegalStateException("Cannot get value from a failure result");
        }
        return value;
    }

    public Throwable getError() {
        if (success) {
            throw new IllegalStateException("Cannot get error from a success result");
        }
        return error;
    }

    public <U> Result<U> map(Function<? super T, ? extends U> mapper) {
        if (success) {
            return Result.success(mapper.apply(value));
        } else {
            return Result.failure(error);
        }
    }

    public void ifSuccess(Consumer<? super T> action) {
        if (success) {
            action.accept(value);
        }
    }

    public void ifFailure(Consumer<Throwable> action) {
        if (!success) {
            action.accept(error);
        }
    }
}
