package protein;

public enum Cell {
	
	ECOLI("E.coli"),LLACTIS("L.lactis"),PPASTORIS("P.pastoris"),SCEREVISIAE("S.cerevisiae"),ANIGER("A.niger");
	
	private Cell(String cell)
	{
		this.cell=cell;
	}
	
	//��
	private final String cell;
}
