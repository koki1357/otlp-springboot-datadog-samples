package com.example.samplediceapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "otel.instrumentation.spring-webmvc.enabled=false",
    "otel.sdk.disabled=true"
})
class SamplediceappApplicationTests {

	@Test
	void contextLoads() {
	}

}
