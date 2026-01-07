package com.example.demo;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import org.junit.Test;

public class HelloControllerTest {
    @Test
    public void helloReturnsMessage() {
        HelloController controller = new HelloController();
        Map<String, String> response = controller.hello();

        assertEquals("Hello from Spring Boot", response.get("message"));
    }
}
