

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class crawler {

	public static void main(String args[]) throws Exception {
		String frontpage = "http://www.sina.com.cn/";
		Connection conn = null;
		//connect the MySQL database
		try{
			Class.forName("com.mysql.jdbc.Driver");
			String dburl = "jdbc:mysql://localhost:3306?useUnicode=true&characterEncoding=utf8";
			conn = DriverManager.getConnection(dburl, "root", "welcome");
			System.out.println("connection build");
		}catch(SQLException e){
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		
		String sql = null;
		String url = frontpage;
		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;
		if(conn != null){
			// create database and table will be needed
			try{
				sql = "CREATE DATABASE IF NOT EXISTS crawler";
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				sql = "USE crawler";
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				sql = "create table if not exists record (recordID int(5) not null auto_increment, URL text not null, crawled tinyint(1) not null, primary key(recordID)) engine=InnoDB DEFAULT CHARSET=utf8";
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);
				sql = "create table if not exists tags (tagnum int(4) not null auto_increment, tagname text not null, primary key (tagnum)) engine=InnoDB DEFAULT CHARSET=utf8";
				stmt = conn.createStatement();
				stmt.executeUpdate(sql);
			}catch(SQLException e){
				e.printStackTrace();
			}
			
			while(true){
				httpGet.getByString(url, conn);
				count++;
				sql = "UPDATE record SET crawled = 1 WHERE URL = '" +url+"'";
				stmt = conn.createStatement();
				if(stmt.executeUpdate(sql)>0){
					sql = "SELECT * FROM record WHERE crawled = 0";
					stmt = conn.createStatement();
					rs = stmt.executeQuery(sql);
					if(rs.next()){
						url = rs.getString(2);
					}else {
						break;
					}
					
					if(count >1000 || url ==null){
						break;
					}
				}
			}
			
			conn.close();
			conn = null;
			System.out.println("Done.");
			System.out.println(count);
		}
	}
	
	
}
