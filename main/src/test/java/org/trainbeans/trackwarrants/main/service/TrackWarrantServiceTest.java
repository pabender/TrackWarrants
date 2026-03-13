package org.trainbeans.trackwarrants.main.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.trainbeans.trackwarrants.main.dto.CreateWarrantRequest;
import org.trainbeans.trackwarrants.main.entity.TrackWarrant;
import org.trainbeans.trackwarrants.main.exception.DuplicateWarrantIdException;
import org.trainbeans.trackwarrants.main.exception.WarrantNotFoundException;
import org.trainbeans.trackwarrants.main.factory.TrackWarrantFactory;
import org.trainbeans.trackwarrants.main.repository.TrackWarrantRepository;
import org.trainbeans.trackwarrants.main.validation.TrackWarrantValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TrackWarrantService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TrackWarrantService Unit Tests")
class TrackWarrantServiceTest {

    @Mock
    private TrackWarrantRepository repository;

    @Mock
    private WarrantStatusTransitionPolicy transitionPolicy;

    @Mock
    private TrackWarrantValidator validator;

    @Mock
    private TrackWarrantFactory factory;

    @InjectMocks
    private TrackWarrantService service;

    private TrackWarrant testWarrant;
    private CreateWarrantRequest createRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        testWarrant = TrackWarrant.builder()
            .id(1L)
            .warrantId("TW-TEST-001")
            .warrantNumber(1)
            .warrantDate("2026-03-13")
            .trainId("TRAIN-123")
            .trainCrew("ENGINEER A / CONDUCTOR B")
            .location("STATION A")
            .startingLocation("Station A")
            .issuedDateTime(now)
            .status(TrackWarrant.WarrantStatus.ACTIVE)
            .issuedBy("Dispatcher Test")
            .okTime("0800")
            .dispatcher("Dispatcher Test")
            .copiedBy("CONDUCTOR B")
            .line2Instruction("Proceed from Station A to Station B on Main Line Track")
            .createdDateTime(now)
            .lastModifiedDateTime(now)
            .build();

