package net.letcute.wanted;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class Database {

    private Connection connection;
    private final String databasePath;
    private final Logger logger;

    public Database(File pluginFolder, String databaseName, Logger logger) {
        File dbFile = new File(pluginFolder, databaseName + ".db");
        this.databasePath = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        this.logger = logger;
    }

    public void openConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(databasePath);
                logger.log(Level.INFO, "SQLite connection established.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Could not establish SQLite database connection.", e);
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                openConnection();
            }
            return connection;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting database connection.", e);
        }
        return null;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.log(Level.INFO, "SQLite connection closed.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error while closing SQLite connection.", e);
        }
    }

    public ResultSet executeQuery(String query, Object... params) {
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            setParameters(statement, params);
            return statement.executeQuery();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing query: " + query, e);
            return null;
        }
    }

    public int executeUpdate(String query, Object... params) {
        try {
            if (connection == null || connection.isClosed()) {
                openConnection();
            }
            PreparedStatement statement = connection.prepareStatement(query);
            setParameters(statement, params);
            return statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing update: " + query, e);
            return 0;
        }
    }

    private void setParameters(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            int index = i + 1;

            switch (param.getClass().getSimpleName()) {
                case "Integer" -> statement.setInt(index, (Integer) param);
                case "String" -> statement.setString(index, (String) param);
                case "Double" -> statement.setDouble(index, (Double) param);
                case "Boolean" -> statement.setBoolean(index, (Boolean) param);
                case "Float" -> statement.setFloat(index, (Float) param);
                case "Long" -> statement.setLong(index, (Long) param);
                default -> statement.setObject(index, param);
            }
        }
    }

    public boolean updatePlayer(String playerName, int money) {
        String query = "UPDATE players SET money = ? WHERE name = ?";
        return executeUpdate(query, money, playerName) > 0;
    }

    public boolean addMoneyPlayer(String playerName, int money) {
        String query = "UPDATE players SET money = money + ? WHERE name = ?";
        return executeUpdate(query, money, playerName) > 0;
    }

    public boolean addPlayer(String playerName, int initialMoney) {
        String query = "INSERT INTO players (name, money) VALUES (?, ?)";
        return executeUpdate(query, playerName, initialMoney) > 0;
    }

    public boolean removePlayer(String playerName) {
        String query = "DELETE FROM players WHERE name = ?";
        return executeUpdate(query, playerName) > 0;
    }

    public Integer getPlayerMoney(String playerName) {
        String query = "SELECT money FROM players WHERE name = ?";
        ResultSet resultSet = executeQuery(query, playerName);

        try {
            if (resultSet != null && resultSet.next()) {
                return resultSet.getInt("money");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching player data for " + playerName, e);
        }
        return null;
    }

    public boolean isPlayerExists(String playerName) {
        String query = "SELECT COUNT(*) FROM players WHERE name = ?";
        ResultSet resultSet = executeQuery(query, playerName);

        try {
            return resultSet != null && resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking if player exists for " + playerName, e);
        }
        return false;
    }

    public List<TopPlayerMoney> getTopPlayers() {
        List<TopPlayerMoney> topPlayers = new ArrayList<>();
        String query = "SELECT name, money FROM players ORDER BY money DESC LIMIT 10";
        ResultSet resultSet = executeQuery(query);

        try {
            while (resultSet != null && resultSet.next()) {
                String name = resultSet.getString("name");
                int money = resultSet.getInt("money");
                topPlayers.add(TopPlayerMoney.builder().name(name).money(money).build());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching top players", e);
        }

        return topPlayers;
    }

    @AllArgsConstructor
    @Data
    @Builder
    public static class TopPlayerMoney {
        private String name;
        private int money;
    }
}
