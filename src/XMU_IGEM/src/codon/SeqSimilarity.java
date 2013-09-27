package codon;

import java.util.Scanner;

import protein.Codondb;

public class SeqSimilarity {

	public static void main(String[] args) {
		SigmaDetailed sigma = Promoterdb.selectTable("sigma70");
		Scanner in = new Scanner(System.in);
		System.out.println("�������У�");
		String seq = in.nextLine();
		in.close();
		for (int i = 0; i < 1; i++) {
			SeqSimilarity s = new SeqSimilarity(sigma, seq);
			System.out.println(sigma.getName());
			System.out.println(s);
			System.out.println();
		}
	}

	public SeqSimilarity(String seq) {
		this.getMaxSigma(seq);
	}

	public SeqSimilarity(SigmaDetailed sigma, String seq) {
		this.matrix1 = sigma.getMatrix1();
		this.matrix2 = sigma.getMatrix2();
		this.geneSeq = seq;
		this.motifWeight = sigma.getMotifWeight();
		this.spaceWeight = sigma.getSpaceWeight();
		this.spaceMin = sigma.getSpaceMin();
		this.spaceMax = sigma.getSpaceMax();
		this.adjustE = sigma.getAdjustE();
		this.name = sigma.getName();

		// ֱ���ڹ��췽���е��õý�������巽���ں��棬���϶centerĬ��Ϊ�������Сֵ��һ��
		this.getSimilarity(this.matrix1, this.matrix2, this.geneSeq,
				this.motifWeight, this.spaceWeight, this.spaceMin,
				this.spaceMax, (this.spaceMin + this.spaceMax) / 2,
				this.adjustE);
	}

	public SeqSimilarity(GeneFreMatrix matrix1, GeneFreMatrix matrix2,
			String geneSeq, double motifWeight, double spaceWeight,
			int spaceMin, int spaceMax, double adjustE) {
		this.matrix1 = matrix1;
		this.matrix2 = matrix2;
		this.geneSeq = geneSeq;
		this.motifWeight = motifWeight;
		this.spaceWeight = spaceWeight;
		this.spaceMin = spaceMin;
		this.spaceMax = spaceMax;
		this.adjustE = adjustE;

		// ֱ���ڹ��췽���е��õý�������巽���ں��棬���϶centerĬ��Ϊ�������Сֵ��һ��
		this.getSimilarity(this.matrix1, this.matrix2, this.geneSeq,
				this.motifWeight, this.spaceWeight, this.spaceMin,
				this.spaceMax, (this.spaceMin + this.spaceMax) / 2,
				this.adjustE);
	}

	// ��������������ȡ�����ʼλ�㣬��1��ʼ����Ѽ�϶�����ƶ�
	public int getBestSpaceLength() {
		return bestSpaceLength;
	}

	public int getBestStartPoint() {
		return bestStartPoint;
	}

	public double getSimilarity() {
		return Similarity/this.getMax();
	}

	// ֮��ӵ�һ��getter��������name��
	public String getName() {
		return name;
	}

	// ����ʵ��toString����
	public String toString() {
		String str = "\n�����ʼλ�㣺 " + bestStartPoint + ";\n" + "��Ѽ�϶����: "
				+ bestSpaceLength + ";\n" + "���ƶ�: " +getSimilarity() + ";\n";
		return str;
	}

	// ���������㷨��һ������
	// �����I
	private double getI(GeneFreMatrix matrix, int i) {
		double I = 0.0;
		for (int j = 1; j <= matrix.getRows(); j++) {
			if (matrix.getElement(j, i) == 0.0)
				continue;
			I += matrix.getElement(j, i)
					* Math.log(4 * matrix.getElement(j, i));
		}
		return I;
	}

	// �����ֵ
	private double getMax(GeneFreMatrix matrix) {
		double max = 0.0;
		for (int i = 1; i <= matrix.getLength(); i++) {
			max += getI(matrix, i) * matrix.getMaxInCol(i);
		}
		return max;
	}

