package com.usercard.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/***
 * 封装后的HashMap，方便获取值
 * @author 王威
 */
public class QueryMap extends HashMap<Object, Object>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String PAGE_INDEX = "pageIndex";
	private static final String PAGE_SIZE = "pageSize";
	
	public QueryMap() {
	}
	
	public QueryMap(Map<?,?> map) {
		this.setMap(map);
	}
	
	public QueryMap(Object key, Object value){
		this.put(key, value);
	}
	
	@Override
	public Object put(Object key, Object value){
		if(value instanceof java.sql.Date){
			java.sql.Date sdate = (java.sql.Date)value;
			java.util.Date udate = new Date();
			udate.setTime(sdate.getTime());
			super.put(key, udate);
		}else{
			super.put(key, value);
		}
		return value;
	}
	
	public void convertsInt(Object...keys){
		for(Object key: keys){
			Object value = this.getInteger(key);
			this.setProperty(key, value);
		}
	}
	
	public void setBeginDateTime(String key) {
		String value = this.getDateString(key);
		value += " 00:00:00";
		
		this.setProperty(key, value);
		this.convertsDate(key);
	}
	
	public void setEndDateTime(String key) {
		String value = this.getDateString(key);
		value += " 23:59:59";
		
		this.setProperty(key, value);
		this.convertsDate(key);
	}
	
	public void setProperty(Object key,Object value){
		this.put(key, value);
	}
	
	public void setKeys(Object...keys){
		for(Object key : keys){
			this.setProperty(key, null);
		}
	}
	
	public String getLeftLikeValue(Object key){
		String value = this.getString(key);
		value = value+"%";
		return value;
	}
	
	public String getLikeValue(Object key){
		String value = this.getString(key);
		value = "%"+value+"%";
		return value;
	}
	
	public void emptyAllValues(){
		for(Object key : this.keySet()){
			this.put(key, null);
		}
	}
	
	public Boolean isEmpty(Object key){
		Log.i("key=>"+key);
		if(super.containsKey(key)){
			Object value = super.get(key);
			if(value == null){
				Log.i(key+" 1 true");
				return true;
			}else if("".equals(String.valueOf(value).trim())){
				Log.i(key+" 2 true");
				return true;
			}
			Log.i(key+" 3 false");
			return false;
		}
		Log.i(key+" 4 true");
		return true;
	}
	
	public Boolean isNotEmpty(Object key){
		return !this.isEmpty(key);
	}
	
	public Object getObject(Object key){
		return super.get(key);
	}
	
	public <T> T getClsObject(Object key, Class<T> objClass){
		return (T)this.get(key);
	}
	
	public String getString(Object key){
		return super.get(key).toString();
	}
	
	public Integer getInteger(Object key){
		Object value = this.get(key);
		if("".equals(value) || value == null)return null;
		
		return Integer.parseInt(value.toString());
	} 
	
	public Double getDouble(Object key){
		return Double.parseDouble(this.getString(key));
	}
	
	public Float getFloat(Object key){
		return Float.parseFloat(this.getString(key));
	}
	
	public Boolean getBoolean(Object key){
		return Boolean.parseBoolean(this.getString(key));
	}
	
	public void convertsDateTime(Object...keys){
		for(Object key: keys){
			if(this.isEmpty(key)) continue;
			
			Object value = this.get(key);
			String datePattern = "yyyy-MM-dd HH:mm:ss";
			
			value = this.getDate(key, datePattern);
			this.setProperty(key, value);
		}
	}
	
	public void convertsDate(Object...keys){
		for(Object key: keys){
			if(this.isEmpty(key)) continue;
			
			Object value = this.get(key);
			String datePattern = "yyyy-MM-dd";
			
			value = this.getDate(key, datePattern);
			this.setProperty(key, value);
		}
	}
	
	public Date getDate(Object key,String datePattern){
		if(datePattern == null) datePattern = "yyyy-MM-dd";
		SimpleDateFormat df = new SimpleDateFormat(datePattern, Locale.UK);
		Object value = super.get(key);
		if(value == null){
			return null;
		}else if(value instanceof Date){
			return (Date)value;
		}else if(value instanceof String){
	    	try {
				return df.parse(this.getString(key));
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
		return (Date)value;
	}
	
	public Date getDate(Object key){
		return this.getDate(key, null);
	}
	
	public String getDateString(Object key){
		return this.getDateString(key, null);
	}
	
	public String getDateString(Object key,String datePattern){
		if(datePattern == null) datePattern = "yyyy-MM-dd";
		SimpleDateFormat df = new SimpleDateFormat(datePattern, Locale.UK);
		Object value = super.get(key);
		if(value == null || "".equals(value)){
			return "";
		}else if(value instanceof Date){
			return df.format(value);
		}else if(value instanceof String){
			try {
				Date date = df.parse(this.getString(key));
				return df.format(date);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}
	
	//从http requestMap中获取值
	public void setMap(Map<?,?> map) {
		for(Object key : map.keySet()) {
			Object[] value = (Object[])map.get(key);
			this.setProperty(key, value[0]);
		}
	}
	
	public Integer getPageIndex() {
		return this.getInteger(PAGE_INDEX);
	}
	
	public Integer getPageSize() {
		return this.getInteger(PAGE_SIZE);
	}
}
