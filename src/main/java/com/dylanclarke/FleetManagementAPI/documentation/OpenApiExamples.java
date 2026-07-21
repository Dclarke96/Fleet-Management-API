package com.dylanclarke.FleetManagementAPI.documentation;


public class OpenApiExamples {


    public static final String UNAUTHORIZED = """
    {
      "status": 401,
      "error": "Unauthorized",
      "message": "Invalid authentication credentials",
      "path": "/api/auth/login",
      "timestamp": "2026-07-16T23:18:11",
      "traceId": "example-trace-id",
      "fieldErrors": null
    }
    """;


    public static final String VALIDATION_ERROR = """
    {
      "status": 400,
      "error": "Invalid Request",
      "message": "Request validation failed",
      "path": "/api/auth/login",
      "timestamp": "2026-07-16T23:18:11",
      "traceId": "example-trace-id",
      "fieldErrors": [
        {
          "field": "username",
          "message": "Username cannot be blank",
          "rejectedValue": ""
        }
      ]
    }
    """;


    public static final String NOT_FOUND = """
    {
      "status": 404,
      "error": "Resource Not Found",
      "message": "Vehicle not found",
      "path": "/api/vehicles/1",
      "timestamp": "2026-07-16T23:18:11",
      "traceId": "example-trace-id",
      "fieldErrors": null
    }
    """;
}