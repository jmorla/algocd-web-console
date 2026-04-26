package com.algocd.webportal.services;

import com.algocd.webportal.entities.MetaTraderVersion;
import com.algocd.webportal.entities.Tag;
import com.algocd.webportal.entities.Terminal;
import com.algocd.webportal.mappers.TagMapper;
import com.algocd.webportal.mappers.TerminalMapper;
import com.algocd.webportal.services.models.CreateTerminalRecord;
import com.algocd.webportal.services.models.TagRecord;
import com.algocd.webportal.util.Result;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
    void shouldCreateTerminalWithTags() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();
        CreateTerminalRecord record = new CreateTerminalRecord(
            MetaTraderVersion.METATRADER_5, planId, locationId, 
            List.of(new TagRecord("env", "prod"), new TagRecord("team", "alpha")), 
            "My Terminal", "12345", "Broker-1", "secret"
        );

        // When
        Result<Void> result = terminalService.createTerminal(userId, record);

        // Then
        assertThat(result.isSuccess()).isTrue();

        ArgumentCaptor<Terminal> terminalCaptor = ArgumentCaptor.forClass(Terminal.class);
        verify(terminalMapper).insert(terminalCaptor.capture());
        Terminal savedTerminal = terminalCaptor.getValue();
        
        assertThat(savedTerminal.getUserId()).isEqualTo(userId);
        assertThat(savedTerminal.getVersion()).isEqualTo(MetaTraderVersion.METATRADER_5);

        ArgumentCaptor<Tag> tagCaptor = ArgumentCaptor.forClass(Tag.class);
        verify(tagMapper, times(2)).insert(tagCaptor.capture());
        List<Tag> savedTags = tagCaptor.getAllValues();

        assertThat(savedTags).extracting(Tag::getTagKey).containsExactlyInAnyOrder("env", "team");
        assertThat(savedTags).extracting(Tag::getTagValue).containsExactlyInAnyOrder("prod", "alpha");
        assertThat(savedTags).allMatch(tag -> tag.getResourceId().equals(savedTerminal.getTerminalId().toString()));
    }
}
