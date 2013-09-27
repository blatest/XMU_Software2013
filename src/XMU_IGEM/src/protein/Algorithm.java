package protein;

//Ϊ�����ӿ������ٶȣ������ݿ��е�����һ��ȡ�����������ڴ��У�����ʱ������ת���ɵ�int�ͼ���
//ע�����ܶ�ؼ��ط��õ�Codondb���ݿ��parseInt������parseStr������ע����ʱ����һ��

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Algorithm {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("�����ַ���");
		String str = in.nextLine();
		in.close();
		System.out.println("���г��ȣ�" + str.length());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ
		System.out.println("��ʼʱ�䣺" + df.format(new Date()));
		Algorithm al = new Algorithm(str, Cell.ANIGER,200,200,0.1,0.1);
		String[] codon = al.getFinalCodon();
		String codons[] = al.getSerialCodons();
		int countOfPrimaryCodons[] = al.getCountOfPrimaryCodons();
		int countOfFinalCodons[] = al.getCountOfFinalCodons();
		double proportionOfPrimaryCodons[] = al.getProportionOfPrimaryCodons();
		double proportionOfFinalCodons[] = al.getProportionOfFinalCodons();
		System.out.println("\n����ʱ�䣺" + df.format(new Date()));
		try {
			PrintWriter p=new PrintWriter("�����ʽ��.txt");
			p.println("�����������£�\n"+str);
			p.println("����������£�\n");
			for (int i = 0; i < codon.length; i++)
				p.print(codon[i]);
			p.println();
			p.println("������\t��ʼ����\t��ʼ����\t��������\t���ձ���");
			for(int i=0; i<codons.length; i++)
				p.println(codons[i]+"\t"+countOfPrimaryCodons[i]+"\t"+proportionOfPrimaryCodons[i]+"\t"+countOfFinalCodons[i]+"\t"+proportionOfFinalCodons[i]);
			p.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// �ڶ������캯���������зֿ�
		public Algorithm(String seq, String cell,int population, int daishu, double crossRate, double varRate) {
			/*
			 * if(seq.length()%3!=0) { System.out.println("�������г��Ȳ���3�ı�����"); return;
			 * }
			 */
			Seq = new String[seq.length() / 3];
			IntOfSeq = new int[seq.length() / 3];
			for (int i = 0; i < seq.length() / 3; i++) {
				Seq[i] = seq.substring(3 * i, 3 * i + 3);
				IntOfSeq[i] = Codondb.parseInt(Seq[i]);
			} // ��Seq��IntOfSeq��ʼ����
			synCodonArray = Codondb.selectTable();
			this.cell = Cell.valueOf(cell);
			if(!this.cell.equals(Cell.ANIGER))
				this.nObsHigh = NHighdb.selectTable(Cell.valueOf(cell));
			else
				this.w = Wdb.selectTable();
			this.rScAll = AllAndHighdb.selectAllFromTable();
			this.rScHigh = AllAndHighdb.selectHighFromTable();
			int codon[] = this.makeGenerations(this.getPopulation(this.Seq, population), daishu, crossRate, varRate);			//�Ŵ�
			finalCodon = new String[codon.length];
			for(int i=0; i<finalCodon.length; i++)
				finalCodon[i] = Codondb.parseStr(codon[i]);		//ת��
			
			//�������ڷ���������
			this.countOfPrimaryCodons = new int[64];
			this.proportionOfPrimaryCodons = new double[64];
			this.countOfFinalCodons = new int[64];
			this.proportionOfFinalCodons = new double[64];
			

				for(int j=0; j<this.IntOfSeq.length; j++)
						this.countOfPrimaryCodons[IntOfSeq[j]]++;
			
			for(int i=0; i<64; i++)
				this.proportionOfPrimaryCodons[i] = (double)this.countOfPrimaryCodons[i]/IntOfSeq.length;
			

				for(int j=0; j<codon.length; j++)
						this.countOfFinalCodons[codon[j]]++;
			
			for(int i=0; i<64; i++)
				this.proportionOfFinalCodons[i] = (double)this.countOfFinalCodons[i]/codon.length;
			//��������������˳������ĳ�ʼ��
			this.serialCodons = new String[64];
			for(int i=0; i<64; i++)
				this.serialCodons[i] = Codondb.parseStr(i);
		}
	
	// ���캯���������зֿ�
	public Algorithm(String seq, Cell cell,int population, int daishu, double crossRate, double varRate) {
		/*
		 * if(seq.length()%3!=0) { System.out.println("�������г��Ȳ���3�ı�����"); return;
		 * }
		 */
		Seq = new String[seq.length() / 3];
		IntOfSeq = new int[seq.length() / 3];
		for (int i = 0; i < seq.length() / 3; i++) {
			Seq[i] = seq.substring(3 * i, 3 * i + 3);
			IntOfSeq[i] = Codondb.parseInt(Seq[i]);
		} // ��Seq��IntOfSeq��ʼ����
		this.cell = cell;
		synCodonArray = Codondb.selectTable();
		if(!this.cell.equals(Cell.ANIGER))
			this.nObsHigh = NHighdb.selectTable(cell);
		else
			this.w = Wdb.selectTable();
		this.rScAll = AllAndHighdb.selectAllFromTable();
		this.rScHigh = AllAndHighdb.selectHighFromTable();
		int codon[] = this.makeGenerations(this.getPopulation(this.Seq, population), daishu, crossRate, varRate);			//�Ŵ�
		finalCodon = new String[codon.length];
		for(int i=0; i<finalCodon.length; i++)
			finalCodon[i] = Codondb.parseStr(codon[i]);		//ת��
		// ������Щ������˿���ɾ�������ǵ�һ�ַ�����û���õ��Ŵ��㷨
		/*
		 * int c[] = new int[Seq.length]; c=this.getFit(IntOfSeq); for(int i=0;
		 * i<c.length; i++) System.out.print(Codondb.parseStr(c[i]));
		 */

		//�������ڷ���������
		this.countOfPrimaryCodons = new int[64];
		this.proportionOfPrimaryCodons = new double[64];
		this.countOfFinalCodons = new int[64];
		this.proportionOfFinalCodons = new double[64];
		

			for(int j=0; j<this.IntOfSeq.length; j++)
					this.countOfPrimaryCodons[IntOfSeq[j]]++;
		
		for(int i=0; i<64; i++)
			this.proportionOfPrimaryCodons[i] = (double)this.countOfPrimaryCodons[i]/IntOfSeq.length;
		

			for(int j=0; j<codon.length; j++)
					this.countOfFinalCodons[codon[j]]++;
		
		for(int i=0; i<64; i++)
			this.proportionOfFinalCodons[i] = (double)this.countOfFinalCodons[i]/codon.length;
		//��������������˳������ĳ�ʼ��
		this.serialCodons = new String[64];
		for(int i=0; i<64; i++)
			this.serialCodons[i] = Codondb.parseStr(i);
	}

	public String[] getFinalCodon() {
		return finalCodon;
	}

	public int[] getCountOfPrimaryCodons() {
		return countOfPrimaryCodons;
	}

	public double[] getProportionOfPrimaryCodons() {
		return proportionOfPrimaryCodons;
	}

	public int[] getCountOfFinalCodons() {
		return countOfFinalCodons;
	}

	public double[] getProportionOfFinalCodons() {
		return proportionOfFinalCodons;
	}

	public String[] getSerialCodons() {
		return serialCodons;
	}

	/* ���¾����ĳ�����int�м��� */
	// ��ȡn_obs_high
	private int getNObsHigh(int ci, int cj) {
		if (ci > 63 || cj > 63) {
			System.out.println("wrong at Algorithm.getNObsHigh about length!!");
			return 0;
		}
		return this.nObsHigh[ci][cj];
	}

	// ��ȡĳ���������������б���,��r_sc_all
	private double getRScAll(int str) // ��������Ƕ�AllAndHighdb���ݿ���в������Ѿ������õ������ݿ�ĺ�������
	{
		return this.rScAll[str] / 100.0;
	}

	// ��ȡĳ���������������еı�������r_sc_high
	private double getRScHigh(int str) // �������Ҫ�ģ���NHighdb������ݣ������õ��ĺ���ֱ�Ӹ�Ϊint���Ѹ�
	{
		return this.rScHigh[str] / 100.0;
	}

	// ��ȡͬ�������ӣ��ڹ��캯���м�ȡ�����ݿ��е��������ݣ�������synCodonArray�����У��Բ�С��64������Ϊ�ָ���
	private int[] getSynCodon(int seq) {
		int point = 0; // point���ڱ������������ͬ������������synCodonArray�е�λ��
		for (int i = 0; i < this.synCodonArray.length; i++)
			if (seq == synCodonArray[i]) {
				point = i;
				break;
			}
		int startPoint = point; // startPoint���ڱ���������ӵ�ͬ�������ӵ���ʾλ��
		while (this.synCodonArray[startPoint - 1] < 64)
			startPoint--;
		int endPoint = point; // endPoint���ڱ���������ӵ�ͬ�������ӵĽ���λ��
		while (endPoint < synCodonArray.length
				&& this.synCodonArray[endPoint] < 64)
			endPoint++;
		int[] synCodon = new int[endPoint - startPoint]; // ������鼴ΪҪ���ص�ͬ������������
		for (int i = startPoint; i < endPoint; i++)
			synCodon[i - startPoint] = this.synCodonArray[i];
		return synCodon;
	}

	// ��ȡn_exp_combi
	private double getNExpCombi(int ci, int cj) {
		int synCiCodon[];
		synCiCodon = getSynCodon(ci);
		int synCjCodon[];
		synCjCodon = getSynCodon(cj);
		int nObsHighSum = 0;
		for (int i = 0; i < synCiCodon.length; i++)
			for (int j = 0; j < synCjCodon.length; j++)
				nObsHighSum += getNObsHigh(synCiCodon[i], synCjCodon[j]);
		return getRScAll(ci) * getRScAll(cj) * nObsHighSum;
	}

	// ��ȡw
	private double getW(int ci, int cj) {
		if(this.cell == Cell.ANIGER)
			return this.w[ci][cj];
		double m = getNExpCombi(ci, cj);
		double n = getNObsHigh(ci, cj);
		if (m > n)
			return (m - n) / m;
		else
			return (m - n) / n;
	}

	// ��ȡr_sc_target
	private double getRScTarget(int ck) {
		return 2 * getRScHigh(ck) - getRScAll(ck);
	}

	// ��ȡr_sc_g���˴��õ��Ĳ������������У��������зֳɵ�ÿ����һ���string����
	private double getRScG(int ck, int g[]) {
		int count = 0;
		for (int i = 0; i < g.length; i++)
			if (g[i] == ck)
				count += 1;
		return count / 100.0;
	}

	// ��ȡfit_sc
	private double getFitSc(int c[]) {
		if (c.length != Seq.length) {
			System.out.println("wrong at Algorithm.getFitSc. c.length="
					+ c.length);
			return 0.0;
		}
		double halfResult = 0.0;
		for (int k = 0; k < c.length; k++)
			halfResult += getRScTarget(c[k]) - getRScG(c[k], c);
		return halfResult / c.length;
	}

	// ��ȡfit_cp
	private double getFitCp(int c[]) {
		double halfResult = 0.0;
		for (int k = 0; k < c.length - 1; k++) {
			// System.out.println("c[k]="+c[k]+"c[k+1]"+c[k+1]+"clength="+c.length+"k="+k);
			halfResult += getW(c[k], c[k + 1]);
		}
		return halfResult / (c.length - 1);
	}

	// ��ȡfit_combi
	private double getFitCombi(int c[]) {
		return getFitCp(c) / (this.Cpi + getFitSc(c));
	}

	/* �������Ŵ��㷨ʵ�� */
	// ���ݰ��������еõ�һ����Ⱥ���ݶ�����200
	private int[][] getPopulation(String[] aordCodon, int population) {
		int[] ordCodon = new int[aordCodon.length];
		for (int i = 0; i < ordCodon.length; i++)
			ordCodon[i] = Codondb.parseInt(aordCodon[i]); // ��ԭ��������ת����int������
		int[][] pop = new int[population][ordCodon.length]; // ���ڱ������ɵ���Ⱥ������ȫ����int�ͱ�ʾ
		for (int i = 0; i < population; i++) {
			for (int j = 0; j < ordCodon.length; j++) {
				Random r = new Random();
				int[] str = getSynCodon(ordCodon[j]); // �����ݿ��ж�ȡͬ��������
				pop[i][j] = str[r.nextInt(str.length)];
			}
		}
		return pop;
	}

	// ����������һ�������ʽ���,������ֻ����һ��
	private int[][] makeCross(int[] pa, int[] ma) {
		if (pa.length != ma.length) {
			System.out.println("wrong at Algorithm.makeCross!");
			return null;
		}
		int[][] result = new int[2][pa.length];
		result[0] = (int[]) pa.clone();
		result[1] = (int[]) ma.clone();
		Random r = new Random();
		int point = r.nextInt(result.length);
		int tmp = 0;
		for (int i = point; i < result[0].length; i++) {
			tmp = result[0][i];
			result[0][i] = result[1][i];
			result[1][i] = tmp;
		}
		return result;
	}

	// ���죬ֻ�������е�һ�������ӣ�����Ϊͬ�������ӣ�������һ����ʱ��û�ã�����ֱ��ʵ�֣��ƺ������ˡ�����
	private int[] makeVariation(int[] ordCodon) {
		int newCodon[] = (int[]) ordCodon.clone();
		Random r = new Random();
		int point = r.nextInt(newCodon.length);
		Random r1 = new Random();
		newCodon[point] = getSynCodon(newCodon[point])[r1
				.nextInt(getSynCodon(newCodon[point]).length)]; // ����ǰ�pointλ���ͬ��������
		return newCodon;
	}

	// ����һ��
	private int[][] makeOneGeneration(int[][] ordCodon, double crossRate,
			double varRate) {
		double P_k[] = new double[ordCodon.length]; // P_k���ڱ���ÿ����������������
		for (int i = 0; i < ordCodon.length; i++) {

			Random r = new Random();
			P_k[i] = r.nextDouble();
		}

		int count = 0; // �˴�count���ڱ���С��crossRate�ĸ���
		for (int i = 0; i < ordCodon.length; i++)
			if (P_k[i] < crossRate)
				count += 1;

		int[] crossPoint = new int[count]; // corssPoint���ڱ���С��crossRate��λ��
		int k = 0; // ����ǰһ��crossPoint�Ѿ���ֵ��������
		for (int i = 0; i < P_k.length; i++) // ע��˴��������������
		{
			if (P_k[i] < crossRate) {
				crossPoint[k] = i;
				k++;
			}
		}

		// ��ӡ��������
		/*
		 * for(int i=0; i<ordCodon.length; i++) for(int j=0;
		 * j<ordCodon[0].length; j++) System.out.print(ordCodon[i][j]+"\t");
		 */

		// ���潻��֮�������ɵ����У�����������������һ�����ڶ������棬�ڶ����ٸ����������棬ֱ��������������һ��û�и���һ������
		int[][] newSeq = new int[2 * (count - 1)][ordCodon[0].length];
		for (int i = 0; i < newSeq.length / 2; i++) {
			int seq[][] = new int[2][ordCodon[0].length];
			seq = makeCross(ordCodon[crossPoint[i]],
					ordCodon[crossPoint[i + 1]]);
			newSeq[2 * i] = seq[0];
			newSeq[2 * i + 1] = seq[1];
		}

		// ��ӡ��������
//		System.out.println("newSeq���£�");
//		for (int i = 0; i < newSeq.length; i++) {
//			for (int j = 0; j < newSeq[0].length; j++)
//				System.out.print(newSeq[i][j] + "\t");
//			System.out.println();
//		}
//		System.out.println("newSeq.length=" + newSeq.length);

		// ò��

		// �����ȴ���200�������������ɵ�������ȡfit����200�������÷����ǽ�ÿ��newSeq�е����и�ÿ��ordCodon�е����бȽ�
		// �����newSeq��ordCodon��FitCombi������ֱ���һ����ΪnewSeq.length��ordCodon.length������
//		System.out.println("��ʼ����newSeq��"
//				+ new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS")
//						.format(new Date()));
		double[] newSeq_FitCombi = new double[newSeq.length];
		double[] ordCodon_FitCombi = new double[ordCodon.length];
		for (int i = 0; i < newSeq.length; i++)
			newSeq_FitCombi[i] = getFitCombi(newSeq[i]);
		for (int i = 0; i < ordCodon.length; i++)
			ordCodon_FitCombi[i] = getFitCombi(ordCodon[i]);
		// ���ϳ�ʼ�����
		// ���ȶ�newSeq���д�С���������
//		System.out.println("��ʼ����newSeq��"
//				+ new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS")
//						.format(new Date()));
		int[] tmp = new int[newSeq.length];
		double tmp_FitCombi = 0.0;
		for (int i = 0; i < newSeq.length; i++)
			for (int j = i; j < newSeq.length; j++)
				if (newSeq_FitCombi[i] > newSeq_FitCombi[j]) {
					// �ȶ�newSeq_FitCombi���н���
					tmp_FitCombi = newSeq_FitCombi[i];
					newSeq_FitCombi[i] = newSeq_FitCombi[j];
					newSeq_FitCombi[j] = tmp_FitCombi;

					// �ٶ�newSeq���н���
					tmp = (int[]) newSeq[i].clone();
					newSeq[i] = newSeq[j];
					newSeq[j] = (int[]) tmp.clone();
				}
//		System.out.println("newSeq������ϣ�"
//				+ new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS")
//						.format(new Date()));
		// ���漴��newSeq�е�ÿ��FitCombi��ordCodon�еıȽϣ��滻
		for (int i = 0; i < newSeq.length; i++) {
			for (int j = 0; j < ordCodon.length; j++) {
				/*
				 * for(int m=0; m<newSeq[i].length; m++)
				 * System.out.print(newSeq[i][m]); System.out.println(); for(int
				 * n=0; n<ordCodon[j].length; n++)
				 * System.out.print(ordCodon[j][n]); System.out.println();
				 */
				if (newSeq_FitCombi[i] > ordCodon_FitCombi[j]) {
					tmp = (int[]) newSeq[i].clone();
					newSeq[i] = ordCodon[j];
					ordCodon[j] = tmp;
				}
			}
		}

		// ��ӡ��������
//		System.out.println("����֮��");
//		for (int i = 0; i < ordCodon.length; i++)
//			for (int j = 0; j < ordCodon[0].length; j++)
//				System.out.print(ordCodon[i][j] + "\t");

		// ������б��죬���������ֱ���ҳ�����֮ǰ������֮������
		for (int i = 0; i < ordCodon.length; i++) {
			Random r = new Random();
			if (r.nextDouble() < varRate) // ����õ��������С��varRate������б���
			{
				int[] seq = (int[]) ordCodon[i].clone();
				Random s = new Random();
				int sResult = s.nextInt(seq.length); // s���ڲ�������λ���������������Ľ��������sResult
				Random t = new Random(); // ��ʾ׼�����ĸ�ͬ��������
				seq[sResult] = getSynCodon(seq[sResult])[t
						.nextInt(getSynCodon(seq[sResult]).length)]; // �˴�����˵���ǵõ�sResultλ��ͬ�������ӣ��ж��ͬ�������ӣ�ͬ��Ҳ���������һ��
				if (getFitCombi(seq) > getFitCombi(ordCodon[i]))
					ordCodon[i] = seq;

				// System.out.println("������̣�"+new
				// SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS") .format(new
				// Date() ));
			}
		}

		return ordCodon;
	}

	// ���������ж���Ŵ�
	private int[] makeGenerations(int[][] ordCodon, int daishu,
			double crossRate, double varRate) {
		for (int i = 0; i < daishu; i++)
			ordCodon = makeOneGeneration(ordCodon, crossRate, varRate);
		return getBest(ordCodon);
	}

	// ����������ҳ�ordCodon���������
	private int[] getBest(int[][] ordCodon) {
		double max = 0.0;
		int maxPoint = 0;
		for (int i = 0; i < ordCodon.length; i++)
			if (max < getFitCombi(ordCodon[i])) {
				max = getFitCombi(ordCodon[i]);
				maxPoint = i; // �������λ��
			}
		return ordCodon[maxPoint];
	}

	/**
	 * @deprecated �˺���������ʹ��
	 * @param k
	 * @param c
	 * @return
	 */
	// ����kλ�滻��ͬ���������ҳ����
	private int[] getSingleFit(int k, int c[]) {
		int cl[] = c; // �˴�cl�������ڱ�����һ�ε�cֵ
		int synCodon[] = getSynCodon(c[k - 1]);
		for (int i = 0; i < synCodon.length; i++) {
			cl[k - 1] = synCodon[i];
			if (getFitCombi(c) < getFitCombi(cl)) {
				c[k - 1] = cl[k - 1];
			}
		}
		return c;
	}

	/**
	 * @deprecated �˺����������п��������Ч�ʲ���
	 * @param c
	 * @return
	 */
	// ���д�ͷ��β�ı���
	public int[] getFit(int c[]) {
		for (int i = 1; i <= c.length; i++)
			c = getSingleFit(i, c);
		return c;
	}

	// ��
	private Cell cell;
	private String Seq[];
	private int IntOfSeq[];
	private double Cpi;
	private int synCodonArray[];
	private int nObsHigh[][];
	private int rScAll[];
	private int rScHigh[];
	private String finalCodon[];
	//�����ĸ�����ֱ𱣴��������и�������������������������и���������������������������
	private int countOfPrimaryCodons[];
	private double proportionOfPrimaryCodons[];
	private int countOfFinalCodons[];
	private double proportionOfFinalCodons[];
	//����һ��Ҳ���������������ڱ�������������
	private String serialCodons[];
	//���������Ϊw�����飬��Ϊ�˱���A.niger��w�����õģ�ʵ��Ӧ���п���Ϊnull��С�ģ�
	private double w[][];
}
