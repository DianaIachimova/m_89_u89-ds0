package com.example.insurance_app.domain.exception;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Map;

public abstract class DomainException extends RuntimeException {

    private final HttpStatus status;
    private final String type;
    private final String title;
    private final String detail;
    private final String code;
    private final Map<String, Serializable> props;

    protected DomainException(String message,
                              HttpStatus status,
                              String type,
                              String title,
                              String detail,
                              String code,
                              java.util.Map<String, Serializable> props) {
        super(message);
        this.status = status;
        this.type = type;
        this.title = title;
        this.detail = detail;
        this.code = code;
        this.props = props == null ? java.util.Map.of() : java.util.Map.copyOf(props);
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public String getCode() {
        return code;
    }

    public Map<String, Serializable> getProps() {
        return props;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