	// ����Сֵ
	private double getMin(GeneFreMatrix matrix) {
		double min = 0.0;
		for (int i = 1; i <= matrix.getLength(); i++) {
			min += getI(matrix, i) * matrix.getMinInCol(i);
		}
		return min;
	}

	// ��ǰֵ֮ǰ������ת�����֣�Ĭ������ΪACGT
	private int parseInt(String s) {
		int a = 1;
		switch (s.substring(0, 1)) {
		case "A":
			a = 1;
			break;
		case "a":
			a = 1;
			break;
		case "C":
			a = 2;
			break;
		case "c":
			a = 2;
			break;
		case "G":
			a = 3;
			break;
		case "g":
			a = 3;
			break;
		case "T":
			a = 4;
			break;
		case "t":
			a = 4;
			break;
		default:
			System.out.println("NO ELSE EXCEPT \"ATGC\"");
		}
		return a;
	}
	
	private String parseStr(int i)
	{
		String str = "A";
		switch(i)
		{
		case 1:
			return "A";
		case 2:
			return "C";
		case 3:
			return "G";
		case 4:
			return "T";
			default:
				return "A";
		}
	}

	// ��ǰֵ��Ĭ������ATGC
	private double getCur(GeneFreMatrix matrix, String s) {
		double cur = 0.0;
		if (s.length() != matrix.getLength()) {
			System.out
					.println("Wrong at function getCur! Has the string the same length with frequency matrix??");
			return cur;
		}
		for (int i = 1; i <= s.length(); i++) {
			cur += getI(matrix, i)
					* matrix.getElement(parseInt(s.substring(i - 1, i)), i);
		}
		return cur;
	}

	// ��mSS
	private double getmSS(GeneFreMatrix matrix, String s) {
		return (getCur(matrix, s) - getMin(matrix))
				/ (getMax(matrix) - getMin(matrix));
	}

	// ���϶�÷֣�����accessibility
	private double getAccessibility(int spacemin, int spacemax, int d,
			int center) {
		return 1.0 + Math.cos(2 * Math.PI / 10.6 * (d - center));
	}

	// ���϶�÷֣�����GS
	private double getGS(int spacemin, int spacemax, int d, int center,
			double adjuste) {
		return getAccessibility(spacemin, spacemax, d, center);
	}

	// ���������еĵ�mλ��ʼ����ģ���϶��spacemin��spacemax,����ֱ�������s���Ѿ����и��
	private double getSimilarityKnownStart(GeneFreMatrix matrix1,
			GeneFreMatrix matrix2, String s, double motifweight,
			double spaceweight, int spacemin, int spacemax, int center,
			double adjuste) {
		double max = 0.0, cur = 0.0;
		double firstPart = 0.0, secondPart = 0.0, thirdPart = 0.0;
		for (int i = spacemin; matrix1.getColumns() + i + matrix2.getColumns() < s
				.length() && i <= spacemax; i++) {
			// System.out.println("�ӵ�һ��ģ�忪ʼ�� "+s+";��϶���ȣ� "+i);
			firstPart = motifweight
					* getmSS(matrix1, s.substring(0, matrix1.getColumns()));
			secondPart = spaceweight
					* getGS(spacemin, spacemax, i, center, adjuste);
			thirdPart = motifweight
					* getmSS(
							matrix2,
							s.substring(
									0 + matrix1.getColumns() + i,
									0 + matrix1.getColumns() + i
											+ matrix2.getColumns()));
			cur = firstPart + secondPart + thirdPart;
			// System.out.println("similarity: "+cur);
			if (max < cur) {
				max = cur;
				if (max > this.Similarity)
					bestSpaceLength = i;
			}
		}
		return max;
	}

