# 🎬 CineBase: Hibernate Persistence & Data Engine

CineBase is a Java-based backend core designed to manage movie ratings, user subscriptions, and watch histories. It demonstrates advanced **Hibernate (JPA)** techniques, including complex relationships, automated data generation, and a custom JSON-based data migration tool.

---

## 🚀 Key Features

* **Complex Domain Model:** Managed relationships between Users, Movies, Actors, and Subscriptions.
* **Datafaker Integration:** Automated seeding of thousands of realistic records (names, movie titles, reviews).
* **JSON Snapshots:** Custom built Export/Import system using **Jackson** to backup and restore the entire database state.
* **Data Integrity:** Implemented unique constraints and optimized indexing (Functional & B-Tree).
* **Hibernate Best Practices:** Optimized fetch strategies (Lazy/Eager) and cascade operations.

---

## 🛠 Tech Stack

* **Language:** Java 17+
* **ORM:** Hibernate / JPA
* **Database:** Oracle 19c (or any RDBMS)
* **JSON Processing:** Jackson Databind
* **Data Generation:** Datafaker (Java-faker)
* **Build Tool:** Maven

---

## 📊 Database Schema

The system implements the following entities:
* **User & Subscription:** One-to-One / Many-to-One logic for user access.
* **Movie & Actor:** Many-to-Many relationship with custom join table properties.
* **Ratings:** Core logic ensuring a user can rate a movie only once.
* **Watch History:** Tracking user activity over time.

---

## 💾 Data Exchange Format

The project supports full database export to a single JSON file (example can be seen in full_export.json). This allows for easy environment migrations:

```json
{
  "users": [...],
  "movies": [...],
  "ratings": [
    {
      "id": 101,
      "score": 5,
      "review": "Masterpiece!",
      "user": 1,
      "movie": 42
    }
  ]
}
