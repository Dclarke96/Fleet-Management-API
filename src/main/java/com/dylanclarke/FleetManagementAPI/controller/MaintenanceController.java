package com.dylanclarke.FleetManagementAPI.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dylanclarke.FleetManagementAPI.api.ApiResponse;
import com.dylanclarke.FleetManagementAPI.api.PageResponse;
import com.dylanclarke.FleetManagementAPI.dto.ErrorResponse;
import com.dylanclarke.FleetManagementAPI.dto.MaintenanceRequestDTO;
import com.dylanclarke.FleetManagementAPI.dto.MaintenanceResponseDTO;
import com.dylanclarke.FleetManagementAPI.service.MaintenanceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@Tag(
        name = "Maintenance",
        description = "Endpoints for managing vehicle maintenance records within the authenticated user's company."
)
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;


    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }



    // ----------------------------------------
    // GET ALL
    // ----------------------------------------

    @Operation(
            summary = "Retrieve all maintenance records",
            description = "Returns a paginated list of maintenance records for the authenticated user's company."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Maintenance records retrieved successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiResponse.class
                            )
                    )
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MaintenanceResponseDTO>>> getAllMaintenance(
            Pageable pageable) {

        Page<MaintenanceResponseDTO> page =
                maintenanceService.getAllMaintenance(pageable);

        PageResponse<MaintenanceResponseDTO> pageResponse =
                new PageResponse<>(page);

        return ResponseEntity.ok(
                ApiResponse.success(
                        pageResponse,
                        "Maintenance records retrieved successfully"
                )
        );
    }



    // ----------------------------------------
    // GET BY ID
    // ----------------------------------------

    @Operation(
            summary = "Retrieve a maintenance record by ID",
            description = "Returns a single maintenance record belonging to the authenticated user's company."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Maintenance record retrieved successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiResponse.class
                            )
                    )
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaintenanceResponseDTO>> getMaintenanceById(
            @PathVariable Long id) {

        MaintenanceResponseDTO response =
                maintenanceService.getMaintenanceById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Maintenance retrieved successfully"
                )
        );
    }



    // ----------------------------------------
    // GET BY VEHICLE
    // ----------------------------------------

    @Operation(
            summary = "Retrieve maintenance for a vehicle",
            description = "Returns a paginated list of maintenance records associated with a specific vehicle."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Maintenance records retrieved successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiResponse.class
                            )
                    )
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse<PageResponse<MaintenanceResponseDTO>>> getByVehicle(
            @PathVariable Long vehicleId,
            Pageable pageable) {

        Page<MaintenanceResponseDTO> page =
                maintenanceService.getMaintenanceForVehicle(vehicleId, pageable);

        PageResponse<MaintenanceResponseDTO> pageResponse =
                new PageResponse<>(page);


        return ResponseEntity.ok(
                ApiResponse.success(
                        pageResponse,
                        "Maintenance records retrieved successfully"
                )
        );
    }



    // ----------------------------------------
    // CREATE
    // ----------------------------------------

    @Operation(
            summary = "Create a maintenance record",
            description = "Creates a new maintenance record for a vehicle owned by the authenticated user's company."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Maintenance record created successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiResponse.class
                            )
                    )
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid maintenance data",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<MaintenanceResponseDTO>> createMaintenance(
            @Valid @RequestBody MaintenanceRequestDTO requestDTO) {

        MaintenanceResponseDTO response =
                maintenanceService.addMaintenance(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                response,
                                "Maintenance created successfully"
                        )
                );
    }



    // ----------------------------------------
    // UPDATE
    // ----------------------------------------

    @Operation(
            summary = "Update a maintenance record",
            description = "Updates an existing maintenance record belonging to the authenticated user's company."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Maintenance record updated successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiResponse.class
                            )
                    )
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid maintenance data",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MaintenanceResponseDTO>> updateMaintenance(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceRequestDTO requestDTO) {

        MaintenanceResponseDTO response =
                maintenanceService.updateMaintenance(id, requestDTO);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Maintenance updated successfully"
                )
        );
    }



    // ----------------------------------------
    // DELETE
    // ----------------------------------------

    @Operation(
            summary = "Delete a maintenance record",
            description = "Deletes a maintenance record belonging to the authenticated user's company."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Maintenance record deleted successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiResponse.class
                            )
                    )
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Authentication required",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMaintenance(
            @PathVariable Long id) {

        maintenanceService.deleteMaintenance(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Maintenance deleted successfully"
                )
        );
    }
}