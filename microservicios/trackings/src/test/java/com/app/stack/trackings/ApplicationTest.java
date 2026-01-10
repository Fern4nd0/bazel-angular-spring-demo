package com.app.stack.trackings;

import static org.junit.Assert.assertNotNull;

import com.app.stack.Application;
import org.junit.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public class ApplicationTest {
    @Test
    public void applicationIsAnnotated() {
        assertNotNull(Application.class.getAnnotation(SpringBootApplication.class));
    }
}
