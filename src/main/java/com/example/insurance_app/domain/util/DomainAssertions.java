package com.example.insurance_app.domain.util;

import com.example.insurance_app.domain.exception.DomainValidationException;
import com.example.insurance_app.domain.model.client.ClientType;

import java.math.BigDecimal;


public final class DomainAssertions {

    private DomainAssertions() {
    }

    public static <T> T notNull(T value, String fieldName) {
        if (value == null) {
            throw new DomainValidationException(fieldName + " cannot be null");
        }
        return  value;
    }

    public static String notBlank(String value, String fieldName) {
        if (value==null || value.isBlank()) {
            throw new DomainValidationException(fieldName + " cannot be blank");
        }
        return value;
    }


    public static String normalize(String value) {
        return value != null ? value.trim() : null;
    }


    public static String normalizeIdentificationNumber(String identificationNumber) {
        return identificationNumber != null ? identificationNumber.replaceAll("\\s+", "") : null;
    }

    public static void check(boolean condition, String message) {
        if (!condition) throw new DomainValidationException(message);
    }

    public static void validateIdentificationNumber(ClientType clientType, String identificationNumber) {
        if (clientType == ClientType.INDIVIDUAL && !isLikelyCnp(identificationNumber)) {
            throw new DomainValidationException("Individual clients must have a valid CNP");
        }

        if (clientType == ClientType.COMPANY && !isLikelyCui(identificationNumber)) {
            throw new DomainValidationException("Company clients must have a valid CUI (2-10 digits)");
        }
    }

    private static boolean isLikelyCnp(String v) {
        return v != null && v.matches("\\d{13}");
    }

    private static boolean isLikelyCui(String v) {
        return v != null && v.matches("\\d{2,10}");
    }

    public static void requireInRange(int value, int min, int max, String field) {
        check(value >= min && value <= max, field + " must be between " + min + " and " + max);
    }

    public static void requireBigDecimalFormat(BigDecimal value, int integerDigits, int fractionDigits, String field) {
        notNull(value, field);

        int scale = value.scale();
        int precision = value.precision();
        int integerPart = precision - scale;

        check(scale <= fractionDigits, field + " must have max " + fractionDigits + " decimals");
        check(integerPart <= integerDigits, field + " must have max " + integerDigits + " integer digits");
    }

    public static void requirePositive(BigDecimal value, String field) {
        check(value.compareTo(BigDecimal.ZERO) > 0, field+ "must be positive"
        );
    }



    

}
