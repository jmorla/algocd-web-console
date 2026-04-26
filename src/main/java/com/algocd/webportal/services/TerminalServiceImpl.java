package com.algocd.webportal.services;

import com.algocd.webportal.entities.Tag;
import com.algocd.webportal.entities.Terminal;
import com.algocd.webportal.mappers.TagMapper;
import com.algocd.webportal.mappers.TerminalMapper;
import com.algocd.webportal.services.models.CreateTerminalRecord;
import com.algocd.webportal.services.models.TagRecord;
import com.algocd.webportal.util.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
@Validated
public class TerminalServiceImpl implements TerminalService {

    private final TerminalMapper terminalMapper;
    private final TagMapper tagMapper;
    private final Validator validator;

    public TerminalServiceImpl(TerminalMapper terminalMapper, TagMapper tagMapper, Validator validator) {
        this.terminalMapper = terminalMapper;
        this.tagMapper = tagMapper;
        this.validator = validator;
    }

    @Override
    @Transactional
    public Result<Void> createTerminal(UUID userId, CreateTerminalRecord record) {
        Set<ConstraintViolation<CreateTerminalRecord>> violations = validator.validate(record);
        if (!violations.isEmpty()) {
            return Result.failure(new ConstraintViolationException(violations));
        }

        UUID terminalId = UUID.randomUUID();
        Terminal terminal = new Terminal();
        terminal.setTerminalId(terminalId);
        terminal.setUserId(userId);
        terminal.setName(record.name());
        terminal.setVersion(record.version());
        terminal.setPlanId(record.planId());
        terminal.setLocationId(record.locationId());
        terminal.setStatus("PROVISIONING");
        terminal.setCreatedAt(Instant.now());
        terminal.setUpdatedAt(Instant.now());

        terminalMapper.insert(terminal);

        if (record.tags() != null) {
            for (TagRecord tagRecord : record.tags()) {
                tagMapper.insert(new Tag(terminalId.toString(), tagRecord.key(), tagRecord.value()));
            }
        }

        return Result.success(null);
    }
}
