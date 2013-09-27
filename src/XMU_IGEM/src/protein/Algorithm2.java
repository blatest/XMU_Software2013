package protein;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class Algorithm2 {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("�������У�");
		String seq = in.nextLine();
		System.out.println("���г��ȣ�" + seq.length());
		in.close();
		Algorithm2 al = new Algorithm2(seq, "ECOLI", 200, 200, 0.1, 0.1);
		String[] result = al.getFinalCodon();
		String codons[] = al.getSerialCodons();
		int countOfPrimaryCodons[] = al.getCountOfPrimaryCodons();
		int countOfFinalCodons[] = al.getCountOfFinalCodons();
		double proportionOfPrimaryCodons[] = al.getProportionOfPrimaryCodons();
		double proportionOfFinalCodons[] = al.getProportionOfFinalCodons();
		try {
			PrintWriter p=new PrintWriter("�����ʽ��2.txt");
			p.println("�����������£�\n"+seq);
			p.println("����������£�\n");
			for (int i = 0; i < result.length; i++)
				p.print(result[i]);
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

	public Algorithm2(String seq, String cell, int population, int daishu,
			double crossRate, double varRate) {
		Seq = new String[seq.length() / 3];
		IntOfSeq = new int[seq.length() / 3];
		for (int i = 0; i < seq.length() / 3; i++) {
			Seq[i] = seq.substring(3 * i, 3 * i + 3);
			IntOfSeq[i] = Codondb.parseInt(Seq[i]);
		} // ��Seq��IntOfSeq��ʼ����
		synCodonArray = Codondb.selectTable();
		if(!cell.equals(Cell.ANIGER))
			this.nObsHigh = NHighdb.selectTable(Cell.valueOf(cell));
		else
			this.nObsHigh = NHighdb.selectTable(Cell.ECOLI);
		this.single_nObsHigh = NHighdb.selectTableOfSingleCodon(Cell
				.valueOf(cell));
		int[] result = this
				.makeGenerations(this.getPopulation(this.IntOfSeq, population),
						daishu, crossRate, varRate);
		finalCodon = new String[result.length];
		for (int i = 0; i < finalCodon.length; i++)
			finalCodon[i] = NHighdb.parseStr(result[i]);
		
		//�������ڷ���������
		this.countOfPrimaryCodons = new int[64];
		this.proportionOfPrimaryCodons = new double[64];
		this.countOfFinalCodons = new int[64];
		this.proportionOfFinalCodons = new double[64];
		

			for(int j=0; j<this.IntOfSeq.length; j++)
					this.countOfPrimaryCodons[IntOfSeq[j]]++;
		
		for(int i=0; i<64; i++)
			this.proportionOfPrimaryCodons[i] = (double)this.countOfPrimaryCodons[i]/IntOfSeq.length;
		

			for(int j=0; j<result.length; j++)
					this.countOfFinalCodons[result[j]]++;
		
		for(int i=0; i<64; i++)
			this.proportionOfFinalCodons[i] = (double)this.countOfFinalCodons[i]/result.length;
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

	/* �������Ŵ��㷨ʵ�� */
	// ���ݰ��������еõ�һ����Ⱥ
	private int[][] getPopulation(String[] aordCodon, int length) {
		int[] ordCodon = new int[aordCodon.length];
		for (int i = 0; i < ordCodon.length; i++)
			ordCodon[i] = Codondb.parseInt(aordCodon[i]); // ��ԭ��������ת����int������
		int[][] pop = new int[length][ordCodon.length]; // ���ڱ������ɵ���Ⱥ������ȫ����int�ͱ�ʾ
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < ordCodon.length; j++) {
				Random r = new Random();
				int[] str = getSynCodon(ordCodon[j]); // �����ݿ��ж�ȡͬ��������
				pop[i][j] = str[r.nextInt(str.length)];
			}
		}
		return pop;
	}

	// ���������Ⱥʱֱ������int����
	private int[][] getPopulation(int[] aordCodon, int length) {
		int[] ordCodon = new int[aordCodon.length];
		for (int i = 0; i < ordCodon.length; i++)
			ordCodon[i] = aordCodon[i]; // ��ԭ��������ת����int������
		int[][] pop = new int[length][ordCodon.length]; // ���ڱ������ɵ���Ⱥ������ȫ����int�ͱ�ʾ
		for (int i = 0; i < length; i++) {
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
			newSeq_FitCombi[i] = getMOCO(newSeq[i]);
		for (int i = 0; i < ordCodon.length; i++)
			ordCodon_FitCombi[i] = getMOCO(ordCodon[i]);
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
				if (getMOCO(seq) > getMOCO(ordCodon[i]))
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
			if (max < getMOCO(ordCodon[i])) {
				max = getMOCO(ordCodon[i]);
				maxPoint = i; // �������λ��
			}
		return ordCodon[maxPoint];
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

	// �˺�������ĳ�������Ӷ�Ӧ�İ������������еĸ���
	private int getTheta_A_1_j(int[] seq, int codon) {
		int count = 0;
		int[] synCodon = getSynCodon(codon);
		for (int element : synCodon)
			// �������ѭ���ҳ��ֱ��ҳ���ͬ��ͬ���������������еĸ�����Ȼ�����
			for (int i = 0; i < seq.length; i++)
				if (element == seq[i])
					count++;
		return count;
	}

	// �˺�������ĳ���������������г��ִ���
	private int getTheta_C_1_k(int[] seq, int codon) {
		int count = 0;
		for (int i = 0; i < seq.length; i++)
			if (codon == seq[i])
				count++;
		return count;
	}

	// �˺�����������ϸ����ĳ�����Ӷ�Ӧ��������ִ���
	private int getTheta_A_0_j(int codon) {
		int count = 0;
		int[] synCodon = getSynCodon(codon);
		for (int i = 0; i < synCodon.length; i++)
			count += this.single_nObsHigh[synCodon[i]];
		return count;
	}

	// �˺�����������ϸ����ĳ�����ӳ��ִ���
	private int getTheta_C_0_k(int codon) {
		return this.single_nObsHigh[codon];
	}

	// �˺��������û�����������p_1_k
	private double getP_1_k(int[] seq, int codon) {
		int fenmu = 0; // ��ĸ
		fenmu = getTheta_A_1_j(seq, codon); // TODO�˴���ĸ�����0��
		if (fenmu == 0)
			return 0;
		return getTheta_C_1_k(seq, codon) / (double) fenmu;
	}

	// �˺�����������ϸ��p_0_k
	private double getP_0_k(int codon) {
		int fenmu = 0;
		fenmu = getTheta_A_0_j(codon);
		if (fenmu == 0)
			return 0;
		return getTheta_C_0_k(codon) / (double) fenmu;
	}

	// ���ICU
	public double getICU(int[] seq) {
		double PhiICU = 0.0;
		for (int i = 0; i < 64; i++)
			PhiICU += Math.abs(getP_0_k(i) - getP_1_k(seq, i));
		PhiICU /= 64;
		return PhiICU;
	}

	// ��������CCO
	// ���ȵõ��û�������ĳ�����Ӷ�Ӧ��������ִ���
	private int getTheta_AA_1_j(int[] seq, int codon1, int codon2) {
		int count = 0;
		int[] synCodon1 = getSynCodon(codon1);
		int[] synCodon2 = getSynCodon(codon2);
		for (int i = 0; i < synCodon1.length; i++)
			for (int j = 0; j < synCodon2.length; j++)
				for (int k = 0; k < seq.length - 1; k++)
					if (synCodon1[i] == seq[k] && synCodon2[j] == seq[k + 1])
						count++;
		return count;
	}

	// Ȼ��õ��û�������ĳ�����ӶԳ��ִ���
	private int getTheta_CC_1_k(int[] seq, int codon1, int codon2) {
		int count = 0;
		for (int i = 0; i < seq.length - 1; i++)
			if (codon1 == seq[i] && codon2 == seq[i + 1])
				count++;
		return count;
	}

	// �õ�����ϸ��ĳ�����ӶԶ�Ӧ�İ�������ֵĴ���
	private int getTheta_AA_0_j(int codon1, int codon2) {
		int count = 0;
		int[] synCodon1 = getSynCodon(codon1);
		int[] synCodon2 = getSynCodon(codon2);
		for (int i = 0; i < synCodon1.length; i++)
			for (int j = 0; j < synCodon2.length; j++)
				count += this.nObsHigh[synCodon1[i]][synCodon2[j]];
		return count;
	}

	// �õ�����ϸ����ĳ�����ӶԳ��ִ���
	private int getTheta_CC_0_k(int codon1, int codon2) {
		return this.nObsHigh[codon1][codon2];
	}

	// �õ�q_1_k���û����������
	private double getQ_1_k(int[] seq, int codon1, int codon2) {
		if (getTheta_AA_1_j(seq, codon1, codon2) == 0)
			return 0;
		return getTheta_CC_1_k(seq, codon1, codon2)
				/ (double) getTheta_AA_1_j(seq, codon1, codon2);
	}

	// �õ�q_0_k������ϸ��������
	private double getQ_0_k(int codon1, int codon2) {
		if (getTheta_AA_0_j(codon1, codon2) == 0)
			return 0;
		return getTheta_CC_0_k(codon1, codon2)
				/ (double) getTheta_AA_0_j(codon1, codon2);
	}

	// �õ�CCO
	public double getCCO(int[] seq) {
		double PhiCC = 0.0;
		for (int i = 0; i < 64; i++)
			for (int j = 0; j < 64; j++)
				PhiCC += getQ_0_k(i, j) / getQ_1_k(seq, i, j);
		PhiCC /= 3904;
		return PhiCC;
	}

	// ���MOCO��ICU��CCO��Ȩ�طֱ�ȡ0.5
	public double getMOCO(int[] seq) {
		return 0.5 * getICU(seq) + 0.5 * getCCO(seq);
	}

	// ��
	private String Seq[];
	private int IntOfSeq[]; // ���ڱ���Seqÿ��������ת��������֮���ֵ
	private int synCodonArray[];
	private int nObsHigh[][];
	private int single_nObsHigh[];
	private String finalCodon[];
	// �����ĸ�����ֱ𱣴��������и�������������������������и���������������������������
	private int countOfPrimaryCodons[];
	private double proportionOfPrimaryCodons[];
	private int countOfFinalCodons[];
	private double proportionOfFinalCodons[];
	// ����һ��Ҳ���������������ڱ�������������
	private String serialCodons[];
}
