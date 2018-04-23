package org.bt2.Boot2Demo.util.jdbc;

import org.bt2.Boot2Demo.publics.login.model.LoginModel;
import org.bt2.Boot2Demo.publics.token.model.TokenModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ld
 * @name
 * @table
 * @remarks
 */
public class JDBC {
    public List<TokenModel> queryToken(String sql) {
        TokenModel model = null;
        List<TokenModel> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            //创建连接
            conn = JDBCUtils.getConnetions();
            //创建prepareStatement对象，用于执行SQL
            ps = conn.prepareStatement(sql);
            //获取查询结果集
            result = ps.executeQuery();
            while (result.next()) {
                model = new TokenModel(result.getString(1),
                        result.getString(2),
                        result.getString(3),
                        result.getLong(4),
                        result.getString(5));
                list.add(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(result, ps, conn);
        }
        return list;
    }

    public List<LoginModel> queryToken2(String sql) {
        LoginModel model = null;
        List<LoginModel> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            //创建连接
            conn = JDBCUtils.getConnetions();
            //创建prepareStatement对象，用于执行SQL
            ps = conn.prepareStatement(sql);
            //获取查询结果集
            result = ps.executeQuery();
            while (result.next()) {
                model = new LoginModel();
                model.setUsername(result.getString(1));
                model.setPassword(result.getString(2));
                model.setTypes(result.getInt(3));
                list.add(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.release(result, ps, conn);
        }
        return list;
    }

    public int update(String sql) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //创建数据库连接
            conn = JDBCUtils.getConnetions();
            //创建执行SQL的prepareStatement对象
            ps = conn.prepareStatement(sql);
            //用于增删改操作
            int result = ps.executeUpdate();
            return result;
        } catch (Exception e) {
            System.out.println("出现异常1=" + e.toString());
            return 0;
        } finally {
            JDBCUtils.release(ps, conn);
        }
    }
}
