package org.main;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

public class TestMain {

    static final String dbURL = "jdbc:redshift://"+System.getenv("redshift_endpoint") + "/sampledb";
    static final String MasterUsername = "rootuser";
    static final String MasterUserPassword = System.getenv("rs_master_pass");

    @Test
    public void TestRedshiftConnection() throws Exception{
        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName("com.amazon.redshift.jdbc.Driver");

            System.out.println(dbURL + "\n" + MasterUsername + "\n"+ MasterUserPassword);
            System.out.println("Connecting to database...");
            Properties props = new Properties();
            props.setProperty("user", MasterUsername);
            props.setProperty("password", MasterUserPassword);
            conn = DriverManager.getConnection(dbURL);
            //Try a simple query.
            System.out.println("Listing system tables...");
            stmt = conn.createStatement();
            String sql;
            sql = "select * from information_schema.tables limit 5;";
            ResultSet rs = stmt.executeQuery(sql);

            //Get the data from the result set.
            while(rs.next()){
                //Retrieve two columns.
                String catalog = rs.getString("table_catalog");
                String name = rs.getString("table_name");

                //Display values.
                System.out.print("Catalog: " + catalog);
                System.out.println(", Name: " + name);
            }
            rs.close();
            stmt.close();
            conn.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(Exception ex){
            }
            try{
                if(conn!=null)
                    conn.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        System.out.println("Finished connectivity test.");
    }

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