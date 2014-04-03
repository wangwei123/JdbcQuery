JdbcQuery
=========

JdbcQuery is easy to process jdbc operation, get result list, list map, list array, and very easy to call a produce. if you are using hibernate, I think you need JdbcQuery to make up hibernate, because hibernate is not enough for jdbc.

1. code example for call procedure:
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
	P_ErrorInfo is a out parameter, for store error message
	P_OutData is a CURSOR

```java
String strError = query.getString("P_ErrorInfo");
List<TaskBarCounter> dataList = query.getResultList("P_OutData",TaskBarCounter.class);
query.closeCallableStatement();
```	
	### you can very easy to get result list.

	    
2. code example for call sql command:
-------
```java
public Result findCollect(final QueryMap qMap) {
	final StringBuilder sb = new StringBuilder();
	StringBuilder sbWhere = new StringBuilder();
	
	// select fields
	sb.append("select c.store_id, s.name,SUM(c.total_recharge) as total_recharge,");
	sb.append("SUM(c.total_payment) as total_payment,");
	sb.append("SUM(c.total_reversal) as total_reversal ");
	
	// select where
	sbWhere.append(" from collect c inner join store s on c.store_id = s.id ");
	sbWhere.append(" where c.created >= :start ");
	sbWhere.append(" and c.created <= :end "); 
	sbWhere.append(" and s.parent_id = :parentId ");
	sbWhere.append(" and s.name like :storeName");
	sbWhere.append(" GROUP BY c.store_id,s.name ");
	
	// select count
	final String countSQL = "select count(c.id)" + sbWhere.toString();
	
	// page
	sb.append(sbWhere).append("limit :pageStart,:pageSize");
	
	// QueryMap is a strong Map, it support easy to type convert, For example: String to Integer、String to Date ect.
	qMap.convertsInt("pageIndex", "pageSize");

	// count to get pageStart parameter for sql: limit :pageStart,:pageSize
	qMap.setProperty("pageStart", qMap.getPageIndex()*qMap.getPageSize());
	
	// set parentId parameter for sql: and s.parent_id = :parentId
	qMap.setProperty("parentId", 5); 
	
	// getLikeValue can get a format string, For example: test => '%test%'
	qMap.setProperty("storeName", qMap.getLikeValue("storeName")); 
	
	final Result result = new Result();
	super.getCurrentSession().doWork(new Work() {  
	    public void execute(Connection connection) { 
	    	// get hibernate's datasource connection
	    	// set methond's params: connection, sql, params
	    	JdbcQuery query = JdbcUtils.createNativeQuery(connection, countSQL, qMap);
	    	// get count
	    	int count = query.getCount();
	    	
	    	// set methond's params: connection, sql, params
	    	JdbcQuery querys = JdbcUtils.createNativeQuery(connection, sb.toString(), qMap);
	    	// get result list<? extends HashMap>
	    	List<?> list = querys.getResultList();
	    	
	    	// set the count and list to result.
	    	result.setTotal(count);
	    	result.setData(list);
	    	
	    	Log.i("result", result);
	    }
	});
	
	return result;
} 
```	
	
	
