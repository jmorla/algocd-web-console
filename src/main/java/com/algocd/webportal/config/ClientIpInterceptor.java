package com.algocd.webportal.config;

import io.grpc.*;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Component
public class ClientIpInterceptor implements ServerInterceptor {
    public static final Context.Key<String> CLIENT_IP_KEY = Context.key("client-ip");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        
        String ip = "unknown";
        SocketAddress remoteAddress = call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
        if (remoteAddress instanceof InetSocketAddress inetSocketAddress) {
            ip = inetSocketAddress.getAddress().getHostAddress();
        }
        
        Context context = Context.current().withValue(CLIENT_IP_KEY, ip);
        return Contexts.interceptCall(context, call, headers, next);
    }
}
