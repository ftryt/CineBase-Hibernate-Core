package org.example;

import net.datafaker.Faker;
import org.example.Tables.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Main {
    public static void main(String[] args) {
        Configuration cfg = new Configuration().configure();

        SessionFactory factory = cfg.buildSessionFactory();

        Generator generator = new Generator();
        // generator.putUsers(5000, factory);
        // generator.testUserTable(factory);

        // Generate at least 100 inserts in each table
        // generator.generateAll(100, factory);
    }
}