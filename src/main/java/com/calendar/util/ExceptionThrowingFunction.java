package com.calendar.util;

public interface ExceptionThrowingFunction<T, R, E extends Exception> {
    R apply(T input) throws E;
}