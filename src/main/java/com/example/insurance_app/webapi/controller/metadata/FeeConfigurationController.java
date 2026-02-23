package com.example.insurance_app.webapi.controller.metadata;

import org.springframework.web.bind.annotation.*;
import com.example.insurance_app.application.dto.PageDto;
import com.example.insurance_app.application.dto.metadata.feeconfig.request.CreateFeeConfigRequest;
import com.example.insurance_app.application.dto.metadata.feeconfig.request.UpdateFeeConfigRequest;
import com.example.insurance_app.application.dto.metadata.feeconfig.response.FeeConfigResponse;
import com.example.insurance_app.application.service.metadata.FeeConfigurationService;
import com.example.insurance_app.application.service.metadata.FeeConfigurationUpdateService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/fees")
public class FeeConfigurationController {
    private final FeeConfigurationService feeConfigurationService;
    private final FeeConfigurationUpdateService feeUpdateService;

    public FeeConfigurationController(FeeConfigurationService feeConfigurationService, FeeConfigurationUpdateService feeUpdateService) {
        this.feeConfigurationService = feeConfigurationService;
        this.feeUpdateService = feeUpdateService;
    }

    @GetMapping
    public PageDto<FeeConfigResponse> listFeeConfigurations(
            @PageableDefault(size = 20, sort = "code", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return feeConfigurationService.listFeeConfigurations(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeeConfigResponse createFeeConfiguration(@Valid @RequestBody CreateFeeConfigRequest request) {
        return feeConfigurationService.create(request);
    }

    @PutMapping("/{id}")
    public FeeConfigResponse updateFeeConfiguration(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFeeConfigRequest request
    ) {
        return feeUpdateService.update(id, request);
    }

    @PostMapping("/{id}/deactivate")
    public FeeConfigResponse deactivateFeeConfiguration(@PathVariable UUID id) {
        return feeUpdateService.deactivate(id);
    }

}
