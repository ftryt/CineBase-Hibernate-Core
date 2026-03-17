package org.example;

import org.example.Tables.*;
import java.util.List;

public class DatabaseSnapshot {
    private List<Actor> actors;
    private List<Genre> genres;
    private List<Movie> movies;
    private List<User> users;
    private List<Rating> ratings;
    private List<Subscription> subscriptions;
    private List<WatchHistory> watchHistories;

    public DatabaseSnapshot() {}

    public DatabaseSnapshot(List<Actor> actors,
                            List<Genre> genres,
                            List<Movie> movies,
                            List<User> users,
                            List<Rating> ratings,
                            List<Subscription> subscriptions,
                            List<WatchHistory> watchHistories) {
        this.actors = actors;
        this.genres = genres;
        this.movies = movies;
        this.users = users;
        this.ratings = ratings;
        this.subscriptions = subscriptions;
        this.watchHistories = watchHistories;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public List<WatchHistory> getWatchHistories() {
        return watchHistories;
    }

    public void setWatchHistories(List<WatchHistory> watchHistories) {
        this.watchHistories = watchHistories;
    }
}