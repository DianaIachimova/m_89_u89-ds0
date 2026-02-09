package com.example.insurance_app.webapi.controller.metadata;

import com.example.insurance_app.application.dto.PageDto;
import com.example.insurance_app.application.dto.metadata.riskfactors.request.CreateRiskFactorRequest;
import com.example.insurance_app.application.dto.metadata.riskfactors.request.RiskFactorActionRequest;
import com.example.insurance_app.application.dto.metadata.riskfactors.request.UpdateRiskFactorPercentageRequest;
import com.example.insurance_app.application.dto.metadata.riskfactors.response.RiskFactorResponse;
import com.example.insurance_app.application.service.metadata.RiskFactorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/risk-factors")
public class RiskFactorController {

    private final RiskFactorService riskFactorService;

    public RiskFactorController(RiskFactorService riskFactorService) {
        this.riskFactorService = riskFactorService;
    }

    @GetMapping
    public PageDto<RiskFactorResponse> listRiskFactors(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return riskFactorService.listRiskFactors(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RiskFactorResponse create(@Valid @RequestBody CreateRiskFactorRequest request) {
        return riskFactorService.create(request);
    }

    @PutMapping("/{id}")
    public RiskFactorResponse updatePercentage(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRiskFactorPercentageRequest request
    ) {
        return riskFactorService.updatePercentage(id, request);
    }

    @PostMapping("/{id}/actions")
    public RiskFactorResponse executeAction(
            @PathVariable UUID id,
            @Valid @RequestBody RiskFactorActionRequest request
    ) {
        return riskFactorService.executeAction(id, request);
    }

}
