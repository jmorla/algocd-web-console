package com.algocd.webportal.services;

import com.algocd.webportal.entities.Platform;
import com.algocd.webportal.entities.Tag;
import com.algocd.webportal.entities.Terminal;
import com.algocd.webportal.entities.TerminalStatus;
import com.algocd.webportal.mappers.TagMapper;
import com.algocd.webportal.mappers.TerminalMapper;
import com.algocd.webportal.services.models.CreateTerminalRequest;
import com.algocd.webportal.util.Result;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TerminalServiceImplTest {

    @Mock
    private TerminalMapper terminalMapper;

    @Mock
    private TagMapper tagMapper;

    private TerminalServiceImpl terminalService;

    @BeforeEach
    void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        terminalService = new TerminalServiceImpl(terminalMapper, tagMapper, validator);
    }

    @Test
    @DisplayName("When createTerminal is called, then terminal and tags (name, platform) are inserted")
    void whenCreateTerminal_thenInsertsTerminalAndTags() {
        // Given
        CreateTerminalRequest request = new CreateTerminalRequest("My Terminal", Platform.METATRADER_5, Map.of("key1", "value1"));

        // When
        Result<Terminal> result = terminalService.createTerminal(request);

        // Then
        assertThat(result.isSuccess()).isTrue();
        Terminal terminal = result.getValue();
        assertThat(terminal.getTerminalId()).isNotNull();

        verify(terminalMapper).insert(any(Terminal.class));

        ArgumentCaptor<Tag> tagCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagMapper, times(3)).insert(tagCaptor.capture());

        List<Tag> capturedTags = tagCaptor.getAllValues();
        assertThat(capturedTags).extracting(Tag::getTagKey).containsExactlyInAnyOrder("name", "platform", "key1");
        assertThat(capturedTags).filteredOn(t -> t.getTagKey().equals("name")).extracting(Tag::getTagValue).containsExactly("My Terminal");
        assertThat(capturedTags).filteredOn(t -> t.getTagKey().equals("platform")).extracting(Tag::getTagValue).containsExactly("METATRADER_5");
        assertThat(capturedTags).filteredOn(t -> t.getTagKey().equals("key1")).extracting(Tag::getTagValue).containsExactly("value1");
    }

    @Test
    @DisplayName("When heartbeat is called, then terminal heartbeat and status are updated")
    void whenHeartbeat_thenUpdatesHeartbeatAndStatus() {
        // Given
        UUID terminalId = UUID.randomUUID();

        // When
        Result<Void> result = terminalService.heartbeat(terminalId);

        // Then
        assertThat(result.isSuccess()).isTrue();
        verify(terminalMapper).updateHeartbeat(any(UUID.class), any(TerminalStatus.class), any());
    }

    @Test
    @DisplayName("When markStaleTerminalsAsDisconnected is called, then mapper is invoked with correct cutoff time")
    void whenMarkStaleTerminalsAsDisconnected_thenInvokesMapper() {
        // Given
        Duration ttl = Duration.ofMinutes(5);

        // When
        terminalService.markStaleTerminalsAsDisconnected(ttl);

        // Then
        verify(terminalMapper).updateStatusForStaleHeartbeats(
                eq(TerminalStatus.CONNECTED),
                eq(TerminalStatus.DISCONNECTED),
                any(Instant.class),
                any(Instant.class)
        );
    }
}
