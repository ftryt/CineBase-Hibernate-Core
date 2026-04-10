package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.controller.MovieController;
import org.example.dao.MovieDao;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

// Use of generator:
// Generator generator = new Generator();
// generator.generateAll(20, factory); // factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory()

// Session sesssion = factory.openSession();
// List<User> users = sesssion
//  .createQuery("Select u FROM User u", User.class)
//  .getResultList();

// Use of DataExchangeService:
// DataExchangeService dataExchangeService = new DataExchangeService();
// dataExchangeService.fullExport("full_export.json", factory);
// dataExchangeService.fullImport("full_export.json", factory);


public class Main extends Application {

    private SessionFactory factory;

    @Override
    public void init() {
        // Connect the database when the program starts
        factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/movie_view.fxml"));
        BorderPane root = loader.load();

        // Passing the DAO to the controller
        MovieController controller = loader.getController();
        controller.setMovieDao(new MovieDao(factory));

        primaryStage.setTitle("CineBase - Admin Panel");
        primaryStage.setScene(new Scene(root, 1280, 800));
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Close the connection when exiting the program
        if (factory != null) {
            factory.close();
        }
    }

    public static void main(String[] args) {
        Generator generator = new Generator();
        generator.generateAll(20, new Configuration().configure("hibernate.cfg.xml").buildSessionFactory());

        launch(args);
    }
}