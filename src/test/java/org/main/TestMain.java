package org.main;

import org.junit.Test;

public class TestMain {
    @Test
    public void testSample() {
        System.out.println("\n\n\nPrinting System Variable: " + System.getenv("aws_key") + "\n\n\n");
    }
}