package com.kabutar.keyfort.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ResponseHandler {

    private HttpStatus status;
    private List<String> error = Collections.emptyList();
    private List<String> message = Collections.emptyList();
    private List<Object> data = Collections.emptyList();

    public ResponseHandler status(HttpStatus status) {
        this.status = status;
        return this;
    }

    public ResponseHandler error(List<String> error) {
        this.error = error;
        return this;
    }

    public ResponseHandler message(List<String> message) {
        this.message = message;
        return this;
    }

    public ResponseHandler data(List<Object> data) {
        this.data = data;
        return this;
    }

    public ResponseEntity<?> build() {
        return ResponseEntity.status(status).body(Map.of(
                "error", error,
                "message", message,
                "data", data
        ));
    }
}
