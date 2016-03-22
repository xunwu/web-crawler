
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
  
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
  
import java.net.URLDecoder;
public class parsePage {
	
	public static void parseFromString(String content, Connection conn)throws Exception{
		Parser parser = new Parser(content);
		HasAttributeFilter filter = new HasAttributeFilter("href");
		try{
			NodeList list = parser.parse(filter);
			int count = list.size();
			for(int i=0; i<count; i++){
				Node node = list.elementAt(i);
				if(node instanceof LinkTag){
					LinkTag link = (LinkTag) node;
					String nextlink = link.extractLink();
					String mainurl = "http://www.sina.com.cn/";
					String wpurl = mainurl + "wp-content/";
					
					if(nextlink.startsWith(mainurl)){
						String sql = null;
						ResultSet rs = null;
						PreparedStatement pstmt = null;
						Statement stmt = null;
						String tag = null;
						
						if(nextlink.startsWith(wpurl)){
							continue;
						}
						
						try{
							sql = "SELECT * FROM record WHERE URL = '"+nextlink+"'";
							stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
							rs = stmt.executeQuery(sql);
							if(rs.next()){
								
							}else {
								sql = "INSERT INTO RECORD (URL, crawled) VALUES('"+nextlink+"',0)";
								pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
								pstmt.execute();
								System.out.println(nextlink);
								nextlink = nextlink.substring(mainurl.length());
								if(nextlink.startsWith("tag/")){
									tag = nextlink.substring(4, nextlink.length()-1);
									tag = URLDecoder.decode(tag, "UTF-8");
									sql = "INSERT INTO tags (tagname) VALUES ('"+tag+"')";
									pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
									pstmt.execute();
								}
							}
						}catch (SQLException e){
							System.out.println("SQLException: "+e.getMessage());
							System.out.println("SQLState: " + e.getSQLState());
							System.out.println("VndorError: "+e.getErrorCode());
						}finally{
							if(pstmt != null){
								try{
									pstmt.close();
								}catch(SQLException e2){
									e2.printStackTrace();
								}
							}
							pstmt = null;
							
							if(rs != null){
								try{
									rs.close();
								}catch(SQLException e1){
									e1.printStackTrace();
								}
							}
							rs = null;
							
							if(stmt != null){
								try{
									stmt.close();
								}catch (SQLException e3){
									
								}
							}
							stmt = null;			
						}
					}
				}
			}
		}catch(ParserException e){
			e.printStackTrace();
		}
	}
}
