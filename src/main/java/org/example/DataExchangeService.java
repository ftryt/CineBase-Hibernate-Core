package org.example;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.Tables.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataExchangeService {
    private final ObjectMapper mapper;

    public DataExchangeService() {
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // optional but recommended for LocalDateTime
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void exportUsersToJson(List<User> users, String filePath) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), users);

            System.out.println("Export finished successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fullExport(String filePath, SessionFactory factory){
        try (Session session = factory.openSession()) {
            List<Actor> actors = session.createQuery("from Actor", Actor.class).getResultList();
            List<Genre> genres = session.createQuery("from Genre", Genre.class).getResultList();
            List<Movie> movies = session.createQuery("from Movie", Movie.class).getResultList();
            List<User> users = session.createQuery("from User", User.class).getResultList();
            List<Rating> ratings = session.createQuery("from Rating", Rating.class).getResultList();
            List<Subscription> subscriptions = session.createQuery("from Subscription", Subscription.class).getResultList();
            List<WatchHistory> watchHistories = session.createQuery("from WatchHistory", WatchHistory.class).getResultList();

            // Pack to DTO (Data transfer object)
            DatabaseSnapshot databaseSnapshot = new DatabaseSnapshot(actors, genres, movies, users, ratings, subscriptions, watchHistories);

            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), databaseSnapshot);
            System.out.println("Export finished successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fullImport(String filePath, SessionFactory factory){
        try {
            DatabaseSnapshot databaseSnapshot = mapper.readValue(new File(filePath), DatabaseSnapshot.class);

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                // For proper loading in database all parent classes with ids should exist

                for (Actor a: databaseSnapshot.getActors()) {
                    a.setId(null);
                    session.persist(a);
                }

                for (Genre g: databaseSnapshot.getGenres()) {
                    g.setId(null);
                    session.merge(g);
                }

                for (Movie m: databaseSnapshot.getMovies()) {
                    m.setId(null);
                    session.merge(m);
                }
                for (User u: databaseSnapshot.getUsers()) session.merge(u);

                session.flush();
                session.clear();

                for (Rating r: databaseSnapshot.getRatings()) session.merge(r);
                for (Subscription s: databaseSnapshot.getSubscriptions()) session.merge(s);
                for (WatchHistory wh: databaseSnapshot.getWatchHistories()) session.merge(wh);

                session.getTransaction().commit();
                System.out.println("Data was imported!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
