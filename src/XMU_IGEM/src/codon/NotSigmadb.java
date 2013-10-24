package codon;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NotSigmadb {

	public static void main(String[] args)
	{
		try {
			BufferedReader txt = new BufferedReader(new FileReader("NotSigma_factor.txt"));
			
			NotSigmadb.dropTable();
			//�������ݿ�
			NotSigmadb.createTable();
			
			//����ÿ����Ԫ��ͷ����Ҫ���ַ����ĳ���
			int beforeId = "Transcription Factor ID: ".length();
			int beforeName = "Transcription Factor Name: ".length();
			int beforeTbs = "Total of binding sites: ".length();
			int beforePssm_size = "PSSM size: ".length();
			
			//����һ��NotSigmaDetailed
			NotSigmaDetailed notsigma = new NotSigmaDetailed();
			
			String str = txt.readLine();
			//�����ļ���ͷ��һ���ַ��ǡ�#������
			while(!str.isEmpty())
				str = txt.readLine();
			
			txt.readLine();	txt.readLine();	str=txt.readLine();		//����3��
			while(str!=null)
			{
				notsigma.setId(str.substring(beforeId));
				str = txt.readLine();		notsigma.setName(str.substring(beforeName));
				str = txt.readLine();		notsigma.setTbs(Integer.valueOf(str.substring(beforeTbs)));
				str = txt.readLine();		notsigma.setPssm_size(Integer.valueOf(str.substring(beforePssm_size)));
				
				//����������tbs+2��
				for(int i=1; i<=notsigma.getTbs()+2; i++)
					txt.readLine();
				
				//������
				str = txt.readLine();
				String strs[] = str.split("\t");
				GeneFreMatrix matrix = new GeneFreMatrix(strs.length-2);
				for(int i=1; i<=4; i++)
				{
					for(int j=2; j<strs.length; j++)
						matrix.setElement(i, j-1, Double.valueOf(strs[j]));
					str = txt.readLine();
					strs = str.split("\t");
				}
				
				notsigma.setMatrix(matrix);
				
				//���ݿ����
				NotSigmadb.insertTable(notsigma);
				
				txt.readLine();	txt.readLine();	str=txt.readLine();
			}
			txt.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	// ��genefrematrix�����ÿ��ת����һ���ַ�����ת���ɵ��ַ������Ȳ��ܳ���100���Կո�Ϊ�ָ���
	private static String[] parseStr(GeneFreMatrix geneMatrix) {
		String[] str = { "", "", "", "" };
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < geneMatrix.getColumns(); j++)
				str[i] = str[i] + " "
						+ Double.toString(geneMatrix.getElement(i + 1, j + 1));
		return str;
	}

	// �������ж�����Stringת����GeneFreMatrix
	private static GeneFreMatrix parseGeneMatrix(String[] str, int matrixColumn) {
		if (str.length != 4) {
			System.out
					.println("wrong at Promoterdb.parseGeneMatrix.see str.length="
							+ str.length);
			return null;
		}
		GeneFreMatrix geneMatrix = new GeneFreMatrix(matrixColumn);
		for (int i = 0; i < 4; i++) {
			String strs[] = str[i].trim().split(" ");
			if (geneMatrix.getColumns() != strs.length) {
				System.out.println("wrong at Promoterdb.parseGeneMatrix.");
			}
			for (int j = 0; j < strs.length; j++)
				geneMatrix
						.setElement(i + 1, j + 1, Double.parseDouble(strs[j]));
		}
		return geneMatrix;
	}

	// �������ݿ�
	public static Connection getCon() {
		Connection con;
		String url = "jdbc:derby:promoterdb;create=true";

		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			con = DriverManager.getConnection(url);
			return con;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// ������
	public static boolean createTable() {
		Connection con = getCon();
		boolean result = false;
		try {
			Statement stm = con.createStatement();
			String sql = "create table notsigma_tbl(id varchar(20), name varchar(10) primary key, tbs int, pssm_size int, motif_line1 varchar(200),motif_line2 varchar(200),"
					+ "motif_line3 varchar(200),motif_line4 varchar(200))";
			stm.execute(sql);
			result = true;
			stm.close();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	// ɾ����
	public static boolean dropTable() {
		Connection con = getCon();
		boolean result = false;
		try {
			Statement stm = con.createStatement();
			String sql = "drop table notsigma_tbl";
			stm.execute(sql);
			result = true;
			stm.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	// ����һ����¼
	public static boolean insertTable(NotSigmaDetailed notsigma) {
		Connection con = getCon();
		boolean result = false;
		String sql = "insert into notsigma_tbl values(?,?,?,?,?,?,?,?)";
		PreparedStatement pstm = null;
		try {
			pstm = con.prepareStatement(sql);
			pstm.setString(1, notsigma.getId());
			pstm.setString(2, notsigma.getName());
			pstm.setInt(3, notsigma.getTbs());
			pstm.setInt(4, notsigma.getPssm_size());
			String[] strs = parseStr(notsigma.getMatrix());
			for (int i = 1; i <= 4; i++)
				pstm.setString(i + 4, strs[i - 1]);
			pstm.executeUpdate();
			result = true;
			pstm.close();
			con.close();
		} catch (Exception e) {
			System.out.println(notsigma.getId());
			e.printStackTrace();
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(notsigma.getId());
		}

		return result;
	}

	// ɾ��һ����¼id
	public static boolean deleteTable(String id) {
		Connection con = getCon();
		boolean result = false;
		PreparedStatement pstm = null;
		String sql = "delete from notsigma_tbl where id=?";
		try {
			pstm = con.prepareStatement(sql);
			pstm.setString(1, id);
			pstm.executeUpdate();
			result = true;
			pstm.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	// ��ѯ��¼,��id��sigma
	public static NotSigmaDetailed selectTable(String id) {
		Connection con = getCon();
		NotSigmaDetailed notsigma = new NotSigmaDetailed();
		String sql = "select * from notsigma_tbl where id=?";
		try {
			PreparedStatement pstm = con.prepareStatement(sql);
			pstm.setString(1, id);
			ResultSet rs = pstm.executeQuery();
			rs.next();
			notsigma.setId(rs.getString(1));
			notsigma.setName(rs.getString(2));
			notsigma.setTbs(rs.getInt(3));
			notsigma.setPssm_size(rs.getInt(4));
			String strs[] = new String[4];
			for (int i = 0; i < 4; i++)
				strs[i] = rs.getString(i + 2);
			notsigma.setMatrix(parseGeneMatrix(strs, notsigma.getPssm_size()));
			pstm.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return notsigma;
	}

	// ��ѯ��¼,�õ����е�notsigma
	public static NotSigmaDetailed[] selectTable() {
		Connection con = getCon();
		int count = 0;
		NotSigmaDetailed[] notsigma = null;
		String sql0 = "select count(*) from notsigma_tbl";
		String sql = "select * from notsigma_tbl";
		try {
			PreparedStatement pstm = con.prepareStatement(sql0);
			ResultSet rs = pstm.executeQuery();
			// �õ����ݿ��б���ĸ���
			rs.next();
			count = rs.getInt(1);
			notsigma = new NotSigmaDetailed[count];

			// �ٽ������ݶ�ȡ
			pstm = con.prepareStatement(sql);
			rs = pstm.executeQuery();
			int k = 0; // ��k�������
			while (rs.next()) {
				notsigma[k] = new NotSigmaDetailed();
				notsigma[k].setId(rs.getString(1));
				notsigma[k].setName(rs.getString(2));
				notsigma[k].setTbs(rs.getInt(3));
				notsigma[k].setPssm_size(rs.getInt(4));
				String strs[] = new String[4];
				for (int i = 0; i < 4; i++)
					strs[i] = rs.getString(i + 5);
				notsigma[k].setMatrix(parseGeneMatrix(strs, notsigma[k].getPssm_size()));
				k++;
			}
			pstm.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return notsigma;
	}
}
