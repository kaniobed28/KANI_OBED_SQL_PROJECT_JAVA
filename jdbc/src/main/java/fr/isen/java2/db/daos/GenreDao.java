package fr.isen.java2.db.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;

public class GenreDao {

	// Method to list all genres
	public List<Genre> listGenres() {
		List<Genre> listOfGenres = new ArrayList<>();
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			try (Statement statement = connection.createStatement()) {
				// SQL query to fetch all genres
				String sqlQuery = "SELECT * FROM genre";
				try (ResultSet results = statement.executeQuery(sqlQuery)) {
					// Iterate through the result set
					while (results.next()) {
						// Create Genre object for each result
						Genre genre = new Genre(
								results.getInt("idgenre"),
								results.getString("name")
						);
						// Add the genre object to the list
						listOfGenres.add(genre);
					}
				}
			}
		} catch (SQLException e) {
			// In case of exception, return an empty list
			return new ArrayList<>();
		}
		// Return the list of genres
		return listOfGenres;
	}

	// Method to get a genre by its name
	public Genre getGenre(String name) {
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre WHERE name=?")) {
				// Set the genre name as a parameter in the SQL query
				statement.setString(1, name);
				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						// If a result is found, create and return the Genre object
						Genre genre = new Genre();
						genre.setId(result.getInt("idgenre"));
						genre.setName(result.getString("name"));
						return genre;
					}
				}
			}
		} catch (SQLException e) {
			// Print stack trace in case of exception
			e.printStackTrace();
		}
		// Return null if genre not found
		return null;
	}

	// Method to get a genre by its ID
	public Genre getGenreById(Integer id) {
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre WHERE idgenre=?")) {
				// Set the genre ID as a parameter in the SQL query
				statement.setInt(1, id);
				try (ResultSet result = statement.executeQuery()) {
					if (result.next()) {
						// If a result is found, create and return the Genre object
						Genre genre = new Genre(
								result.getInt("idgenre"),
								result.getString("name")
						);
						return genre;
					}
				}
			}
		} catch (SQLException e) {
			// Print stack trace in case of exception
			e.printStackTrace();
		}
		// Return null if genre not found
		return null;
	}

	// Method to add a new genre
	public void addGenre(String name) {
		try (Connection connection = DataSourceFactory.getDataSource().getConnection()) {
			// SQL query to insert a new genre
			String sqlQuery = "INSERT INTO genre(name) VALUES(?)";
			try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
				// Set the genre name as a parameter in the SQL query
				statement.setString(1, name);
				// Execute the SQL query to insert the new genre
				statement.executeUpdate();
			}
		} catch (SQLException e) {
			// Print stack trace in case of exception
			e.printStackTrace();
		}
	}
}
