package org.example.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.Tables.Actor;
import org.example.Tables.Genre;
import org.example.dao.MovieDao;
import org.example.Tables.Movie;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MovieController {

    // Table data
    @FXML private TableView<Movie> movieTable;
    @FXML private TableColumn<Movie, String> titleCol;
    @FXML private TableColumn<Movie, String> releaseCol;
    @FXML private TableColumn<Movie, String> languageCol;
    @FXML private TableColumn<Movie, String> genresCol;
    @FXML private TableColumn<Movie, String> actorsCol;

    // Multiple selection data
    @FXML private ListView<Genre> genreListView;
    @FXML private ListView<Actor> actorListView;

    // Inputs
    @FXML private TextField titleInput;
    @FXML private TextField languageInput;
    @FXML private TextField yearInput;
    @FXML private DatePicker releaseDatePicker;

    private MovieDao movieDao;

    // Here we pass sessionFactory from Main
    public void setMovieDao(MovieDao movieDao) {
        this.movieDao = movieDao;
        loadData();

        // Allow selection of multiple
        genreListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        actorListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Make list display names
        genreListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Genre item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getGenre_name());
            }
        });

        actorListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Actor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getFullName());
            }
        });
    }

    @FXML
    public void initialize() {
        // Table data
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        languageCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLanguage()));

        releaseCol.setCellValueFactory(data -> {
            if (data.getValue().getRelease() != null) {
                return new SimpleStringProperty(data.getValue().getRelease().toString());
            }
            return new SimpleStringProperty("");
        });

        // Complex fields (with Many -> Many), we convert Set<> to String
        genresCol.setCellValueFactory(data -> {
            String genresStr = data.getValue().getGenres().stream()
                    .map(Genre::getGenre_name)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(genresStr);
        });

        actorsCol.setCellValueFactory(data -> {
            String actorsStr = data.getValue().getActors().stream()
                    .map(Actor::getFullName)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(actorsStr);
        });

        // Logic for filling movie details (right plane) when clicking in the table
        movieTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Filling in the text fields
                titleInput.setText(newValue.getTitle());
                releaseDatePicker.setValue(newValue.getRelease());
                languageInput.setText(newValue.getLanguage());

                // Clearing the previous selection in lists
                genreListView.getSelectionModel().clearSelection();
                actorListView.getSelectionModel().clearSelection();

                // Highlighting genres that belong to this film
                for (Genre movieGenre : newValue.getGenres()) {
                    for (int i = 0; i < genreListView.getItems().size(); i++) {
                        Genre listGenre = genreListView.getItems().get(i);

                        if (listGenre.getId().equals(movieGenre.getId())) {
                            genreListView.getSelectionModel().select(i); // Виділяємо по індексу
                            break;
                        }
                    }
                }

                // Highlighting actors who belong in this film
                for (Actor movieActor : newValue.getActors()) {
                    for (int i = 0; i < actorListView.getItems().size(); i++) {
                        Actor listActor = actorListView.getItems().get(i);

                        if (listActor.getId().equals(movieActor.getId())) {
                            actorListView.getSelectionModel().select(i); // Виділяємо по індексу
                            break;
                        }
                    }
                }
            }
        });
    }

    private void loadData() {
        ObservableList<Movie> movieList = FXCollections.observableArrayList(movieDao.getAllMovies());
        movieTable.setItems(movieList);

        genreListView.setItems(FXCollections.observableArrayList(movieDao.getAllGenres()));
        actorListView.setItems(FXCollections.observableArrayList(movieDao.getAllActors()));
    }

    @FXML
    private void handleAdd() {
        Movie movie = new Movie();
        movie.setTitle(titleInput.getText());
        movie.setRelease(releaseDatePicker.getValue());
        movie.setLanguage(languageInput.getText());

        // Getting selected items from lists
        Set<Genre> selectedGenres = new HashSet<>(genreListView.getSelectionModel().getSelectedItems());
        Set<Actor> selectedActors = new HashSet<>(actorListView.getSelectionModel().getSelectedItems());

        movie.setGenres(selectedGenres);
        movie.setActors(selectedActors);

        movieDao.saveOrUpdate(movie);
        loadData(); // Update main table
    }

    public void handleUpdate(ActionEvent actionEvent) {
        // Get the selected movie from the table
        Movie selectedMovie = movieTable.getSelectionModel().getSelectedItem();

        if (selectedMovie == null) {
            showAlert("Error", "Please select a movie to update!");
            return;
        }

        selectedMovie.setTitle(titleInput.getText());
        selectedMovie.setRelease(releaseDatePicker.getValue());
        selectedMovie.setLanguage(languageInput.getText());

        // Take the selected elements from the lists and put them in the Set
        selectedMovie.setGenres(new HashSet<>(genreListView.getSelectionModel().getSelectedItems()));
        selectedMovie.setActors(new HashSet<>(actorListView.getSelectionModel().getSelectedItems()));

        movieDao.saveOrUpdate(selectedMovie);

        loadData();
        clearForm();
    }

    public void handleDelete(ActionEvent actionEvent) {
        Movie selectedMovie = movieTable.getSelectionModel().getSelectedItem();

        if (selectedMovie == null) {
            showAlert("Error", "Please select a movie to delete!");
            return;
        }

        // Ask the user for confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm deletion");
        alert.setHeaderText("Deleting a movie");
        alert.setContentText("Are you sure you want to delete the movie: " + selectedMovie.getTitle() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            movieDao.delete(selectedMovie);

            loadData();
            clearForm();
        }
    }

    @FXML
    private void clearForm() {
        titleInput.clear();
        releaseDatePicker.setValue(null);
        languageInput.clear();
        genreListView.getSelectionModel().clearSelection();
        actorListView.getSelectionModel().clearSelection();
        movieTable.getSelectionModel().clearSelection();
    }

    // Helper method for beautiful error messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
