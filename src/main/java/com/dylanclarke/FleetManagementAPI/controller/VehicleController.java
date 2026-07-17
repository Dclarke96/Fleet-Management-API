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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dylanclarke.FleetManagementAPI.api.ApiResponse;
import com.dylanclarke.FleetManagementAPI.api.PageResponse;
import com.dylanclarke.FleetManagementAPI.dto.ErrorResponse;
import com.dylanclarke.FleetManagementAPI.dto.VehicleRequestDTO;
import com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO;
import com.dylanclarke.FleetManagementAPI.service.VehicleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@Tag(
        name = "Vehicles",
        description = "Endpoints for managing vehicles belonging to the authenticated user's company."
)
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService service;


    public VehicleController(VehicleService service) {
        this.service = service;
    }


    // ----------------------------------------
    // GET ALL
    // ----------------------------------------

    @Operation(
            summary = "Retrieve all vehicles",
            description = "Returns a paginated list of vehicles for the authenticated user's company."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Vehicles retrieved successfully",
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
    public ResponseEntity<ApiResponse<PageResponse<VehicleResponseDTO>>> getAllVehicles(
            Pageable pageable) {

        Page<VehicleResponseDTO> page = service.getAllVehicles(pageable);

        PageResponse<VehicleResponseDTO> pageResponse =
                new PageResponse<>(page);

        return ResponseEntity.ok(
                ApiResponse.success(
                        pageResponse,
                        "Vehicles retrieved successfully"
                )
        );
    }



    // ----------------------------------------
    // GET BY ID
    // ----------------------------------------

    @Operation(
            summary = "Retrieve a vehicle by ID",
            description = "Returns a single vehicle owned by the authenticated user's company."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Vehicle retrieved successfully",
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
    public ResponseEntity<ApiResponse<VehicleResponseDTO>> getVehicleById(
            @PathVariable Long id) {

        VehicleResponseDTO dto =
                service.getVehicleById(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        dto,
                        "Vehicle retrieved successfully"
                )
        );
    }



    // ----------------------------------------
    // SEARCH
    // ----------------------------------------

    @Operation(
            summary = "Search vehicles",
            description = "Searches vehicles by supported vehicle criteria."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully",
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
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<VehicleResponseDTO>>> searchVehicles(
            @RequestParam("q") String query,
            Pageable pageable) {

        Page<VehicleResponseDTO> page =
                service.searchVehicles(query, pageable);

        PageResponse<VehicleResponseDTO> pageResponse =
                new PageResponse<>(page);


        return ResponseEntity.ok(
                ApiResponse.success(
                        pageResponse,
                        "Vehicles search results retrieved successfully"
                )
        );
    }



    // ----------------------------------------
    // CREATE
    // ----------------------------------------

    @Operation(
            summary = "Create a vehicle",
            description = "Creates a new vehicle for the authenticated user's company."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Vehicle created successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiResponse.class
                            )
                    )
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid vehicle data",
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
    public ResponseEntity<ApiResponse<VehicleResponseDTO>> createVehicle(
            @Valid @RequestBody VehicleRequestDTO request) {

        VehicleResponseDTO created =
                service.addVehicle(request);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                created,
                                "Vehicle created successfully"
                        )
                );
    }



    // ----------------------------------------
    // UPDATE
    // ----------------------------------------

    @Operation(
            summary = "Update a vehicle",
            description = "Updates an existing vehicle belonging to the authenticated user's company."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Vehicle updated successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiResponse.class
                            )
                    )
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid vehicle data",
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
    public ResponseEntity<ApiResponse<VehicleResponseDTO>> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequestDTO request) {


        VehicleResponseDTO updated =
                service.updateVehicle(id, request);


        return ResponseEntity.ok(
                ApiResponse.success(
                        updated,
                        "Vehicle updated successfully"
                )
        );
    }



    // ----------------------------------------
    // DELETE
    // ----------------------------------------

    @Operation(
            summary = "Delete a vehicle",
            description = "Deletes a vehicle belonging to the authenticated user's company."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Vehicle deleted successfully",
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
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(
            @PathVariable Long id) {

        service.deleteVehicle(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Vehicle deleted successfully"
                )
        );
    }
}