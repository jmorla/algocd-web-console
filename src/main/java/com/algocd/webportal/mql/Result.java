package com.algocd.webportal.mql;

public class Result<V, E> {
    private final V value;
    private final E error;
    private final boolean isSuccess;

    private Result(V v, E e, boolean s) {
        this.value = v;
        this.error = e;
        this.isSuccess = s;
    }

    public static <V, E> Result<V, E> ok(V value) { return new Result<>(value, null, true); }
    public static <V, E> Result<V, E> fail(E error) { return new Result<>(null, error, false); }

    public boolean isSuccess() { return isSuccess; }
    public V getValue() { return value; }
    public E getError() { return error; }
}
