package com.app.stack;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public class ApplicationTest {
    @Test
    public void applicationIsAnnotated() {
        assertNotNull(Application.class.getAnnotation(SpringBootApplication.class));
    }
}
