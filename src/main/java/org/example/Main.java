package org.example;

import org.example.Tables.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Configuration cfg = new Configuration().configure();
        SessionFactory factory = cfg.buildSessionFactory();


//        Generator generator = new Generator();
//        generator.generateAll(20, factory);

//        Session sesssion = factory.openSession();
//        List<User> users = sesssion
//                .createQuery("Select u FROM User u", User.class)
//                .getResultList();

        DataExchangeService dataExchangeService = new DataExchangeService();
        // dataExchangeService.fullExport("full_export.json", factory);
        dataExchangeService.fullImport("full_export.json", factory);
    }
}