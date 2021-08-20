package org.main;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;
import java.util.Scanner;

public class TestMain {
    @Test
    public void testSample() throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/test/resources/test.properties"));
        String redshift_endpoint = System.getenv("redshift_endpoint");
        System.out.println(redshift_endpoint);
        String s = System.getenv(properties.getProperty("s"));
        File myObj = new File("filename.txt");
        if (myObj.createNewFile()) {
            System.out.println("File created: " + myObj.getName());
        } else {
            System.out.println("File already exists.");
        }
        FileWriter myWriter = new FileWriter("filename.txt");
        myWriter.write(s);
        myWriter.close();
        System.out.println("Successfully wrote to the file.");
        System.out.println("\n\n\nPrinting System Variable: " + s + "\n\n\n");
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            StringBuilder input1 = new StringBuilder();
            input1.append(data);
            input1.reverse();
            System.out.println(input1);
        }
        myReader.close();
    }
}