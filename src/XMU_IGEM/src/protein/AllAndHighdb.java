package protein;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AllAndHighdb {

	public static void main(String[] args) throws IOException
	{
		AllAndHighdb.dropTable();
		AllAndHighdb.createTable();
		BufferedReader txt = new BufferedReader(new FileReader("allhigh.txt"));
		String str = txt.readLine();
		while(str!=null)
		{
			String strs[] = str.split("\t");
			AllAndHighdb.insertTable(parseInt(strs[0]), Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), Integer.parseInt(strs[3]));
			str = txt.readLine();
		}
		txt.close();
		AllAndHighdb.selectTable();
	}
	
	//AGCT��1-4
	public static int parse(char ch)
	{
		int m = 0;
		if(ch >= 97)
			ch -= 32;
		switch(ch)
		{
		case 'A':
			m = 0;
			break;
		case 'C':
			m = 1;
			break;
		case 'G':
			m = 2;
			break;
		case 'T':
			m = 3;
			break;
		case 'U':
			m = 3;
			break;
			default:
				m = 0;
		}
		
		return m;
	}
	
	public static char parse(int i)
	{
		switch(i)
		{
		case 0:
			return 'A';
		case 1:
			return 'C';
		case 2:
			return 'G';
		case 3:
			return 'T';
			default:
				return 'A';
		}
	}
	
	//�����ӵ�һλȨ����16���ڶ�λȨ����4������λȨ����1��AGCT��1-4��T��U�ȼۣ����ݿ�����T�洢
	public static int parseInt(String str)
	{
		int id = 0;
		if(str.length()!=3)
			System.out.println("���ִ�����allandhighdb��parseint������");
		str = str.toUpperCase();
		id = 16*parse(str.charAt(0))+4*parse(str.charAt(1))+parse(str.charAt(2));
		return id;
	}
	
	//��id�õ�������
	public static String parseStr(int id)
	{
		String str = "";
		str += parse(id/16);
		id %= 16;
		str += parse(id/4);
		id %= 4;
		str += parse(id);
		return str;
	}
	
	//�������ݿ�
	public static Connection getCon()
	{
		Connection con;
		String url = "jdbc:derby:codondb;create=true";
		
		try{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			con = DriverManager.getConnection(url);
			return con;
		}catch(ClassNotFoundException | SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	//������ 
	public static boolean createTable(){
		Connection con = getCon();
		boolean result = false;
		try{
			Statement stm = con.createStatement();
			String sql = "create table allandhighdb(id int primary key, alll int, high int, ignored int)";
			stm.execute(sql);
			result = true;
			stm.close();
			con.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			con.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return result;
	}
	
	//ɾ����
	public static boolean dropTable(){
		Connection con = getCon();
		boolean result = false;
		try{
			Statement stm = con.createStatement();
			String sql = "drop table allandhighdb";
			stm.execute(sql);
			result = true;
			stm.close();
			con.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		try{
			con.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	//����һ����¼
	public static boolean insertTable(int seq, int all, int high, int ignored){
		Connection con = getCon();
		boolean result = false;
		String sql = "insert into allandhighdb values(?,?,?,?)";
		PreparedStatement pstm = null;
		try{
			pstm = con.prepareStatement(sql);
			pstm.setInt(1, seq);
			pstm.setInt(2,all);
			pstm.setInt(3, high);
			pstm.setInt(4, ignored);
			pstm.executeUpdate();
			result = true;
			pstm.close();
			con.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
			con.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	//ɾ��һ����¼seq
	public static boolean deleteTable(int seq){
		Connection con = getCon();
		boolean result = false;
		PreparedStatement pstm = null;
		String sql = "delete from allandhighdb where id=?";
		try{
			pstm = con.prepareStatement(sql);
			pstm.setInt(1, seq);
			pstm.executeUpdate();
			result = true;
			pstm.close();
			con.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		try{
			con.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return result;
	}

	//��ѯ��¼,��seq��all
	public static int selectAllFromTable(int seq){
		Connection con = getCon();
		int all = 0;
		String sql = "select * from allandhighdb where id=?";
		try{
			PreparedStatement pstm = con.prepareStatement(sql);
			pstm.setInt(1, seq);
			ResultSet rs = pstm.executeQuery();
			rs.next();
			all = rs.getInt(2);
			pstm.close();
			con.close();
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("seq="+seq);
		}
		try{
			con.close();
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("seq="+seq);
		}
		return all;
	}
	
	//��ѯ��¼,�õ����е�all
			public static int[] selectAllFromTable(){
				Connection con = getCon();
				int all[] = new int[64];
				String sql = "select alll from allandhighdb order by id";
				try{
					PreparedStatement pstm = con.prepareStatement(sql);
					ResultSet rs = pstm.executeQuery();
					for(int i=0; i<all.length; i++)
						if(!rs.next())
								all[i] = rs.getInt(1);
					pstm.close();
					con.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
				try{
					con.close();
				}catch(SQLException e){
					e.printStackTrace();
				}
				return all;
			}
	
	//��ѯ��¼,��seq��high
	public static int selectHighFromTable(int seq){
		Connection con = getCon();
		int high = 0;
		String sql = "select * from allandhighdb where id=?";
		try{
			PreparedStatement pstm = con.prepareStatement(sql);
			pstm.setInt(1, seq);
			ResultSet rs = pstm.executeQuery();
			rs.next();
			high = rs.getInt(3);
			pstm.close();
			con.close();
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("seq="+seq);
		}
		try{
			con.close();
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("seq="+seq);
		}
		return high;
	}
	
	//��ѯ��¼,�õ����е�high
		public static int[] selectHighFromTable(){
			Connection con = getCon();
			int high[] = new int[64];
			String sql = "select high from allandhighdb order by id";
			try{
				PreparedStatement pstm = con.prepareStatement(sql);
				ResultSet rs = pstm.executeQuery();
				for(int i=0; i<high.length; i++)
					if(!rs.next())
							high[i] = rs.getInt(1);
				pstm.close();
				con.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
			try{
				con.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
			return high;
		}
	
	//��ѯ��¼,��seq��ignored
		public static int selectIgnoredFromTable(int seq){
			Connection con = getCon();
			int high = 0;
			String sql = "select * from allandhighdb where id=?";
			try{
				PreparedStatement pstm = con.prepareStatement(sql);
				pstm.setInt(1, seq);
				ResultSet rs = pstm.executeQuery();
				rs.next();
				high = rs.getInt(4);
				pstm.close();
				con.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
			try{
				con.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
			return high;
		}
		
		//��ѯ��¼���ܼ�¼
				public static boolean selectTable(){
					Connection con = getCon();
					boolean result = false;
					String sql = "select * from allandhighdb order by id";
					try{
						PreparedStatement pstm = con.prepareStatement(sql);
						ResultSet rs = pstm.executeQuery();
						while(rs.next())
							System.out.println("id="+rs.getInt(1)+"\t"+rs.getInt(2)+"\t"+rs.getInt(3)+"\t"+rs.getInt(4));
						result=true;
						pstm.close();
						con.close();
					}catch(SQLException e){
						e.printStackTrace();
					}
					try{
						con.close();
					}catch(SQLException e){
						e.printStackTrace();
					}
					return result;
				}
}
