package com.example.samplediceapp.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
// import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// import java.time.Duration;

@Configuration
public class OpenTelemetryConfig {

    @Value("${otel.exporter.otlp.endpoint:http://localhost:4317}")
    private String otlpEndpoint;

    @Bean
    public OpenTelemetry openTelemetry() {
        // ResourceAttributesを使わずに直接AttributeKeyを使用
        // Resource resource = Resource.getDefault()
        //         .merge(Resource.create(Attributes.builder()
        //                 .put(AttributeKey.stringKey("service.name"), "dice-app")
        //                 .put(AttributeKey.stringKey("deployment.environment"), "development")
        //                 .build()));
        Resource resource = Resource.getDefault()
        .merge(Resource.create(Attributes.of(
                AttributeKey.stringKey("service.name"), "dice‑app",
                AttributeKey.stringKey("deployment.environment"), "development"
        )));

        // OTLP
        OtlpGrpcSpanExporter otlpExporter =
        OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://localhost:4317")
                .build();

        // Logging
        LoggingSpanExporter loggingExporter = new LoggingSpanExporter();

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .setResource(resource)
                .addSpanProcessor(BatchSpanProcessor.builder(otlpExporter).build())
                .addSpanProcessor(SimpleSpanProcessor.create(loggingExporter))
                .build();
                // .addSpanProcessor(BatchSpanProcessor.builder(
                //         OtlpGrpcSpanExporter.builder()
                //                 .setEndpoint(otlpEndpoint)
                //                 .setTimeout(Duration.ofSeconds(5))
                //                 .build())
                        // .build())
                // .setResource(resource)
                // .build();

        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();

        // シャットダウンフック
        Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));

        return sdk;
    }

    @Bean
    public Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("com.example.samplediceapp");
    }
}