package com.example.insurance_app.webapi.controller;

import com.example.insurance_app.application.dto.building.request.CreateBuildingRequest;
import com.example.insurance_app.application.dto.building.request.UpdateBuildingRequest;
import com.example.insurance_app.application.dto.building.response.BuildingResponse;
import com.example.insurance_app.application.service.BuildingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/brokers")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping("/clients/{clientId}/buildings")
    public List<BuildingResponse> getBuildingsByClient(@PathVariable UUID clientId) {
        return buildingService.getBuildingsByClientId(clientId);
    }

    @GetMapping("/buildings/{buildingId}")
    public BuildingResponse getBuildingById(@PathVariable UUID buildingId) {
        return buildingService.getBuildingById(buildingId);
    }

    @PostMapping("/clients/{clientId}/buildings")
    @ResponseStatus(HttpStatus.CREATED)
    public BuildingResponse createBuilding(
            @PathVariable UUID clientId,
            @Valid @RequestBody CreateBuildingRequest request
    ) {
        return buildingService.createBuilding(clientId, request);
    }

    @PutMapping("/buildings/{buildingId}")
    public BuildingResponse updateBuilding(
            @PathVariable UUID buildingId,
            @Valid @RequestBody UpdateBuildingRequest request
    ) {
        return buildingService.updateBuilding(buildingId, request);
    }
}
