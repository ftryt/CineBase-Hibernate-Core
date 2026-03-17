package org.example.Tables;

import jakarta.persistence.*;

@Entity
@Table(name = "Watch_History")
public class WatchHistory {
    // Tracks what users watched and its progress

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "movie_id")
    private Movie movie;
    private short progress_percent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public short getProgress_percent() {
        return progress_percent;
    }

    public void setProgress_percent(short progress_percent) {
        this.progress_percent = progress_percent;
    }
}
