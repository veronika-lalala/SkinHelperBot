package bot;
import java.sql.*;
public  class WorkWithSQL {
    public static Connection connection;
    public static PreparedStatement statement;
    public String URL;
    public String USERNAME;
    public String PASSWORD;
    public String TABLE;

    public WorkWithSQL(String url, String username, String password, String table) {
        this.URL = url;
        this.USERNAME = username;
        this.PASSWORD = password;
        this.TABLE = table;
    }

    private void startConnection() throws SQLException {
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
    public String getInfFromComponent(String component) throws SQLException {
        String sql = "SELECT inf FROM " + TABLE + " WHERE component = ?";
        startConnection();
        statement = connection.prepareStatement(sql);
        statement.setString(1, component);
        ResultSet resultSet = statement.executeQuery();
        String result = "";
        while (resultSet.next()) {
            result = resultSet.getString("inf");
        }
        return result;
    }
}