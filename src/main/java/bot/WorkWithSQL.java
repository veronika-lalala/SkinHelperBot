package bot;

import java.sql.*;

public class WorkWithSQL {
    private static Connection connection; // Приватное соединение
    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;
    private final String TABLE;

    public WorkWithSQL(String url, String username, String password, String table) throws SQLException {
        this.URL = url;
        this.USERNAME = username;
        this.PASSWORD = password;
        this.TABLE = table;
        startConnection(); // Подключение к БД при создании объекта
    }

    private void startConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
    }

    public String getInfFromComponent(String component) throws SQLException {
        String sql = "SELECT inf FROM " + TABLE + " WHERE component = ?";
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

    public String getDetailInfFromComponent(String component) throws SQLException {
        String sql = "SELECT detailInf FROM " + TABLE + " WHERE component = ?";
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



}