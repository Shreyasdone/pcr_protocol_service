# Section A - API Design (15 Marks)
## Sciverse PCR Protocol Management Service

This document provides the complete API specification for managing PCR Protocols and Run execution lifecycles in accordance with REST principles, robust input validation, and standardized error handling.

---

## 1. Design Principles & Conventions

- **Base Path:** `/api/v1`
- **Content-Type:** `application/json`
- **Versioning Strategy:** URI Path Versioning (`/api/v1/`) to guarantee backward compatibility and explicit API contract governance.
- **Authentication Strategy:** JWT Bearer Token passed via HTTP `Authorization: Bearer <token>` header. Token claims include `sub` (User UUID) and `roles` (`LAB_TECHNICIAN`, `ADMIN`).
- **Standardized Error Envelope:** Every API failure returns a uniform JSON structure across all endpoints:
  ```json
  {
    "timestamp": "2026-07-31T01:25:00Z",
    "status": 422,
    "error": "Unprocessable Entity",
    "message": "Validation failed",
    "details": [
      { "field": "cycleCount", "issue": "must be between 1 and 60" }
    ]
  }
  ```

---

## 2. Protocol Management Endpoints

### 2.1 Create Protocol
Creates a new PCR temperature-cycling program.

- **Endpoint:** `POST /api/v1/protocols`
- **Authentication:** Required (`Bearer JWT`)
- **Request Headers:**
  - `Content-Type: application/json`
- **Request Payload:**
  ```json
  {
    "name": "Standard Taq PCR Protocol",
    "description": "Standard 30-cycle genomic DNA amplification program",
    "steps": [
      {
        "name": "Initial Denaturation",
        "targetTemperatureCelsius": 95.0,
        "holdTimeSeconds": 180
      },
      {
        "name": "Denaturation",
        "targetTemperatureCelsius": 95.0,
        "holdTimeSeconds": 30
      },
      {
        "name": "Annealing",
        "targetTemperatureCelsius": 58.5,
        "holdTimeSeconds": 30
      },
      {
        "name": "Extension",
        "targetTemperatureCelsius": 72.0,
        "holdTimeSeconds": 60
      }
    ],
    "cycleCount": 30,
    "rampRate": 3.5
  }
  ```
- **Validation Constraints:**
  - `name`: Required, non-blank, max 255 characters.
  - `steps`: Non-empty list (minimum 1 step required).
  - `steps[].name`: Required, non-blank.
  - `steps[].targetTemperatureCelsius`: Required decimal, range `[4.0, 99.0]` °C.
  - `steps[].holdTimeSeconds`: Required integer, range `[1, 3600]` seconds.
  - `cycleCount`: Required integer, range `[1, 60]`.
  - `rampRate`: Required decimal, range `[0.1, 6.0]` °C/s.
- **Success Response (201 Created):**
  ```json
  {
    "id": "e49c3391-c868-4c8e-b618-1f7f635f4de4",
    "name": "Standard Taq PCR Protocol",
    "description": "Standard 30-cycle genomic DNA amplification program",
    "steps": [
      {
        "name": "Initial Denaturation",
        "targetTemperatureCelsius": 95.0,
        "holdTimeSeconds": 180
      },
      {
        "name": "Denaturation",
        "targetTemperatureCelsius": 95.0,
        "holdTimeSeconds": 30
      },
      {
        "name": "Annealing",
        "targetTemperatureCelsius": 58.5,
        "holdTimeSeconds": 30
      },
      {
        "name": "Extension",
        "targetTemperatureCelsius": 72.0,
        "holdTimeSeconds": 60
      }
    ],
    "cycleCount": 30,
    "rampRate": 3.5,
    "status": "ACTIVE",
    "version": 0,
    "createdAt": "2026-07-31T01:25:00Z",
    "updatedAt": "2026-07-31T01:25:00Z"
  }
  ```
- **Error Status Codes:**
  - `400 Bad Request`: Malformed JSON structure.
  - `401 Unauthorized`: Missing or invalid JWT bearer token.
  - `422 Unprocessable Entity`: Validation failure on constraint limits.

---

### 2.2 Get Protocol
Retrieves a single protocol by UUID.

- **Endpoint:** `GET /api/v1/protocols/{id}`
- **Authentication:** Required (`Bearer JWT`)
- **Path Parameters:**
  - `id` (UUID): Protocol ID.
- **Success Response (200 OK):** Same payload as Create Protocol response.
- **Error Status Codes:**
  - `401 Unauthorized`: Authentication required.
  - `404 Not Found`: Protocol ID missing or marked `status = DELETED`.

---

### 2.3 Update Protocol
Modifies an existing protocol definition.

