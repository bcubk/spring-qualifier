package com.bcubk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TestBeanQualifiers {

    static class TestClass {
        @Autowired
        @Qualifier("open.left")
        TestInterface right;

        public boolean doit() {
            return right.doItRight();
        }
    }

    @Configuration
    public static class BeanConfigurations {

        @Bean({ "open.right","right"})
        public TestInterface right() {
            return () -> false;
        }

        @Bean({"open.left","left"})
        public TestInterface left() {
            return () -> true;
        }

        @Bean
        protected TestClass testClass() {
            return new TestClass();
        }
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testInterface() {
        assertTrue(applicationContext.getBean(TestClass.class).doit());
    }
}
