package org.example;

import net.datafaker.Faker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.example.Tables.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

// Serves for filling DB with data
public class Generator {
    private Faker faker;

    public Generator(){
        faker = new Faker();
    }

    public void generateAll(int n, SessionFactory factory){
        // Fills 10 DB tables with random data
        // n - at least n inserts in each table
        // Total in average 17*n inserts
        // putActors n = n inserts in "ACTORS"
        // putGenres 21 inserts or 0 if table is not empty in "GENRES"
        // putMovies n =
        // n inserts in "MOVIES", 5-10n inserts in "MOVIE_ACTORS", 1-3n inserts in "MOVIE_GENRES"
        // putUsers n =
        // n inserts in "USERS", 0-3n inserts in "WATCHLIST_USER_MOVIE"
        // putRating n = n inserts in "RATINGS"
        // putSubscriptions n = n inserts in "SUBSCRIPTION"
        // putWatchHistory n = n inserts in "WATCH_HISTORY"

        int totalInserts = 0;
        totalInserts += putActors(n, factory);
        totalInserts += putGenres(factory);
        totalInserts += putMovies(n, factory);
        totalInserts += putUsers(n, factory);
        totalInserts += putRating(n, factory);
        totalInserts += putSubscriptions(n, factory);
        totalInserts += putWatchHistory(n, factory);

        System.out.println("Tables were filled with -" + totalInserts + " inserts!");
    }

    public int putActors(int n, SessionFactory factory){
        // Put random actors in DB

        int totalInserts = 0;
        try (Session session = factory.openSession()){
            session.beginTransaction();

            for (int i = 0; i < n; i++){
                totalInserts += 1;
                Actor actor = new Actor();
                actor.setFullName(faker.name().fullName());
                actor.setBirthDate(faker.timeAndDate().birthday());
                session.persist(actor);
            }

            session.getTransaction().commit();
        }

        return totalInserts;
    }

    public int putGenres(SessionFactory factory){
        // This function checks if table "GENRES" contains anything and if not ands set of genres

        int totalInserts = 0;
        String[] genres = {"Action", "Adventure", "Animation", "Biography", "Comedy", "Crime", "Documentary",
                "Drama", "Family", "Fantasy", "History", "Horror", "Music", "Musical", "Mystery",
                "Romance", "Science Fiction", "Sport", "Thriller", "War", "Western"};

        try (Session session = factory.openSession()){
            session.beginTransaction();

            Long count = session
                    .createQuery("SELECT COUNT(a) FROM Genre a", Long.class)
                    .getSingleResult();

            if (count > 0){
                System.out.println("Genres already exist in DB!");
                session.getTransaction().commit();
                return 0;
            }

            for (String genre_name : genres){
                totalInserts += 1;
                Genre genre = new Genre();
                genre.setGenre_name(genre_name);
                session.persist(genre);
            }

            session.getTransaction().commit();
            System.out.println("Genres were put in DB!");
        }

        return totalInserts;
    }

    public int putMovies(int n, SessionFactory factory){
        // Put random movies with relation 5-10 actors and 1-3 genres in DB
        Random random = new Random();
        int totalInserts = 0;

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            long actorsCount = session
                    .createQuery("SELECT COUNT(a) FROM Actor a", Long.class)
                    .getSingleResult();
            long genresCount = session
                    .createQuery("SELECT COUNT(a) FROM Genre a", Long.class)
                    .getSingleResult();

            if (actorsCount <= 4 || genresCount <= 2){
                System.out.println("Not enough actors or genres in DB!");
                session.getTransaction().commit();
                return 0;
            }

            for (int i = 0; i < n; i++){
                totalInserts += 1;
                Movie movie = new Movie();
                movie.setTitle(faker.movie().name());
                movie.setRelease(faker.timeAndDate().birthday(0, 50));
                movie.setLanguage(faker.address().country());

                // Actors
                Set<Actor> actors = new HashSet<>();
                while (actors.size() < random.nextInt(6) + 5) {
                    Actor actor = session.find(Actor.class, random.nextInt((int) actorsCount) + 1);
                    if (actor != null) {
                        totalInserts += 1;
                        actors.add(actor);
                    }
                }
                movie.setActors(actors);

                // Genres
                Set<Genre> genres = new HashSet<>();
                while (genres.size() < random.nextInt(4) + 1) {
                    Genre genre = session.find(Genre.class, random.nextInt((int) genresCount) + 1);
                    if (genre != null) {
                        totalInserts += 1;
                        genres.add(genre);
                    }
                }
                movie.setGenres(genres);

                session.persist(movie);
            }

            session.getTransaction().commit();
        }

