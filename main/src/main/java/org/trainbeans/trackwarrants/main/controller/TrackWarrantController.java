package org.trainbeans.trackwarrants.main.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.trainbeans.trackwarrants.main.dto.CreateWarrantRequest;
import org.trainbeans.trackwarrants.main.dto.TrackWarrantResponse;
import org.trainbeans.trackwarrants.main.entity.TrackWarrant;
import org.trainbeans.trackwarrants.main.mapper.TrackWarrantMapper;
import org.trainbeans.trackwarrants.main.service.TrackWarrantService;
import org.trainbeans.trackwarrants.main.service.TrackWarrantUseCase;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for track warrant operations.
 * Provides RESTful endpoints for managing track warrants.
 */
@RestController
@RequestMapping("/api/warrants")
@RequiredArgsConstructor
@Slf4j
public class TrackWarrantController {


    private final TrackWarrantUseCase service;
    private final TrackWarrantMapper mapper;

    /**
     * Get the next sequential warrant number for today.
     * GET /api/warrants/next-number
     */
    @GetMapping("/next-number")
    public ResponseEntity<Integer> getNextWarrantNumber() {
        log.info("GET /api/warrants/next-number");
        return ResponseEntity.ok(((TrackWarrantService) service).nextDailyWarrantNumber());
    }

    /**
     * Get all track warrants.
     * GET /api/warrants
     */
    @GetMapping
    public ResponseEntity<List<TrackWarrantResponse>> getAllWarrants() {
        log.info("GET /api/warrants - Fetching all warrants");

        List<TrackWarrantResponse> warrants = service.getAllWarrants().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(warrants);
    }

    /**
     * Get active track warrants only.
     * GET /api/warrants/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<TrackWarrantResponse>> getActiveWarrants() {
        log.info("GET /api/warrants/active - Fetching active warrants");

        List<TrackWarrantResponse> warrants = service.getActiveWarrants().stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(warrants);
    }

    /**
     * Get a single track warrant by warrant ID.
     * GET /api/warrants/{warrantId}
     */
    @GetMapping("/{warrantId}")
    public ResponseEntity<TrackWarrantResponse> getWarrantByWarrantId(@PathVariable String warrantId) {
        log.info("GET /api/warrants/{} - Fetching warrant", warrantId);

        return service.getWarrantByWarrantId(warrantId)
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get all warrants for a specific train.
     * GET /api/warrants/train/{trainId}
     */
    @GetMapping("/train/{trainId}")
    public ResponseEntity<List<TrackWarrantResponse>> getWarrantsByTrainId(@PathVariable String trainId) {
        log.info("GET /api/warrants/train/{} - Fetching warrants for train", trainId);

        List<TrackWarrantResponse> warrants = service.getWarrantsByTrainId(trainId).stream()
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        return ResponseEntity.ok(warrants);
    }

    /**
     * Create a new track warrant.
     * POST /api/warrants
     */
    @PostMapping
    public ResponseEntity<TrackWarrantResponse> createWarrant(@RequestBody CreateWarrantRequest request) {
        log.info("POST /api/warrants - Creating warrant: {}", request.getWarrantId());

        TrackWarrant created = service.createWarrant(request);
        TrackWarrantResponse response = mapper.toResponse(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Mark a track warrant as completed.
     * PUT /api/warrants/{warrantId}/complete
     */
    @PutMapping("/{warrantId}/complete")
    public ResponseEntity<TrackWarrantResponse> completeWarrant(@PathVariable String warrantId) {
        log.info("PUT /api/warrants/{}/complete - Completing warrant", warrantId);

        TrackWarrant completed = service.completeWarrant(warrantId);
        TrackWarrantResponse response = mapper.toResponse(completed);

        return ResponseEntity.ok(response);
    }

    /**
     * Cancel a track warrant.
     * PUT /api/warrants/{warrantId}/cancel
     */
    @PutMapping("/{warrantId}/cancel")
    public ResponseEntity<TrackWarrantResponse> cancelWarrant(@PathVariable String warrantId) {
        log.info("PUT /api/warrants/{}/cancel - Cancelling warrant", warrantId);

        TrackWarrant cancelled = service.cancelWarrant(warrantId);
        TrackWarrantResponse response = mapper.toResponse(cancelled);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete a track warrant.
     * DELETE /api/warrants/{warrantId}
     */
    @DeleteMapping("/{warrantId}")
    public ResponseEntity<Void> deleteWarrant(@PathVariable String warrantId) {
        log.info("DELETE /api/warrants/{} - Deleting warrant", warrantId);

        service.deleteWarrant(warrantId);
        return ResponseEntity.noContent().build();
    }
}
