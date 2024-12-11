package database;

import userstate.State;

import java.sql.*;

public class WorkWithSQL {
    private static Connection connection; // Приватное соединение
    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;

    public WorkWithSQL(String url, String username, String password) throws SQLException {
        this.URL = url;
        this.USERNAME = username;
        this.PASSWORD = password;
        startConnection();
    }


    private void startConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
    }

    public String getInfFromComponent(String component, String table) throws SQLException {
        String sql = "SELECT inf FROM " + table + " WHERE component = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, component);
        ResultSet resultSet = statement.executeQuery();
        String result = "";
        if (resultSet.next()) {
            result = resultSet.getString("inf");
        }
        statement.close();
        return result;
    }

    public String getDetailInfFromComponent(String component, String table) throws SQLException {
        String sql = "SELECT detailInf FROM " + table + " WHERE component = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, component);
        ResultSet resultSet = statement.executeQuery();
        String result = "";
        if (resultSet.next()) {
            result = resultSet.getString("detailInf");
        }
        statement.close();
        return result;
    }

    public String getState(long chatId, String table) throws SQLException {
        String sql = "SELECT state FROM " + table + " WHERE idchat = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, chatId);
        ResultSet resultSet = statement.executeQuery();
        String result = "";
        while (resultSet.next()) {
            result = resultSet.getString("state");
        }
        return result;
    }

    public void addUser(String table, long chatId, State state) throws SQLException {
        String sql = "INSERT INTO " + table + " (idchat,state) VALUES (?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, chatId);
        statement.setString(2, String.valueOf(state));
        int rowsInserted = statement.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("Пользователь успешно добавлен!");
        }
    }

    public void updateState(String table, long chatId, State state) throws SQLException {
        String sql = "UPDATE " + table + " SET state = ? WHERE idchat = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, String.valueOf(state));
        statement.setLong(2, chatId);
        statement.executeUpdate();
    }


}