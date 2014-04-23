JdbcQuery
=========

JdbcQuery是一个对JDBC操作进行封装，使得在JAVA中执行复杂的SQL语句和存储过程变得像hibernate一样的简单,并能方便的将结果集以List、Map、Array的形式返回。

1. 调用存储过程示例:
-------
```java
String strSql = "{call callcenter_pack.Get_TaskCaseCount(:P_UserID,:P_ErrorInfo,:P_OutData)}";
JdbcQuery query = JdbcUtils.createNativeQuery(super.getConnection(), strSql);

query.setParameter("P_UserID", 5);
query.setOutParameter("P_ErrorInfo", OracleTypes.VARCHAR);	    
query.setOutParameter("P_OutData", OracleTypes.CURSOR);
query.excuteProcedure();
```
	### params: P_UserID、 P_ErrorInfo、 P_OutData.
	P_ErrorInfo 是一个输出参数, 用于返回存储过程中的错误信息
	P_OutData 是一个游标

```java
String strError = query.getString("P_ErrorInfo");
List<TaskBarCounter> dataList = query.getResultList("P_OutData",TaskBarCounter.class);
query.closeCallableStatement();
```	
	### 可以方便的获取一个List类型的结果

	    
2. 调用SQL语句示例:
-------
```java

public Result findCollect(final QueryMap qMap) {

	final StringBuilder sb = new StringBuilder();
	StringBuilder sbWhere = new StringBuilder();
	
	// 汇总查询所需的列
	sb.append("select c.store_id, s.name,SUM(c.total_recharge) as total_recharge,");
	sb.append("SUM(c.total_payment) as total_payment,");
	sb.append("SUM(c.total_reversal) as total_reversal ");
	
	// 查询条件
	sbWhere.append(" from collect c inner join store s on c.store_id = s.id ");
	sbWhere.append(" where c.created >= :start ");
	sbWhere.append(" and c.created <= :end "); 
	sbWhere.append(" and s.parent_id = :parentId ");
	sbWhere.append(" and s.name like :storeName");
	sbWhere.append(" GROUP BY c.store_id,s.name ");
	
	// 查询数量
	final String countSQL = "select count(c.id)" + sbWhere.toString();
	
	// 分页
	sb.append(sbWhere).append("limit :pageStart,:pageSize");
	
	// QueryMap 是一个功能强大的HashMap, 提供了各种类型的转换, 
	// 例如: String 转 Integer、String 赚 Date 等等.
	qMap.convertsInt("pageIndex", "pageSize");

	// 计算分页参数: limit :pageStart,:pageSize
	qMap.setProperty("pageStart", qMap.getPageIndex()*qMap.getPageSize());
	
	// 设置参数parentId 例如SQL: and s.parent_id = :parentId
	qMap.setProperty("parentId", 5); 
	
	// getLikeValue可以格式化为 Like形式，例如: test => '%test%'
	qMap.setProperty("storeName", qMap.getLikeValue("storeName")); 
	
	final Result result = new Result();
	
    // 获取hibernate数据源连接,基类中获取，通过super.getSession().connection
    // 设置方法的参数：数据库连接，SQL语句，参数
    JdbcQuery query = JdbcUtils.createNativeQuery(super.getSession().connection, countSQL, qMap);
    
    // 获取数量
    int count = query.getCount();
    
    // 设置方法的参数：数据库连接，SQL语句，参数
    JdbcQuery querys = JdbcUtils.createNativeQuery(super.getSession().connection, sb.toString(), qMap);
    
    // 获取结果 List<? extends HashMap>
    List<?> list = querys.getResultList();
    
    // 将结果存入result中返回
    result.setTotal(count);
    result.setData(list);
    
    Log.i("result", result);
	
    return result;
}


//===========================================================================================	
//============= Hibernate4.x 获取数据库连接方式 begin ============================================
	
	super.getCurrentSession().doWork(new Work() {  
	    public void execute(Connection connection) { 
	    	// 获取hibernate数据源连接：
    		// 设置方法的参数：数据库连接，SQL语句，参数
	    	JdbcQuery query = JdbcUtils.createNativeQuery(connection, countSQL, qMap);
	    	
	    	// 获取数量
	    	int count = query.getCount();
	    	
	    	//设置方法的参数：数据库连接，SQL语句，参数
	    	JdbcQuery querys = JdbcUtils.createNativeQuery(connection, sb.toString(), qMap);
	    	// 获取结果 List<? extends HashMap>
	    	List<?> list = querys.getResultList();
	    	
	    	// 将结果存入result中返回
	    	result.setTotal(count);
	    	result.setData(list);
	    	
	    	Log.i("result", result);
	    }
	});
//============= Hibernate4.x 获取数据库连接方式 end ============================================
	
	return result;
} 
```	
	
	