        return totalInserts;
    }

    public int putUsers(int n, SessionFactory factory){
        // Generate and put random user with watchlist (0-3 films) in DB

        Random random = new Random();
        int totalInserts = 0;

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            long moviesCount = session
                    .createQuery("SELECT COUNT(a) FROM Movie a", Long.class)
                    .getSingleResult();

            if (moviesCount <= 4){
                System.out.println("Not enough movies in DB to create watch list, proceeding without...");
            }

            for (int i = 0; i < n; i++){
                totalInserts += 1;
                User user = new User();
                user.setFirstName(faker.name().firstName());
                user.setLastName(faker.name().lastName());
                user.setEmail(faker.credentials().username() + i + "email.com");
                user.setPassword_hash(sha256(user.getEmail()));
                user.setCreated_at(LocalDateTime.now());

                // Watch list
                if (moviesCount > 4){
                    Set<Movie> movies = new HashSet<>();
                    while (movies.size() < random.nextInt(4)) {
                        Movie movie = session.find(Movie.class, random.nextInt((int) moviesCount) + 1);
                        if (movie != null) {
                            totalInserts += 1;
                            movies.add(movie);
                        }
                    }
                    user.setMovies(movies);
                }

                session.persist(user);
            }

            session.getTransaction().commit();
        }

        return totalInserts;
    }

    public int putRating (int n, SessionFactory factory){
        // Put random movies review in DB
        Random random = new Random();
        int totalInserts = 0;

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            long usersCount = session
                    .createQuery("SELECT COUNT(a) FROM User a", Long.class)
                    .getSingleResult();

            long moviesCount = session
                    .createQuery("SELECT COUNT(a) FROM Movie a", Long.class)
                    .getSingleResult();

            if (usersCount <= 0 || moviesCount <= 0){
                System.out.println("Not enough users or movies in DB!");
                session.getTransaction().commit();
                return 0 ;
            }

            for (int i = 0; i < n; i++){
                totalInserts += 1;
                Rating rating = new Rating();
                rating.setScore(random.nextInt(5) + 1);
                rating.setReview(faker.lorem().maxLengthSentence(1000));

                // Movies
                Movie movie = session.find(Movie.class, random.nextInt((int) moviesCount) + 1);
                rating.setMovie(movie);

                // Users
                User user = session.find(User.class, random.nextInt((int) usersCount) + 1);
                rating.setUser(user);

                session.persist(rating);
            }

            session.getTransaction().commit();
        }

        return totalInserts;
    }

    public int putSubscriptions(int n, SessionFactory factory){
        Random random = new Random();
        int totalInserts = 0;

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            long usersCount = session
                    .createQuery("SELECT COUNT(a) FROM User a", Long.class)
                    .getSingleResult();

            if (usersCount <= 0){
                System.out.println("Not enough users!");
                session.getTransaction().commit();
                return 0;
            }

            for (int i = 0; i < n; i++){
                totalInserts += 1;
                Subscription subscription = new Subscription();
                subscription.setPlan_name(faker.company().name() + " TV");
                subscription.setPrice(random.nextInt(1500) + 500);

                // User
                User user = session.find(User.class, random.nextInt((int) usersCount) + 1);
                subscription.setUser(user);

                // Status
                SubscriptionStatus[] statuses = SubscriptionStatus.values();
                SubscriptionStatus status = statuses[random.nextInt(statuses.length)];

                switch (status){
                    case ACTIVE:
                        subscription.setStart_date(LocalDateTime.ofInstant(faker.timeAndDate().past(), ZoneId.systemDefault()));
                        subscription.setEnd_date(LocalDateTime.ofInstant(faker.timeAndDate().future(), ZoneId.systemDefault()));
                        break;
                    case EXPIRED:
                        subscription.setStart_date(LocalDateTime.ofInstant(faker.timeAndDate().past(), ZoneId.systemDefault()));
                        subscription.setEnd_date(LocalDateTime.ofInstant(faker.timeAndDate().past(), ZoneId.systemDefault()));
                        break;
                    case UPCOMMING:
                        subscription.setStart_date(LocalDateTime.ofInstant(faker.timeAndDate().future(), ZoneId.systemDefault()));
                        subscription.setEnd_date(LocalDateTime.ofInstant(faker.timeAndDate().future(), ZoneId.systemDefault()));
                        break;
                }
                subscription.setStatus(status);

                session.persist(subscription);
            }

            session.getTransaction().commit();
        }

        return totalInserts;
    }

    public int putWatchHistory(int n, SessionFactory factory){
        Random random = new Random();
        int totalInserts = 0;

        try (Session session = factory.openSession()) {
            session.beginTransaction();

            long usersCount = session
                    .createQuery("SELECT COUNT(a) FROM User a", Long.class)
                    .getSingleResult();
            long moviesCount = session
                    .createQuery("SELECT COUNT(a) FROM Movie a", Long.class)
                    .getSingleResult();

            if (usersCount <= 0 || moviesCount <= 0) {
                System.out.println("Not enough users or movies!");
                session.getTransaction().commit();
                return 0;
            }

            for (int i = 0; i < n; i++){
                totalInserts += 1;
                WatchHistory watchHistory = new WatchHistory();
                watchHistory.setProgress_percent((short) random.nextInt(101));

                User user = session.find(User.class, random.nextInt((int) usersCount) + 1);
                watchHistory.setUser(user);

                Movie movie = session.find(Movie.class, random.nextInt((int) moviesCount) + 1);
                watchHistory.setMovie(movie);

                session.persist(watchHistory);
            }

            session.getTransaction().commit();
        }

        return totalInserts;
    }

    public static String sha256(String input) {
        MessageDigest digest = null;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder hex = new StringBuilder();
        for (byte b : hashBytes) {
            String h = Integer.toHexString(0xff & b);
            if (h.length() == 1) hex.append('0');
            hex.append(h);
        }
        return hex.toString();
    }

    public void testUserTable(SessionFactory factory) {
        long totalMillis = 0;
        Random random = new Random();
        long usersCount = 0;

        try (Session session = factory.openSession()) {
            usersCount = session.createQuery("SELECT COUNT(a) FROM User a", Long.class).getSingleResult();
        }

        for (int i = 0; i < 100; i += 1) {
            try (Session session = factory.openSession()) {
                User template = session.find(User.class, random.nextInt((int) usersCount) + 1);
                String fName = template.getFirstName();
                String lName = template.getLastName();

                session.clear(); // Evict objects from memory

                var start = Instant.now();

                session.createQuery(
                        "SELECT u FROM User u WHERE u.lastName = :last AND u.firstName = :first", User.class)
                        .setParameter("last", lName)
                        .setParameter("first", fName)
                        .getResultList();

                totalMillis += Duration.between(start, Instant.now()).toMillis();
            }
        }

        System.out.println("Average Index Lookup Time: " + (totalMillis / 100) + "ms, for " + usersCount + " rows");
    }
}
