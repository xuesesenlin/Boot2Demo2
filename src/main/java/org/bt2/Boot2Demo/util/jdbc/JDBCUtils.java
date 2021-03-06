package org.bt2.Boot2Demo.util.jdbc;

import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @author ld
 * @name
 * @table
 * @remarks
 */
public class JDBCUtils {

    /**
     * 关闭statement和connection
     *
     * @param ps
     * @param conn
     */
    public static void release(PreparedStatement ps, Connection conn) {
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void release(ResultSet result, PreparedStatement ps, Connection conn) {
        try {
            if (result != null) {
                result.close();
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //获取jdbc的链接

    /**
     * 用于创建jdbc链接的工具类对象
     *
     * @return
     */
    public static Connection getConnetions() {
        Connection conn = null;
        String driverClass = null;
        String jdbcUrl = null;
        String user = null;
        String password = null;

        try {
            //读取配置文件中的配置
            InputStream is = JDBCUtils.class.getClassLoader().getResourceAsStream("db.properties");
            Properties properties = new Properties();
            properties.load(is);
            driverClass = properties.getProperty("driver");
            jdbcUrl = properties.getProperty("jdbcUrl");
            user = properties.getProperty("user");
            password = properties.getProperty("password");
            //注册驱动程序
            Class.forName(driverClass);
            //实际应该这样写(由于对应的应用程序中有一个对应的静态代码块，自动回将驱动的类对象进行驱动加载)
            //DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());

            conn = DriverManager.getConnection(jdbcUrl+"?useUnicode=true&characterEncoding=utf-8&useSSL=false", user, password);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return conn;
    }
}
