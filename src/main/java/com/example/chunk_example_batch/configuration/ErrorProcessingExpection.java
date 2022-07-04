package com.example.chunk_example_batch.configuration;

public class ErrorProcessingExpection extends RuntimeException {
    public ErrorProcessingExpection() {
        super("Error While the order where processing");
    }
}
