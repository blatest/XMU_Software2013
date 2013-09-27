package protein;

//�����ݿ��Ϊ���У��ֱ��ǵ�һ�������ӣ��ڶ��������ӣ�Ecoli_n_high,Llactis_n_high,Ppastoris_n_high,Scerevisiae_n_high
//8��25�ţ�������������Ҫ�õ����������ӳ��ִ����ı����Լ�һ�±���Ϊsingle_nhighdb��֮ǰ�ı�������������ӶԳ��ֵĴ���

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ��Ե��������ӳ��ִ������¶�����һЩ��������Щ�����ֱ���ڸ��书�����Ƶĺ�������
 */

public class NHighdb {

	public static void main(String[] args) {
		try {
			// ��һ����Ecoli
			BufferedReader txt = new BufferedReader(new FileReader(
					"E.coli_high.txt"));
			int[][] Ecoli_nhigh_cishu = new int[64][64]; // ���ڱ���Ecoliÿ�������ӶԵĳ��ֵĴ���
			int[] Ecoli_single_nhigh_cishu = new int[64]; // ���ڱ���Ecoliÿ�������ӳ��ִ���
			String seq = new String("");
			do {
				seq = txt.readLine();
				if (seq == null)
					break;
				for (int i = 0; i < seq.length(); i++)
					if (seq.charAt(i) == '\t') {
						seq = seq.substring(i + 1);
						break;
					}
				// System.out.println("���У�"+seq);
				// ���ȶ������ӶԼ�����������������Ҳ����������֮������ʾ
				for (int i = 0; i < seq.length() - 5; i += 3) {
					Ecoli_nhigh_cishu[parseInt(seq.substring(i, i + 3))][parseInt(seq
							.substring(i + 3, i + 6))] += 1;
				}
				// Ȼ���Ƕ������Ӽ���
				for (int i = 0; i < seq.length() - 2; i += 3) {
					Ecoli_single_nhigh_cishu[parseInt(seq.substring(i, i + 3))] += 1;
				}
			} while (true);
			txt.close();
			// ���ǵڶ�������ΪLlactis
			txt = new BufferedReader(new FileReader("L.lactis_high.txt"));
			int[][] Llactis_nhigh_cishu = new int[64][64]; // ���ڱ���Llactisÿ�������ӶԵĳ��ֵĴ���
			int[] Llactis_single_nhigh_cishu = new int[64]; // ���ڱ���Llactisÿ�������ӳ��ִ���
			do {
				seq = txt.readLine();
				if (seq == null)
					break;
				for (int i = 0; i < seq.length(); i++)
					if (seq.charAt(i) == '\t') {
						seq = seq.substring(i + 1);
						break;
					}
				// System.out.println("���У�"+seq);
				for (int i = 0; i < seq.length() - 5; i += 3) {
					Llactis_nhigh_cishu[parseInt(seq.substring(i, i + 3))][parseInt(seq
							.substring(i + 3, i + 6))] += 1;
				}
				for (int i = 0; i < seq.length() - 2; i += 3) {
					Llactis_single_nhigh_cishu[parseInt(seq.substring(i, i + 3))] += 1;
				}
			} while (true);
			txt.close();
			// �������ǵ���������ΪPpastoris
			txt = new BufferedReader(new FileReader("P.pastoris_high.txt"));
			int[][] Ppastoris_nhigh_cishu = new int[64][64]; // ���ڱ���Llactisÿ�������ӶԵĳ��ֵĴ���
			int[] Ppastoris_single_nhigh_cishu = new int[64]; // ���ڱ���Ppastorisÿ�������ӳ��ִ���
			do {
				seq = txt.readLine();
				if (seq == null)
					break;
				for (int i = 0; i < seq.length(); i++)
					if (seq.charAt(i) == '\t') {
						seq = seq.substring(i + 1);
						break;
					}
				// System.out.println("���У�"+seq);
				for (int i = 0; i < seq.length() - 5; i += 3) {
					Ppastoris_nhigh_cishu[parseInt(seq.substring(i, i + 3))][parseInt(seq
							.substring(i + 3, i + 6))] += 1;
				}
				for (int i = 0; i < seq.length() - 2; i += 3) {
					Ppastoris_single_nhigh_cishu[parseInt(seq.substring(i,
							i + 3))] += 1;
				}
			} while (true);
			txt.close();
			// �����������һ������ΪScerevisiae
			txt = new BufferedReader(new FileReader("S.cerevisiae_high.txt"));
			int[][] Scerevisiae_nhigh_cishu = new int[64][64]; // ���ڱ���Llactisÿ�������ӶԵĳ��ֵĴ���
			int[] Scerevisiae_single_nhigh_cishu = new int[64]; // ���ڱ���Scerevisiaeÿ�������ӳ��ֵĴ���
			do {
				seq = txt.readLine();
				if (seq == null)
					break;
				for (int i = 0; i < seq.length(); i++)
					if (seq.charAt(i) == '\t') {
						seq = seq.substring(i + 1);
						break;
					}
				// System.out.println("���У�"+seq);
				for (int i = 0; i < seq.length() - 5; i += 3) {
					Scerevisiae_nhigh_cishu[parseInt(seq.substring(i, i + 3))][parseInt(seq
							.substring(i + 3, i + 6))] += 1;
				}
				for (int i = 0; i < seq.length() - 2; i += 3) {
					Scerevisiae_single_nhigh_cishu[parseInt(seq.substring(i,
							i + 3))] += 1;
				}
			} while (true);
			NHighdb.dropTable();
			NHighdb.dropTableOfSingleCodon();
			NHighdb.createTable();
			NHighdb.createTableOfSingleCodon();
			for (int i = 0; i < 64; i++)
				for (int j = 0; j < 64; j++)
					NHighdb.insertTable(i, j, Ecoli_nhigh_cishu[i][j],
							Llactis_nhigh_cishu[i][j],
							Ppastoris_nhigh_cishu[i][j],
							Scerevisiae_nhigh_cishu[i][j]);
			for (int i = 0; i < 64; i++)
				NHighdb.insertTableOfSingleCodon(i,
						Ecoli_single_nhigh_cishu[i],
						Llactis_single_nhigh_cishu[i],
						Ppastoris_single_nhigh_cishu[i],
						Scerevisiae_single_nhigh_cishu[i]);
			txt.close();
			NHighdb.selectTable();
			System.out.println("������ֿ�");
			int nObsHigh[][] = NHighdb.selectTable(Cell.ECOLI);
			for (int i = 0; i < nObsHigh.length; i++) {
				for (int j = 0; j < nObsHigh[0].length; j++)
					System.out.print(nObsHigh[i][j] + "\t");
				System.out.println();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// AGCT��0-3
	public static int parse(char ch) {
		int m = 0;
		if (ch >= 97)
			ch -= 32;
		switch (ch) {
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
		}

		return m;
	}

	public static char parse(int i) {
		switch (i) {
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

	// �����ӵ�һλȨ����16���ڶ�λȨ����4������λȨ����1��AGCT��1-4��T��U�ȼۣ����ݿ�����T�洢
	public static int parseInt(String str) {
		int id = 0;
		// System.out.println("str="+str);
		if (str.length() != 3)
			System.out.println("���ִ�����nhighdb��parseint������");
		str = str.toUpperCase();
		id = 16 * parse(str.charAt(0)) + 4 * parse(str.charAt(1))
				+ parse(str.charAt(2));
		return id;
	}

	// ��id�õ�������
	public static String parseStr(int id) {
		String str = "";
		str += parse(id / 16);
		id %= 16;
		str += parse(id / 4);
		id %= 4;
		str += parse(id);
		return str;
	}

	// �������ݿ�
	public static Connection getCon() {
		Connection con;
		String url = "jdbc:derby:codondb;create=true";

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
			String sql = "create table nhighdb(firstCodon int, secondCodon int, Ecoli_n_high int, Llactis_n_high int, Ppastoris_n_high int, Scerevisiae_n_high int)";
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

	// ����һ�����������ӳ��ִ����ı�
	public static boolean createTableOfSingleCodon() {
		Connection con = getCon();
		boolean result = false;
		try {
			Statement stm = con.createStatement();
			String sql = "create table single_nhighdb(codon int, Ecoli_n_high int, Llactis_n_high int, Ppastoris_n_high int, Scerevisiae_n_high int)";
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
			String sql = "drop table NHighdb";
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

	// ɾ�����浥�������ӵı�
	public static boolean dropTableOfSingleCodon() {
		Connection con = getCon();
		boolean result = false;
		try {
			Statement stm = con.createStatement();
			String sql = "drop table single_nhighdb";
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
	public static boolean insertTable(String firstCodon, String secondCodon,
			int Ecoli_n_high, int Llactis_n_high, int Ppastoris_n_high,
			int Scerevisiae_n_high) {
		Connection con = getCon();
		boolean result = false;
		String sql = "insert into nhighdb values(?,?,?)";
		PreparedStatement pstm = null;
		try {
			pstm = con.prepareStatement(sql);
			pstm.setInt(1, parseInt(firstCodon));
			pstm.setInt(2, parseInt(secondCodon));
			pstm.setInt(3, Ecoli_n_high);
			pstm.setInt(4, Llactis_n_high);
			pstm.setInt(5, Ppastoris_n_high);
			pstm.setInt(6, Scerevisiae_n_high);
			pstm.executeUpdate();
			result = true;
			pstm.close();
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

	// ����һ����¼�����ӵ����Ѿ���ת����int����
	public static boolean insertTable(int firstCodon, int secondCodon,
			int Ecoli_n_high, int Llactis_n_high, int Ppastoris_n_high,
			int Scerevisiae_n_high) {
		Connection con = getCon();
		boolean result = false;
		String sql = "insert into nhighdb values(?,?,?,?,?,?)";
		PreparedStatement pstm = null;
		try {
			pstm = con.prepareStatement(sql);
			pstm.setInt(1, firstCodon);
			pstm.setInt(2, secondCodon);
			pstm.setInt(3, Ecoli_n_high);
			pstm.setInt(4, Llactis_n_high);
			pstm.setInt(5, Ppastoris_n_high);
			pstm.setInt(6, Scerevisiae_n_high);
			pstm.executeUpdate();
			result = true;
			pstm.close();
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

	// single_nhighdb������һ����¼�����ӵ����Ѿ���ת����int����
	public static boolean insertTableOfSingleCodon(int codon, int Ecoli_n_high,
			int Llactis_n_high, int Ppastoris_n_high, int Scerevisiae_n_high) {
		Connection con = getCon();
		boolean result = false;
		String sql = "insert into single_nhighdb values(?,?,?,?,?)";
		PreparedStatement pstm = null;
		try {
			pstm = con.prepareStatement(sql);
			pstm.setInt(1, codon);
			pstm.setInt(2, Ecoli_n_high);
			pstm.setInt(3, Llactis_n_high);
			pstm.setInt(4, Ppastoris_n_high);
			pstm.setInt(5, Scerevisiae_n_high);
			pstm.executeUpdate();
			result = true;
			pstm.close();
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

	// ��ѯ��¼,��firstCodon��secondCodon�õ�Ecoli_n_high
	public static int selectEcoliFromTable(String firstCodon, String secondCodon) {
		Connection con = getCon();
		int n_high = 0;
		String sql = "select * from nhighdb where firstCodon=? and secondCodon=?";
		try {
			PreparedStatement pstm = con.prepareStatement(sql);
			pstm.setInt(1, parseInt(firstCodon));
			pstm.setInt(2, parseInt(secondCodon));
			ResultSet rs = pstm.executeQuery();
			rs.next();
			n_high = rs.getInt(3);
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
		return n_high;
	}

	// ��ѯ��¼,��firstCodon��secondCodon�õ�Llactis_n_high
	public static int selectLlactisFromTable(String firstCodon,
			String secondCodon) {
		Connection con = getCon();
		int n_high = 0;
		String sql = "select * from nhighdb where firstCodon=? and secondCodon=?";
		try {
			PreparedStatement pstm = con.prepareStatement(sql);
			pstm.setInt(1, parseInt(firstCodon));
			pstm.setInt(2, parseInt(secondCodon));
			ResultSet rs = pstm.executeQuery();
			rs.next();
			n_high = rs.getInt(4);
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
		return n_high;
	}

	// ��ѯ��¼,��firstCodon��secondCodon�õ�Ppastoris_n_high
	public static int selectPpastorisFromTable(String firstCodon,
			String secondCodon) {
		Connection con = getCon();
		int n_high = 0;
		String sql = "select * from nhighdb where firstCodon=? and secondCodon=?";
		try {
			PreparedStatement pstm = con.prepareStatement(sql);
			pstm.setInt(1, parseInt(firstCodon));
			pstm.setInt(2, parseInt(secondCodon));
			ResultSet rs = pstm.executeQuery();
			rs.next();
			n_high = rs.getInt(5);
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
		return n_high;
	}

	// ��ѯ��¼,��firstCodon��secondCodon�õ�Scerevisiae_n_high
	public static int selectScerevisiaeFromTable(String firstCodon,
			String secondCodon) {
		Connection con = getCon();
		int n_high = 0;
		String sql = "select * from nhighdb where firstCodon=? and secondCodon=?";
		try {
			PreparedStatement pstm = con.prepareStatement(sql);
			pstm.setInt(1, parseInt(firstCodon));
			pstm.setInt(2, parseInt(secondCodon));
			ResultSet rs = pstm.executeQuery();
			rs.next();
			n_high = rs.getInt(6);
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
		return n_high;
	}

	// ���ȫ��
	public static boolean selectTable() {
		Connection con = getCon();
		boolean result = false;
		String sql = "select * from nhighdb";
		try {
			PreparedStatement pstm = con.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
			int i = 0;
			while (rs.next()) {
				i += 1;
				System.out.println("firstcodon:" + rs.getInt(1)
						+ "\t,secondcodon:" + rs.getInt(2) + "\t,nhigh:"
						+ rs.getInt(3) + "\t" + i);
			}
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

	// ��ѯ���õ�ĳ��ϸ�������н��
	public static int[][] selectTable(Cell aCell) {
		Connection con = getCon();
		String cell = aCell.toString() + "_n_high";
		int[][] nObsHigh = new int[64][64];
		String sql = "select " + cell
				+ " from nhighdb order by firstCodon,secondCodon";
		try {
			PreparedStatement pstm = con.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
			for (int i = 0; i < nObsHigh.length; i++)
				for (int j = 0; j < nObsHigh[0].length; j++) {
					if (!rs.next())
						break;
					nObsHigh[i][j] = rs.getInt(1);
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
		return nObsHigh;
	}

	// ��single_nhighdb��ѯ���õ�ĳ��ϸ�������н��
	public static int[] selectTableOfSingleCodon(Cell aCell) {
		Connection con = getCon();
		String cell = aCell.toString() + "_n_high";
		int[] nObsHigh = new int[64];
		String sql = "select " + cell
				+ " from single_nhighdb order by codon";
		try {
			PreparedStatement pstm = con.prepareStatement(sql);
			ResultSet rs = pstm.executeQuery();
			for (int i = 0; i < nObsHigh.length; i++) {
				if (!rs.next())
					break;
				nObsHigh[i] = rs.getInt(1);
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
		return nObsHigh;
	}
}
