package com.example.insurance_app.webapi.controller.admin;

import com.example.insurance_app.application.dto.PageDto;
import com.example.insurance_app.application.dto.broker.request.CreateBrokerRequest;
import com.example.insurance_app.application.dto.broker.request.UpdateBrokerRequest;
import com.example.insurance_app.application.dto.broker.response.BrokerResponse;
import com.example.insurance_app.application.service.broker.BrokerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/brokers")
public class BrokerController {

    private final BrokerService brokerService;

    public BrokerController(BrokerService brokerService) {
        this.brokerService = brokerService;
    }

    @GetMapping
    public PageDto<BrokerResponse> list(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return brokerService.list(pageable);
    }

    @GetMapping("/{id}")
    public BrokerResponse getById(@PathVariable UUID id) {
        return brokerService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BrokerResponse create(@Valid @RequestBody CreateBrokerRequest request) {
        return brokerService.create(request);
    }

    @PutMapping("/{id}")
    public BrokerResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBrokerRequest request
    ) {
        return brokerService.update(id, request);
    }

    @PostMapping("/{id}/activate")
    public BrokerResponse activate(@PathVariable UUID id) {
        return brokerService.activate(id);
    }

    @PostMapping("/{id}/deactivate")
    public BrokerResponse deactivate(@PathVariable UUID id) {
        return brokerService.deactivate(id);
    }
}
