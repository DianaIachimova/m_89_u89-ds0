package com.example.insurance_app.webapi.controller.metadata;

import com.example.insurance_app.application.dto.PageDto;
import com.example.insurance_app.application.dto.metadata.currency.request.CreateCurrencyRequest;
import com.example.insurance_app.application.dto.metadata.currency.request.UpdateCurrencyStatusRequest;
import com.example.insurance_app.application.dto.metadata.currency.response.CurrencyResponse;
import com.example.insurance_app.application.service.metadata.CurrencyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/currencies")
public class CurrencyController {
    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping
    public PageDto<CurrencyResponse> listCurrencies(
            @PageableDefault(size = 20, sort = "code", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return currencyService.getAllCurrencies(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CurrencyResponse createCurrency(@Valid @RequestBody CreateCurrencyRequest request) {
        return currencyService.createCurrency(request);
    }

    @PutMapping("/{currencyId}")
    public CurrencyResponse setCurrencyActive(
            @PathVariable UUID currencyId,
            @Valid @RequestBody UpdateCurrencyStatusRequest request
    ) {
        return currencyService.updateActiveStatus(currencyId, request);
    }
}
