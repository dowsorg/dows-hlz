package test;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.*;

public class MainTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        MysqlDataSource dataSource = new MysqlDataSource();
        String dateBase = "dows_hep";
        String url = "jdbc:mysql://192.168.1.60:3309/";
        String user = "root";
        String password = "123456";

        try {
            Connection conn = DriverManager.getConnection(url+dateBase, user, password);
            //查询数据库，下说有表
            String sql = "SELECT TABLE_NAME,TABLE_COMMENT FROM information_schema.`TABLES` WHERE TABLE_SCHEMA="+dateBase;
            System.out.println(conn);
            Statement st = conn.createStatement();
            ResultSet resultSet = st.executeQuery(sql);
            while(resultSet.next()){
                //表名
                String tableName = resultSet.getString("TABLE_NAME");
                String tableComment = resultSet.getString("TABLE_COMMENT");
                System.out.println("表名:"+tableName+"  注释:" +tableComment);
            }
            resultSet.close();
            st.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {

        }
    }
}