- **Endpoint:** `PUT /api/v1/protocols/{id}`
- **Authentication:** Required (`Bearer JWT`)
- **Path Parameters:** `id` (UUID)
- **Request Payload:** Modified protocol payload with fields subject to validation rules.
- **Success Response (200 OK):** Protocol payload with incremented `version` counter (e.g. `version: 1`).
- **Error Status Codes:**
  - `401 Unauthorized`: Invalid auth token.
  - `404 Not Found`: Protocol not found or soft-deleted.
  - `409 Conflict`: Optimistic locking collision (`version` counter mismatch during concurrent edits).
  - `422 Unprocessable Entity`: Payload constraint validation failure.

---

### 2.4 Delete Protocol (Soft Delete)
Soft-deletes a protocol definition by setting `status = DELETED`.

- **Endpoint:** `DELETE /api/v1/protocols/{id}`
- **Authentication:** Required (`Bearer JWT`)
- **Path Parameters:** `id` (UUID)
- **Success Response (204 No Content):** Empty body. Soft delete preserves foreign key integrity for audit logs and historic runs.
- **Error Status Codes:**
  - `401 Unauthorized`: Missing auth token.
  - `404 Not Found`: Protocol not found or already deleted.

---

### 2.5 List Protocols (Paginated & Filterable)
Lists protocols with pagination, status filtering, and field sorting.

- **Endpoint:** `GET /api/v1/protocols`
- **Authentication:** Required (`Bearer JWT`)
- **Query Parameters:**
  - `page` (integer, default `0`): Page index (0-indexed).
  - `size` (integer, default `20`, max `100`): Items per page.
  - `sortBy` (string, default `createdAt`): Property to sort by.
  - `sortDir` (string, default `desc`): Sort direction (`asc` or `desc`).
  - `status` (string, default `ACTIVE`): Status filter (`ACTIVE` or `DELETED`).
- **Success Response (200 OK):**
  ```json
  {
    "content": [
      {
        "id": "e49c3391-c868-4c8e-b618-1f7f635f4de4",
        "name": "Standard Taq PCR Protocol",
        "cycleCount": 30,
        "rampRate": 3.5,
        "status": "ACTIVE",
        "createdAt": "2026-07-31T01:25:00Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "last": true
  }
  ```
- **Error Status Codes:**
  - `400 Bad Request`: Invalid `sortBy` field parameter.

---

## 3. Run Lifecycle Endpoints

### 3.1 Start Run
Initiates a protocol run on a designated lab instrument.

- **Endpoint:** `POST /api/v1/runs`
- **Method:** `POST`
- **Authentication:** Required (`Bearer JWT`)
- **Request Payload:**
  ```json
  {
    "protocolId": "e49c3391-c868-4c8e-b618-1f7f635f4de4",
    "deviceId": "DEV-CYCLER-9082"
  }
  ```
- **Success Response (201 Created):**
  ```json
  {
    "runId": "7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d",
    "protocolId": "e49c3391-c868-4c8e-b618-1f7f635f4de4",
    "deviceId": "DEV-CYCLER-9082",
    "status": "PENDING",
    "startedAt": "2026-07-31T01:25:10Z",
    "completedAt": null
  }
  ```
- **Error Status Codes:**
  - `404 Not Found`: Referenced protocol does not exist or is deleted.
  - `409 Conflict`: Specified device is currently executing another run.

---

### 3.2 Get Run Status
Retrieves live execution status and results for a run.

- **Endpoint:** `GET /api/v1/runs/{id}`
- **Authentication:** Required (`Bearer JWT`)
- **Success Response (200 OK):**
  ```json
  {
    "runId": "7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d",
    "protocolId": "e49c3391-c868-4c8e-b618-1f7f635f4de4",
    "deviceId": "DEV-CYCLER-9082",
    "status": "COMPLETED",
    "currentCycle": 30,
    "totalCycles": 30,
    "startedAt": "2026-07-31T01:25:10Z",
    "completedAt": "2026-07-31T01:55:10Z",
    "result": {
      "resultId": "b1c2d3e4-f5a6-7b8c-9d0e-1f2a3b4c5d6e",
      "ctValue": 22.4,
      "amplificationStatus": "POSITIVE"
    }
  }
  ```
- **Error Status Codes:**
  - `404 Not Found`: Run ID does not exist.

---

### 3.3 Cancel Run
Aborts an in-progress run.

- **Endpoint:** `POST /api/v1/runs/{id}/cancel`
- **Authentication:** Required (`Bearer JWT`)
- **Request Payload:**
  ```json
  {
    "reason": "Aborted by technician due to liquid handler failure"
  }
  ```
- **Success Response (200 OK):**
  ```json
  {
    "runId": "7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d",
    "status": "CANCELLED",
    "cancelledAt": "2026-07-31T01:30:00Z"
  }
  ```
- **Error Status Codes:**
  - `409 Conflict`: Cannot cancel a run that is already in a terminal state (`COMPLETED` or `FAILED`).

---
*Return to [Root README](../README.md)*