        createRequest = CreateWarrantRequest.builder()
            .warrantId("TW-NEW-001")
            .warrantNumber(2)
            .warrantDate("2026-03-13")
            .trainId("TRAIN-456")
            .trainCrew("ENGINEER C / CONDUCTOR D")
            .location("STATION B")
            .startingLocation("Start")
            .issuedBy("Test Dispatcher")
            .okTime("0900")
            .dispatcher("Test Dispatcher")
            .copiedBy("CONDUCTOR D")
            .line2Instruction("Proceed from Start to End on Test Track Track")
            .build();
    }

    @Test
    @DisplayName("createWarrant - Should create new warrant successfully")
    void testCreateWarrant() {
        doNothing().when(validator).validateCreate(createRequest);
        when(factory.create(createRequest)).thenReturn(testWarrant);
        when(repository.save(any(TrackWarrant.class))).thenReturn(testWarrant);

        TrackWarrant result = service.createWarrant(createRequest);

        assertThat(result).isNotNull();
        verify(validator).validateCreate(createRequest);
        verify(factory).create(createRequest);
        verify(repository).save(any(TrackWarrant.class));
    }

    @Test
    @DisplayName("createWarrant - Should throw exception for duplicate warrant ID")
    void testCreateDuplicateWarrant() {
        doThrow(new DuplicateWarrantIdException(createRequest.getWarrantId()))
            .when(validator).validateCreate(createRequest);

        assertThatThrownBy(() -> service.createWarrant(createRequest))
            .isInstanceOf(DuplicateWarrantIdException.class)
            .hasMessageContaining("Warrant ID already exists");

        verify(validator).validateCreate(createRequest);
        verify(factory, never()).create(any());
        verify(repository, never()).save(any(TrackWarrant.class));
    }

    @Test
    @DisplayName("getAllWarrants - Should return all warrants")
    void testGetAllWarrants() {
        List<TrackWarrant> warrants = List.of(testWarrant);
        when(repository.findAll()).thenReturn(warrants);

        List<TrackWarrant> result = service.getAllWarrants();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getWarrantId()).isEqualTo("TW-TEST-001");
        verify(repository).findAll();
    }

    @Test
    @DisplayName("getWarrantByWarrantId - Should return warrant when exists")
    void testGetWarrantByWarrantId() {
        when(repository.findByWarrantId("TW-TEST-001")).thenReturn(Optional.of(testWarrant));

        Optional<TrackWarrant> result = service.getWarrantByWarrantId("TW-TEST-001");

        assertThat(result).isPresent();
        assertThat(result.get().getWarrantId()).isEqualTo("TW-TEST-001");
        verify(repository).findByWarrantId("TW-TEST-001");
    }

    @Test
    @DisplayName("getWarrantByWarrantId - Should return empty when not exists")
    void testGetWarrantByWarrantIdNotFound() {
        when(repository.findByWarrantId("NON-EXISTENT")).thenReturn(Optional.empty());

        Optional<TrackWarrant> result = service.getWarrantByWarrantId("NON-EXISTENT");

        assertThat(result).isEmpty();
        verify(repository).findByWarrantId("NON-EXISTENT");
    }

    @Test
    @DisplayName("getActiveWarrants - Should return only active warrants")
    void testGetActiveWarrants() {
        List<TrackWarrant> activeWarrants = List.of(testWarrant);
        when(repository.findByStatus(TrackWarrant.WarrantStatus.ACTIVE)).thenReturn(activeWarrants);

        List<TrackWarrant> result = service.getActiveWarrants();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(TrackWarrant.WarrantStatus.ACTIVE);
        verify(repository).findByStatus(TrackWarrant.WarrantStatus.ACTIVE);
    }

    @Test
    @DisplayName("getWarrantsByTrainId - Should return warrants for specific train")
    void testGetWarrantsByTrainId() {
        List<TrackWarrant> trainWarrants = List.of(testWarrant);
        when(repository.findByTrainId("TRAIN-123")).thenReturn(trainWarrants);

        List<TrackWarrant> result = service.getWarrantsByTrainId("TRAIN-123");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTrainId()).isEqualTo("TRAIN-123");
        verify(repository).findByTrainId("TRAIN-123");
    }

    @Test
    @DisplayName("completeWarrant - Should mark warrant as completed")
    void testCompleteWarrant() {
        when(repository.findByWarrantId("TW-TEST-001")).thenReturn(Optional.of(testWarrant));
        when(transitionPolicy.isAllowed(TrackWarrant.WarrantStatus.ACTIVE, TrackWarrant.WarrantStatus.COMPLETED)).thenReturn(true);
        when(repository.save(any(TrackWarrant.class))).thenReturn(testWarrant);

        TrackWarrant result = service.completeWarrant("TW-TEST-001");

        assertThat(result).isNotNull();
        verify(repository).findByWarrantId("TW-TEST-001");
        verify(repository).save(any(TrackWarrant.class));
    }

    @Test
    @DisplayName("completeWarrant - Should throw exception when warrant not found")
    void testCompleteWarrantNotFound() {
        when(repository.findByWarrantId("NON-EXISTENT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.completeWarrant("NON-EXISTENT"))
            .isInstanceOf(WarrantNotFoundException.class)
            .hasMessageContaining("Warrant not found");

        verify(repository, never()).save(any(TrackWarrant.class));
    }

    @Test
    @DisplayName("cancelWarrant - Should cancel a warrant")
    void testCancelWarrant() {
        when(repository.findByWarrantId("TW-TEST-001")).thenReturn(Optional.of(testWarrant));
        when(transitionPolicy.isAllowed(TrackWarrant.WarrantStatus.ACTIVE, TrackWarrant.WarrantStatus.CANCELLED)).thenReturn(true);
        when(repository.save(any(TrackWarrant.class))).thenReturn(testWarrant);

        TrackWarrant result = service.cancelWarrant("TW-TEST-001");

        assertThat(result).isNotNull();
        verify(repository).findByWarrantId("TW-TEST-001");
        verify(repository).save(any(TrackWarrant.class));
    }

    @Test
    @DisplayName("updateWarrantStatus - Should update status successfully")
    void testUpdateWarrantStatus() {
        when(repository.findByWarrantId("TW-TEST-001")).thenReturn(Optional.of(testWarrant));
        when(transitionPolicy.isAllowed(TrackWarrant.WarrantStatus.ACTIVE, TrackWarrant.WarrantStatus.EXPIRED)).thenReturn(true);
        when(repository.save(any(TrackWarrant.class))).thenReturn(testWarrant);

        TrackWarrant result = service.updateWarrantStatus("TW-TEST-001", TrackWarrant.WarrantStatus.EXPIRED);

        assertThat(result).isNotNull();
        verify(repository).findByWarrantId("TW-TEST-001");
        verify(repository).save(any(TrackWarrant.class));
    }

    @Test
    @DisplayName("deleteWarrant - Should delete warrant successfully")
    void testDeleteWarrant() {
        when(repository.findByWarrantId("TW-TEST-001")).thenReturn(Optional.of(testWarrant));
        doNothing().when(repository).delete(any(TrackWarrant.class));

        service.deleteWarrant("TW-TEST-001");

        verify(repository).findByWarrantId("TW-TEST-001");
        verify(repository).delete(testWarrant);
    }

    @Test
    @DisplayName("deleteWarrant - Should throw exception when warrant not found")
    void testDeleteWarrantNotFound() {
        when(repository.findByWarrantId("NON-EXISTENT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteWarrant("NON-EXISTENT"))
            .isInstanceOf(WarrantNotFoundException.class)
            .hasMessageContaining("Warrant not found");

        verify(repository, never()).delete(any(TrackWarrant.class));
    }
}
