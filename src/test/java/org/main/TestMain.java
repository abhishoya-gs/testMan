package org.main;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.Properties;

public class TestMain {
    @Test
    public void testSample() throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/test/resources/test.properties"));
        String s = System.getenv(properties.getProperty("s"));
        System.out.println("\n\n\nPrinting System Variable: " + s + "\n\n\n");
    }
}