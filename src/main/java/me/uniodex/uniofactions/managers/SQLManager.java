package me.uniodex.uniofactions.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.uniodex.uniofactions.UnioFactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLManager {

    private UnioFactions plugin;

    private HikariDataSource pointsDataSource;

    private String hostname;
    private String port;
    private String username;
    private String password;

    public SQLManager(UnioFactions plugin) {
        this.plugin = plugin;
        init();
        setupPool();
    }

    private void init() {
        hostname = plugin.getConfig().getString("database.hostName");
        port = plugin.getConfig().getString("database.port");
        username = plugin.getConfig().getString("database.userName");
        password = plugin.getConfig().getString("database.password");
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(
                "jdbc:mysql://" +
                        hostname +
                        ":" +
                        port
        );
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(15000);
        config.setLeakDetectionThreshold(10000);
        config.setPoolName("UnioFactionsPool");
        pointsDataSource = new HikariDataSource(config);
    }

    private Connection getConnection() throws SQLException {
        return pointsDataSource.getConnection();
    }

    private void closePool() {
        if (pointsDataSource != null && !pointsDataSource.isClosed()) {
            pointsDataSource.close();
        }
    }

    public boolean checkExist(String userName, String dbName, String tableName, String userNameColumn) {
        String QUERY = "SELECT " + userNameColumn + " FROM `" + dbName + "`.`" + tableName + "` WHERE " + userNameColumn + " = '" + userName + "';";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                return res.getString(userNameColumn) != null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSQL(String QUERY) {
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY);
            int count = statement.executeUpdate();
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int get(String userName, String dbName, String tableName, String columnName, String userNameColumn) {
        String QUERY = "SELECT " + columnName + " FROM `" + dbName + "`.`" + tableName + "` WHERE " + userNameColumn + " = '" + userName + "';";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                return res.getInt(columnName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -999999;
    }

    public String[] getSkin(String playerName) {
        String texture = "";
        String signature = "";
        String QUERY = "SELECT * FROM `genel`.`skin` WHERE `player` = '" + playerName + "';";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(QUERY);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                if (res.getString("texture") != null) {
                    texture = res.getString("texture");
                }
                if (res.getString("signature") != null) {
                    signature = res.getString("signature");
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return new String[]{texture, signature};
    }

    public void onDisable() {
        closePool();
    }
}
