package frame;

public class TransferExe {
	public static void main(String[] args) {
		  new TransferExe();
		  }
		  
	public TransferExe()
	{
		openExe();
	}
		  //���������Ŀ�ִ���ļ������磺�Լ�������exe������ ���� ��װ�����.
		  public static void openExe() {
		  Runtime rn = Runtime.getRuntime();
		  Process p = null;
		  try {
			  p = rn.exec("./ttec_sustc/TTEC.exe");
		  } catch (Exception e) {
//		  System.out.println("Error exec!");
		  }
		  }
}
