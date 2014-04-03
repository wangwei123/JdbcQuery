package com.usercard.jdbc;

import java.sql.Connection;
import java.util.Map;

public class JdbcUtils
{
    /**
     * 
     * @author 王威 用于对jdbc操作数据库的简单封装，使得代码编写简单
     * 
     */

    public static JdbcQuery createNativeQuery(Connection conn, String strSql)
    {
        return new JdbcQueryImpl(conn, strSql);
    }
    
    public static JdbcQuery createNativeQuery(Connection conn, String strSql, Map<String,Object> params)
    {
        return new JdbcQueryImpl(conn, strSql, params);
    }

	public static JdbcQuery createNativeQuery(Connection conn, String strSql,
            Class classType)
    {
        return new JdbcQueryImpl(conn, strSql, classType);
    }
	
	public static JdbcQuery createNativeQuery(Connection conn, String strSql,
            Class classType, Map<String,Object> params)
    {
        return new JdbcQueryImpl(conn, strSql, classType, params);
    }
}
