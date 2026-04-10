package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.Tables.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.File;
import java.util.List;

// Serves for importing db to file and exporting
public class DataExchangeService {
    private final ObjectMapper mapper;

    public DataExchangeService() {
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // optional but recommended for LocalDateTime
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
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

    public void fullImport(String filePath, SessionFactory factory) {
        // Carefully!! it removes all previous data
        try {
            DatabaseSnapshot snapshot = mapper.readValue(new File(filePath), DatabaseSnapshot.class);

            try (Session session = factory.openSession()) {
                session.beginTransaction();

                // Clear all prev data, using native query its faster
                session.createNativeQuery("TRUNCATE TABLE Watch_History").executeUpdate();
                session.createNativeQuery("TRUNCATE TABLE Ratings").executeUpdate();
                session.createNativeQuery("TRUNCATE TABLE Subscription").executeUpdate();
                session.createNativeQuery("TRUNCATE TABLE Movie_Actors").executeUpdate();
                session.createNativeQuery("TRUNCATE TABLE Watchlist_User_Movie").executeUpdate();
                session.createNativeQuery("TRUNCATE TABLE Movie_Genres").executeUpdate();
                session.createNativeQuery("TRUNCATE TABLE Movies").executeUpdate();
                session.createNativeQuery("TRUNCATE TABLE Actors").executeUpdate();
                session.createNativeQuery("TRUNCATE TABLE Genres").executeUpdate();
                session.createNativeQuery("TRUNCATE TABLE Users").executeUpdate();

                // Creating new entries
                for (Actor a : snapshot.getActors()) {
                    a.setId(null);
                    session.persist(a);
                }
                for (Genre g : snapshot.getGenres()) {
                    g.setId(null);
                    session.persist(g);
                }
                session.flush();

                for (Movie m : snapshot.getMovies()) {
                    m.setId(null);
                    session.persist(m);
                }
                for (User u : snapshot.getUsers()) {
                    u.setId(null);
                    session.persist(u);
                }
                session.flush();

                for (Rating r : snapshot.getRatings()) {
                    r.setId(null);
                    session.persist(r);
                }
                for (Subscription s : snapshot.getSubscriptions()) {
                    s.setId(null);
                    session.persist(s);
                }
                for (WatchHistory wh : snapshot.getWatchHistories()) {
                    wh.setId(null);
                    session.persist(wh);
                }

                session.getTransaction().commit();
                System.out.println("Import finished!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
