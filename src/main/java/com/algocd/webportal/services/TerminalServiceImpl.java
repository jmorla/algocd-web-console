package com.algocd.webportal.services;

import com.algocd.webportal.entities.Tag;
import com.algocd.webportal.entities.Terminal;
import com.algocd.webportal.entities.TerminalStatus;
import com.algocd.webportal.exceptions.AlgocdException;
import com.algocd.webportal.exceptions.ErrorReason;
import com.algocd.webportal.mappers.TagMapper;
import com.algocd.webportal.mappers.TerminalMapper;
import com.algocd.webportal.services.models.CreateTerminalRequest;
import com.algocd.webportal.util.Result;
import com.algocd.webportal.util.TokenUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    public Result<Terminal> createTerminal(UUID userId, CreateTerminalRequest request) {
        Set<ConstraintViolation<CreateTerminalRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            return Result.failure(new AlgocdException(ErrorReason.VALIDATION_FAILED, "Validation failed: " + violations.iterator().next().getMessage()));
        }

        UUID terminalId = UUID.randomUUID();
        String token = TokenUtil.generateSecureToken();
        Instant now = Instant.now();
        Instant expiresAt = now.plus(24, ChronoUnit.HOURS); // Token valid for 24 hours

        Terminal terminal = new Terminal(
                terminalId,
                userId,
                TerminalStatus.DISCONNECTED,
                null,
                token,
                expiresAt,
                now,
                now
        );

        terminalMapper.insert(terminal);

        // Add name as a tag
        tagMapper.insert(new Tag(terminalId.toString(), "name", request.name()));

        // Add platform as a tag
        tagMapper.insert(new Tag(terminalId.toString(), "platform", request.platform().name()));

        // Add additional tags if any
        if (request.tags() != null) {
            request.tags().forEach((key, value) -> {
                if (!"name".equals(key)) {
                    tagMapper.insert(new Tag(terminalId.toString(), key, value));
                }
            });
        }

        return Result.success(terminal);
    }

    @Override
    @Transactional
    public Result<Terminal> bootstrapTerminal(String bootstrapToken, String instanceIp) {
        Terminal terminal = terminalMapper.findByBootstrapToken(bootstrapToken);
        if (terminal == null) {
            return Result.failure(new AlgocdException(ErrorReason.INVALID_BOOTSTRAP_TOKEN));
        }
        
        if (terminal.getBootstrapTokenExpiresAt() != null && terminal.getBootstrapTokenExpiresAt().isBefore(Instant.now())) {
            return Result.failure(new AlgocdException(ErrorReason.BOOTSTRAP_TOKEN_EXPIRED));
        }
        
        Instant now = Instant.now();
        terminalMapper.completeBootstrap(terminal.getTerminalId(), TerminalStatus.CONNECTED, instanceIp, now);
        
        terminal.setStatus(TerminalStatus.CONNECTED);
        terminal.setInstanceIp(instanceIp);
        terminal.setBootstrapToken(null);
        terminal.setBootstrapTokenExpiresAt(null);
        terminal.setUpdatedAt(now);
        
        return Result.success(terminal);
    }
}
