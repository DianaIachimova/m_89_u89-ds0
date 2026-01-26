package com.example.insurance_app.webapi.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;

public final class ProblemDetailsFactory {
    private ProblemDetailsFactory() {}

    public static ProblemDetail of(
            HttpStatus status,
            String type,
            String title,
            String detail,
            String instance,
            String code,
            Map<String, Object> props

    ){
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setType(URI.create(type));
        pd.setTitle(title);
        pd.setDetail(detail);
        pd.setInstance(URI.create(instance));
        pd.setProperty("timestamp", OffsetDateTime.now().toString());

        if(code != null && !code.isBlank()) {
            pd.setProperty("code", code);
        }

        if(props!=null && !props.isEmpty()){
            props.forEach(pd::setProperty);
        }

        return pd;

    }
}
