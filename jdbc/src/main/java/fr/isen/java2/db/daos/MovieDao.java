package fr.isen.java2.db.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDao {

    GenreDao genreDao = new GenreDao();

    // Method to list all movies
    public List<Movie> listMovies() {
        List<Movie> listOfMovies = new ArrayList<>();

        try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
            try (Statement statement = connection.createStatement()) {
                // SQL query to fetch movies along with their genres
                String sqlQuery = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre";
                try (ResultSet results = statement.executeQuery(sqlQuery)) {
                    while (results.next()) {
                        // Get genre_id
                        Integer genreId = results.getInt("genre_id");
                        // Retrieve genre object using genre_id
                        Genre genre = genreDao.getGenreById(genreId);

                        // Create Movie Object 
                        Movie movie = new Movie(
                                results.getInt("idmovie"),
                                results.getString("title"),
                                results.getDate("release_date").toLocalDate(),
                                genre,
                                results.getInt("duration"),
                                results.getString("director"),
                                results.getString("summary")
                        );

                        // Add movie object to list
                        listOfMovies.add(movie);
                    }

                }
            }
        } catch (SQLException e) {
            return null;
        }
        return listOfMovies;
    }

    // Method to list movies by a specific genre
    public List<Movie> listMoviesByGenre(String genreName) {
        List<Movie> listOfMovies = new ArrayList<>();
        try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
            // SQL query to fetch movies of a particular genre
            String sqlQuery = "SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name=?";
            try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
                statement.setString(1, genreName);
                try (ResultSet results = statement.executeQuery()) {
                    while (results.next()) {
                        Integer genreId = results.getInt("genre_id");
                        // Retrieve genre object using genre_id
                        Genre genre = genreDao.getGenreById(genreId);

                        // Create Movie object
                        Movie movie = new Movie(
                                results.getInt("idmovie"),
                                results.getString("title"),
                                results.getDate("release_date").toLocalDate(),
                                genre,
                                results.getInt("duration"),
                                results.getString("director"),
                                results.getString("summary")
                        );
                        // Add movie object to list
                        listOfMovies.add(movie);
                    }
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return listOfMovies;
    }

    // Method to add a new movie
    public Movie addMovie(Movie movie) {
        Movie newMovie = new Movie();
        try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
            // SQL query to insert a new movie
            String sqlQuery = "INSERT INTO movie(title,release_date,genre_id,duration,director,summary) VALUES(?,?,?,?,?,?)";
            try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
                // Fill in Values with relevant data from 'movie' (object) parameter.
                statement.setString(1, movie.getTitle());
                statement.setObject(2, movie.getReleaseDate());
                statement.setInt(3, movie.getGenre().getId());
                statement.setInt(4, movie.getDuration());
                statement.setString(5, movie.getDirector());
                statement.setString(6, movie.getSummary());

                // Execute the insertion query
                statement.executeUpdate();

                // Retrieve the generated keys (specifically the generated ID)
                try (ResultSet ids = statement.getGeneratedKeys()) {
                    if (ids.next()) {
                        int generatedId = ids.getInt(1);
                        // Update the ID of the movie object with the generated ID
                        movie.setId(generatedId);
                        // Update the newMovie object with the generated ID and other details
                        newMovie.setId(generatedId);
                        newMovie.setTitle(movie.getTitle());
                        newMovie.setReleaseDate(movie.getReleaseDate());
                        newMovie.setGenre(movie.getGenre());
                        newMovie.setDirector(movie.getDirector());
                        newMovie.setDuration(movie.getDuration());
                        newMovie.setSummary(movie.getSummary());
                    } else {
                        throw new SQLException("Failed to Create Movie.");
                    }
                }
            }
        } catch (SQLException e) {
            newMovie = null;
        }
        return newMovie;
    }
}
