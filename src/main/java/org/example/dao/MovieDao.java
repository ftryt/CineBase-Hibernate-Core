package org.example.dao;

import org.example.Tables.Actor;
import org.example.Tables.Genre;
import org.example.Tables.Movie;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import java.util.List;

public class MovieDao {
    private final SessionFactory factory;

    public MovieDao(SessionFactory factory) {
        this.factory = factory;
    }

    public List<Movie> getAllMovies() {
        try (Session session = factory.openSession()) {
            // JOIN FETCH forces Hibernate to immediately fetch the genres and actors,
            // so it can be safely displayed in JavaFX.
            return session.createQuery(
                    "select distinct m from Movie m " +
                            "left join fetch m.genres " +
                            "left join fetch m.actors", Movie.class).list();
        }
    }

    public void saveOrUpdate(Movie movie) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.merge(movie);
            session.getTransaction().commit();
        }
    }

    public void delete(Movie movie) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.remove(session.contains(movie) ? movie : session.merge(movie));
            session.getTransaction().commit();
        }
    }

    public List<Genre> getAllGenres() {
        try (Session session = factory.openSession()) {
            return session.createQuery("from Genre", Genre.class).list();
        }
    }

    public List<Actor> getAllActors() {
        try (Session session = factory.openSession()) {
            return session.createQuery("from Actor", Actor.class).list();
        }
    }
}
