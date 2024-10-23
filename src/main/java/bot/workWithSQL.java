package bot;
import java.sql.*;
public  class workWithSQL {
    public static Connection connection;
    public static Statement statement;
    public ResultSet resultSet;
    public String value;
    public String URL;
    public  String USERNAME;
    public  String PASSWORD;
    public  String TABLE;

     public workWithSQL(String url, String username, String password, String table) {
         this.URL= url;
         this.USERNAME = username;
         this.PASSWORD = password ;
         this.TABLE = table ;
     }
    public void StartConnection(String SQL) throws SQLException {
        connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);
        statement = connection.createStatement();
        resultSet = statement.executeQuery(SQL);

    }
    public void ReturnInfFromComponent(String component) throws SQLException {
        value = "select inf from " + TABLE + " where component='" + component + "';";
        System.out.println(value);
        StartConnection(value);
        while (resultSet.next()) {
            System.out.println(resultSet.getString("inf"));
        }
    }
}