	// ����������ͷ��ʼ
	private boolean getSimilarity(GeneFreMatrix matrix1, GeneFreMatrix matrix2,
			String s, double motifweight, double spaceweight, int spacemin,
			int spacemax, int center, double adjuste) {
		double cur = 0.0;
		double max = 0.0;
		for (int i = 0; i + matrix1.getColumns() + spacemin
				+ matrix2.getColumns() < s.length(); i++) {
			// System.out.println("��ʼλ�㣺 "+(i+1));
			cur = getSimilarityKnownStart(matrix1, matrix2, s.substring(i),
					motifweight, spaceweight, spacemin, spacemax, center,
					adjuste);
			if (this.Similarity < cur) {
				this.Similarity = cur;
				bestStartPoint = i + 1;
			}
		}
		return true;
	}

	// �������sigma�����е�similarity���ֵ�����õ��������ʼλ�㣬��Ѽ�϶���ȣ�����ֵΪsigma���ӣ�����ֱֵ�Ӷ���ֵ����
	private boolean getMaxSigma(String seq) {
		SigmaDetailed[] sigma = Promoterdb.selectTable();
		boolean result = false;
		String name = null;
		double cur = 0.0;
		double max = 0.0;
		int bestStartPoint = 0;
		int bestSpaceLength = 0;
		for (int i = 0; i < sigma.length; i++) {
			this.getSimilarity(sigma[i].getMatrix1(), sigma[i].getMatrix2(),
					seq, sigma[i].getMotifWeight(), sigma[i].getSpaceWeight(),
					sigma[i].getSpaceMin(), sigma[i].getSpaceMax(),
					(sigma[i].getSpaceMin() + sigma[i].getSpaceMax()) / 2,
					sigma[i].getAdjustE());
			cur = this.Similarity;
			if (max < cur) {
				bestStartPoint = this.bestStartPoint;
				bestSpaceLength = this.bestSpaceLength;
				max = cur;
				name = sigma[i].getName();
			}
		}
		this.bestStartPoint = bestStartPoint;
		this.bestSpaceLength = bestSpaceLength;
		this.Similarity = max;
		this.name = name;
		return result;
	}

	private String getMotif(GeneFreMatrix matrix) {
		String str = "";
		for (int i = 0; i < matrix.getLength(); i++) {
			int maxPoint = 1;
			double max = 0.0;
			for (int j = 0; j < 4; j++)
				if (matrix.getElement(j + 1, i + 1) > max) {
					max = matrix.getElement(j + 1, i + 1);
					maxPoint = j + 1;
				}
			str += parseStr(maxPoint);
		}
		return str;
	}

	private double getMax() {
		SigmaDetailed sigma = Promoterdb.selectTable(this.name);
		String motif1 = "";
		String motif2 = "";
		motif1 = getMotif(sigma.getMatrix1());
		motif2 = getMotif(sigma.getMatrix2());
		GeneFreMatrix matrix1 = sigma.getMatrix1();
		GeneFreMatrix matrix2 = sigma.getMatrix2();
		double first = sigma.getMotifWeight() * this.getmSS(matrix1, motif1)
				+ sigma.getMotifWeight() * this.getmSS(matrix2, motif2);
		double second = sigma.getSpaceWeight()
				* this.getGS(sigma.getSpaceMin(), sigma.getSpaceMax(),
						(sigma.getSpaceMax() + sigma.getSpaceMin()) / 2,
						(sigma.getSpaceMax() + sigma.getSpaceMin()) / 2, 0.0);
		
		return first+second;
	}

	// ��
	private GeneFreMatrix matrix1;
	private GeneFreMatrix matrix2;
	private String geneSeq;
	private double motifWeight;
	private double spaceWeight;
	private int spaceMin;
	private int spaceMax;
	private double adjustE;
	private int bestSpaceLength;
	private int bestStartPoint;
	private double Similarity;
	// name��֮��ӵģ�ֻ����ʼ��һ�����캯���й�
	private String name;
}
