package com.calendar.util;

public interface ExceptionThrowingConsumer<T, E extends Exception> {
    void consume(T t) throws E;
}
