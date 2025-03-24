package com.bcubk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig
@ContextConfiguration(classes = TestBeanQualifiersInSubContextCorrectFieldName.BeanConfigurations.class)
public class TestBeanQualifiersInSubContextCorrectFieldName {
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
    }

    private static class TestClass {
        @Autowired
        @Qualifier("open.left")
        TestInterface left;

        public boolean doit() {
            return left.doItRight();
        }
    }

    @Configuration
    public static class SubConfiguration {
        @Bean
        TestClass testBean() {
            return new TestClass();
        }
    }

    @Autowired
    ApplicationContext context;

    @Test
    public void testInterface() {
        AnnotationConfigApplicationContext subCtx = new AnnotationConfigApplicationContext();
        subCtx.setParent(context);
        subCtx.register(SubConfiguration.class);
        subCtx.refresh();
        assertTrue(subCtx.getBean(TestClass.class).doit());
    }
}
