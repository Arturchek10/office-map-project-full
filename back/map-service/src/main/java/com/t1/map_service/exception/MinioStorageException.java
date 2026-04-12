package com.t1.map_service.exception;

public class MinioStorageException extends RuntimeException {
    public MinioStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
