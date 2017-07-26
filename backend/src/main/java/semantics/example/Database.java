package semantics.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Database from "wine.rdf" {
  private final Connection conn;

  public Database() {
    try {
      conn = DriverManager.getConnection("jdbc:sqlite:winesearch.db");
      conn.createStatement().execute(
          "CREATE TABLE IF NOT EXISTS ratings (" +
          "  wine   TEXT    NOT NULL," +
          "  author TEXT    NOT NULL," +
          "  rating INTEGER NOT NULL," +
          "  review TEXT," +
          "  PRIMARY KEY (wine, author)," +
          "  CHECK (rating BETWEEN 1 AND 5))");
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


  public void rate(«:Wine» wine, String author, int rating, String review) {
    try {
      PreparedStatement stmt = conn.prepareStatement(
          "INSERT OR REPLACE INTO ratings VALUES (?, ?, ?, ?)");

      stmt.setString(1, wine.getIri());
      stmt.setString(2, author);
      stmt.setInt   (3, rating);
      stmt.setString(4, review);

      stmt.executeUpdate();
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


  public List<Map<String, Object>> getRatingsFor(«:Wine» wine) {
    try {
      PreparedStatement stmt = conn.prepareStatement(
          "SELECT author, rating, review FROM ratings WHERE wine = ?");
      stmt.setString(1, wine.getIri());

      List<Map<String, Object>> ratings = new ArrayList<>();

      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          Map<String, Object> record = new HashMap<>();
          record.put("author", rs.getString("author"));
          record.put("rating", rs.getInt("rating"));
          record.put("review", rs.getString("review"));
          ratings.add(record);
        }
      }

      return ratings;
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
