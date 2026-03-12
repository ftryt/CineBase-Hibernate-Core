package org.example.Tables;

import jakarta.persistence.*;

@Entity
@Table(name = "Genres")
public class Genre {
    // Stores categories like Action, Drama, Comedy
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String genre_name;

    public Long getId() {
        return id;
    }

    public String getGenre_name() {
        return genre_name;
    }

    public void setGenre_name(String genre_name) {
        this.genre_name = genre_name;
    }
}
