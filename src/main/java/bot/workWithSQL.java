package bot;
import java.sql.*;
public  class workWithSQL {
    public static Connection connection;
    public static PreparedStatement statement;
    public ResultSet resultSet;
    public String URL;
    public String USERNAME;
    public String PASSWORD;
    public String TABLE;

    public workWithSQL(String url, String username, String password, String table) {
        this.URL = url;
        this.USERNAME = username;
        this.PASSWORD = password;
        this.TABLE = table;
    }

    private void StartConnection() throws SQLException {
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
    public String ReturnInfFromComponent(String component) throws SQLException {
        String sql = "SELECT inf FROM " + TABLE + " WHERE component = ?";
        StartConnection();
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