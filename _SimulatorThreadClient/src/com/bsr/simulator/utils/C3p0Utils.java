package com.bsr.simulator.utils;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


import java.util.List;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * �������ӳ� c3p0 �����ࣺComboPooledDataSource
 * 
 */
public class C3p0Utils {
	private static DataSource ds = null;
	static {
		// default read c3p0-config.xml
		ds = new ComboPooledDataSource();
		// set the file name 'abc'
		// ds = new ComboPooledDataSource("abc");

	}

	public static DataSource getDs() {
		return ds;
	}

	public static Connection getConn() {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	/**
	 * release connection
	 * @param Connection conn
	 * @param PreparedStatement pstmt
	 * @param ResultSet rs
	 */
	public static void releaseResource(Connection conn,PreparedStatement pstmt,ResultSet rs){
		try {//�ر�˳��rs,pstmt,conn
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		} catch (Exception e) {
		}
	}
	/**
	 * release connection
	 * @param Connection conn
	 * @param Statement stmt
	 * @param ResultSet rs
	 */
	public static void releaseResource(Connection conn, Statement stmt,ResultSet rs) {
		try {//�ر�˳��rs,stmt,conn
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		} catch (Exception e) {
		}
	}
	/**
	 * 根据条件查询数量
	 * @param sql
	 * @return num
	 * @throws SQLException
	 */
	public static int getCount(String sql){
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		int num = 0;
		try {
			conn = getConn();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next()) {
				num = Integer.parseInt(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			releaseResource(conn, st, rs);
		}
		return num;
	}
	/**
	 * ִ����ݿ���£�������insert,update,delete�����ز������?
	 * @param sql
	 * @return boolean
	 * @throws SQLException
	 */
	public static boolean executeUpdate(String sql){
		Connection conn = null;
		Statement st = null;
		boolean result = true;
		try {
			conn = getConn();
			st = conn.createStatement();
			st.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			releaseResource(conn, st, null);
		}
		return result;
	}

	/**
	 * JDBC��ȡ��ݣ������֯��HashMap��ʽ��keyΪ����valueΪ���?
	 * һ��HashMap��Ӧ�ڲ�ѯ����һ�����?
	 * @param sql
	 * @return List
	 * @throws Exception
	 */
	public static List<HashMap<String, String>> executeQuery(String sql){
		Connection conn=null;
        Statement st=null;
        ResultSet rs=null;
        List<HashMap<String,String>> result = new ArrayList<HashMap<String,String>>();
        try{
        	conn=getConn();
        	st=conn.createStatement();
        	rs=st.executeQuery(sql);
        	ResultSetMetaData rsmd = rs.getMetaData();
        	while(rs.next())
        	{
        		HashMap<String,String> map = new HashMap<String,String>();
        		for(int i=0;i<rsmd.getColumnCount();i++)
        		{
        			map.put(rsmd.getColumnLabel(i+1), rs.getString(i+1));
        		}
        		result.add(map);
        	}
        }catch (Exception e) {
			e.printStackTrace();
		}finally{
        	releaseResource(conn, st, rs);
        }
        return result;
	}
	/**
	 * 可插入任意个�?value参数,但sql中的问号个数务必于value个数相同
	 * @param sql
	 * @param value
	 * @return
	 * @throws SQLException
	 * boolean
	 */
	public static boolean insertData(String sql,String...value){
		System.out.println(Arrays.toString(value));
		Connection conn = null;
		PreparedStatement prst = null;
		boolean ret = false;
		int num = 0;
		try {
			conn = getConn();
			prst = conn.prepareStatement(sql);
			for(int i=0;i<value.length;i++){
				prst.setString(i+1, value[i]);
			}
			num = prst.executeUpdate();
			if(num>0){
				ret = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		} finally {
			releaseResource(conn, prst, null);
		}
		return ret;
	}
	/**
	 * ���Ӳ���
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {
		String sql = "insert into usertbl(patientID,deviceID,company,pname) values(?,?,?,?)";
		Connection conn = getConn();
		for(int i=10000;i<60000;i++){
			insertData(sql, "200000"+i,"MINA"+i,"MINA","U"+i);
		}
		conn.close();
	}
}
