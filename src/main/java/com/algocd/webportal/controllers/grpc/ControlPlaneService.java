package com.algocd.webportal.controllers.grpc;

import com.algocd.controlplane.grpc.ControlPlaneGrpc;
import com.algocd.controlplane.grpc.HeartbeatRequest;
import com.algocd.controlplane.grpc.HeartbeatResponse;
import com.algocd.controlplane.grpc.JoinRequest;
import com.algocd.controlplane.grpc.JoinResponse;
import com.algocd.webportal.config.ClientIpInterceptor;
import com.algocd.webportal.entities.Terminal;
import com.algocd.webportal.services.TerminalService;
import com.algocd.webportal.util.Result;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
public class ControlPlaneService extends ControlPlaneGrpc.ControlPlaneImplBase {

    private final TerminalService terminalService;

    public ControlPlaneService(TerminalService terminalService) {
        this.terminalService = terminalService;
    }

    @Override
    public void join(JoinRequest request, StreamObserver<JoinResponse> responseObserver) {
        String bootstrapToken = request.getBootstrapToken();
        String clientIp = extractClientIp();

        Result<Terminal> result = terminalService.bootstrapTerminal(bootstrapToken, clientIp);

        if (result.isSuccess()) {
            JoinResponse response = JoinResponse.newBuilder()
                    .setTerminalId(result.getValue().getTerminalId().toString())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.UNAUTHENTICATED
                    .withDescription(result.getError().getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void heartbeat(HeartbeatRequest request, StreamObserver<HeartbeatResponse> responseObserver) {
        try {
            UUID terminalId = UUID.fromString(request.getTerminalId());
            Result<Void> result = terminalService.heartbeat(terminalId);

            if (result.isSuccess()) {
                HeartbeatResponse response = HeartbeatResponse.newBuilder()
                        .setSuccess(true)
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(Status.INTERNAL
                        .withDescription(result.getError().getMessage())
                        .asRuntimeException());
            }
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid terminal ID format")
                    .asRuntimeException());
        }
    }

    private String extractClientIp() {
        String ip = ClientIpInterceptor.CLIENT_IP_KEY.get();
        return ip != null ? ip : "unknown";
    }
}
