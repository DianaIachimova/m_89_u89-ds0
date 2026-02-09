package com.example.insurance_app.webapi.controller.broker;

import com.example.insurance_app.application.dto.PageDto;
import com.example.insurance_app.application.dto.policy.request.CancelPolicyRequest;
import com.example.insurance_app.application.dto.policy.request.CreatePolicyRequest;
import com.example.insurance_app.application.dto.policy.response.PolicyResponse;
import com.example.insurance_app.application.dto.policy.response.PolicySummaryResponse;
import com.example.insurance_app.application.service.policy.PolicyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/brokers/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PolicyResponse createDraft(@Valid @RequestBody CreatePolicyRequest request) {
        return policyService.createDraft(request);
    }

    @PostMapping("/{id}/activate")
    public PolicyResponse activate(@PathVariable UUID id) {
        return policyService.activate(id);
    }

    @PostMapping("/{id}/cancel")
    public PolicyResponse cancel(@PathVariable UUID id,
                                  @Valid @RequestBody CancelPolicyRequest request) {
        return policyService.cancel(id, request);
    }

    @GetMapping
    public PageDto<PolicySummaryResponse> list(
            @RequestParam(required = false) UUID clientId,
            @RequestParam(required = false) UUID brokerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return policyService.list(clientId, brokerId, status, startDate, endDate, pageable);
    }

    @GetMapping("/{id}")
    public PolicyResponse getById(@PathVariable UUID id) {
        return policyService.getById(id);
    }
}
