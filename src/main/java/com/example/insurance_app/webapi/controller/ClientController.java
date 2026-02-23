package com.example.insurance_app.webapi.controller;

import org.springframework.web.bind.annotation.*;
import com.example.insurance_app.application.dto.PageDto;
import com.example.insurance_app.application.dto.client.request.CreateClientRequest;
import com.example.insurance_app.application.dto.client.request.UpdateClientRequest;
import com.example.insurance_app.application.dto.client.response.ClientResponse;
import com.example.insurance_app.application.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@RestController
@RequestMapping("/api/brokers/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public PageDto<ClientResponse> searchClients(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String identificationNumber,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return clientService.searchClients(name, identificationNumber, pageable);
    }

    @GetMapping("/{clientId}")
    public ClientResponse getClientById(@PathVariable UUID clientId) {
        return clientService.getClientById(clientId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse createClient(@Valid @RequestBody CreateClientRequest request) {
        return clientService.createClient(request);
    }

    @PutMapping("/{clientId}")
    public ClientResponse updateClient(
            @PathVariable UUID clientId,
            @Valid @RequestBody UpdateClientRequest request
    ) {
        return clientService.updateClient(clientId, request);
    }
}
