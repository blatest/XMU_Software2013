package protein;

import java.io.*;
import java.sql.*;

public class Codondb {
	
	public static void main(String[] args) throws IOException
	{
		Codondb.dropTable();
		Codondb.createTable();
		String s = "";
		BufferedReader txt = new BufferedReader(new FileReader("������.txt"));
		String str = txt.readLine();
		while(str!=null)
		{
			String strs[] = str.split(" ");
			for(int i = 1; i< strs.length; i++)
			{
				
				Codondb.insertTable(strs[i], strs[0]);
				s = strs[i];
			}
			str = txt.readLine();
		}
		txt.close();
		int[] codon=Codondb.selectTable();
		for(int i=0; i<codon.length; i++)
		{
			if(codon[i]>=64)
			{
				System.out.println();
				continue;
			}
			System.out.print(codon[i]+"\t");
		}
	}
	
	//AGCT��0-3
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
				m = 1;
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
		//System.out.println("str="+str);
		if(str.length()!=3)
			System.out.println("���ִ�����codondb��parseint������");
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
			String sql = "create table codondb(id int primary key, codon varchar(3))";
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
			String sql = "drop table codondb";
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
	public static boolean insertTable(String seq, String codon){
		Connection con = getCon();
		boolean result = false;
		String sql = "insert into codondb values(?,?)";
		PreparedStatement pstm = null;
		try{
			pstm = con.prepareStatement(sql);
			pstm.setInt(1, parseInt(seq));
			pstm.setString(2, codon);
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
	public static boolean deleteTable(String seq){
		Connection con = getCon();
		boolean result = false;
		PreparedStatement pstm = null;
		String sql = "delete from codondb where id=?";
		try{
			pstm = con.prepareStatement(sql);
			pstm.setInt(1, parseInt(seq));
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
	
	public static boolean updateTable(String seq, String codon){
		Connection con = getCon();
		boolean result = false;
		try{
			String sql = "update codondb set name=? where id =?";
			PreparedStatement pstm = con.prepareStatement(sql);
			pstm.setString(1,codon);
			pstm.setInt(2, parseInt(seq));
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
	
	//��ѯ��¼,��seq��codon
	public static String selectTable(String seq){
		Connection con = getCon();
		String codon = "";
		String sql = "select * from codondb where id=?";
		try{
			PreparedStatement pstm = con.prepareStatement(sql);
			pstm.setInt(1, parseInt(seq));
			ResultSet rs = pstm.executeQuery();
			rs.next();
			codon = rs.getString(2);
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
		return codon;
	}
	
	//��ѯ��¼,��codon��seq
		public static String[] selectTable(String codon,int d){
			Connection con = getCon();
			int id[];
			String seq[] = null;
			String sql0 = "select count(*) from codondb where codon=?";
			String sql = "select * from codondb where codon=?";
			try{
				PreparedStatement pstm = con.prepareStatement(sql0);
				pstm.setString(1, codon);
				ResultSet rs = pstm.executeQuery();
				rs.next();
				int count = rs.getInt(1);
				pstm = con.prepareStatement(sql);
				pstm.setString(1, codon);
				rs = pstm.executeQuery();
				id = new int[count];
				seq = new String[count];
				for(int i=0; i<id.length; i++)
				{
					rs.next();
					id[i] = rs.getInt(1);
					seq[i] = parseStr(id[i]);
				}
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
			return seq;
		}
		
		//���ȫ��
		public static int[] selectTable()
		{
			Connection con = getCon();
			String sql = "select * from codondb order by codon";
			int[] codon=new int[64+21];			//�˴�����������ӵ�intֵ����parseInt��parseStr����������ͬ�������ͬ��������֮���Բ�С��64�����ָ���
			try{
				PreparedStatement pstm = con.prepareStatement(sql);
				ResultSet rs = pstm.executeQuery();
				String cur="";				//���浱ǰ������
				String pre="";				//������һ�εİ�����
				int index=0;					//����ǰһ�ε�����
				int curAnimoAcid=0;		//��ʾ��ǰ����������ڵڼ���
				rs.next();
				label:
				while(true){
					//����ν��������Ӧ�Ĵ���64���������뵽������
					{
						codon[index]=64+curAnimoAcid;
						curAnimoAcid++;
						index++;
					}
					pre=rs.getString(2);			//�������Ǹ���һ����ʼ����
					cur=pre;
					//������������ͬ��������
					while(pre.equals(cur))
					{
						codon[index]=rs.getInt(1);
						//System.out.println("id:"+rs.getInt(1)+",codon:"+rs.getString(2));
						index++;
						if(!rs.next())
							break label;							//�������ڵڶ���ѭ���ж��Ƿ����������Ҫ���ϱ�ǩ
						cur=rs.getString(2);
					}
				}
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
			return codon;
		}
}
