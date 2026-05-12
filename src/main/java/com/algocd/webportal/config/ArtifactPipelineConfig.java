package com.algocd.webportal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aopalliance.aop.Advice;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.handler.advice.ExpressionEvaluatingRequestHandlerAdvice;
import org.springframework.messaging.MessageChannel;

@Configuration
public class ArtifactPipelineConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public MessageChannel analysisInputChannel(
            @Qualifier("applicationTaskExecutor")
            AsyncTaskExecutor applicationTaskExecutor) {
        return new ExecutorChannel(applicationTaskExecutor);
    }

    @Bean
    public MessageChannel analysisErrorChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel compilationInputChannel(
            @Qualifier("applicationTaskExecutor")
            AsyncTaskExecutor applicationTaskExecutor) {
        return new ExecutorChannel(applicationTaskExecutor);
    }

    @Bean
    public MessageChannel compilationErrorChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel uploadInputChannel(
            @Qualifier("applicationTaskExecutor")
            AsyncTaskExecutor applicationTaskExecutor) {
        return new ExecutorChannel(applicationTaskExecutor);
    }

    @Bean
    public MessageChannel uploadErrorChannel() {
        return new DirectChannel();
    }

    @Bean
    public Advice analysisAdvice() {
        ExpressionEvaluatingRequestHandlerAdvice advice = new ExpressionEvaluatingRequestHandlerAdvice();
        advice.setFailureChannelName("analysisErrorChannel");
        // Sending the original payload (ArtifactProcessingQueue) to the error channel
        advice.setOnFailureExpressionString("payload");
        advice.setTrapException(true); // Don't propagate exception further
        return advice;
    }

    @Bean
    public Advice compilationAdvice() {
        ExpressionEvaluatingRequestHandlerAdvice advice = new ExpressionEvaluatingRequestHandlerAdvice();
        advice.setFailureChannelName("compilationErrorChannel");
        advice.setOnFailureExpressionString("payload");
        advice.setTrapException(true);
        return advice;
    }

    @Bean
    public Advice uploadAdvice() {
        ExpressionEvaluatingRequestHandlerAdvice advice = new ExpressionEvaluatingRequestHandlerAdvice();
        advice.setFailureChannelName("uploadErrorChannel");
        advice.setOnFailureExpressionString("payload");
        advice.setTrapException(true);
        return advice;
    }
}
