package version;

public class VersionControl {
	
	/*
	 * new functionality:
	 * new counterparty profile - possibility to add/edit/delete
	 * donor functionality
	 * bug fixing
	 * bloomberg ratings generation and parsing of file with updates
	 * TODO insert new bloomberg ratings into DB
	 * TODO select active ratings report
	 * TODO compare new vs OLD
	 */
	
	private static final double version = 0.50;
	private static final String dateAsOf = "17.05.2018";
	
	public static double getVersion() {
		return version;
	}
	
	public static String getDateasof() {
		return dateAsOf;
	}

	public class VersionInfo {
		public String v050 = "������������ �������������� � SQL, " +
				"������������ ������ �� ������ � ����������";
		public String v040 = "������������ �������������� ���� ����� �����, ������� ����� ��� ������������";
		public String v039 = "��������� ����������� �������������� ������������ �� �������� ������ ���� � �������� ����";
		public String v038 = "������� ����������� �������������� �������� �� �������� ������ ���� � �������� ���� � � Counterparty Profile";
		public String v037 = "��������� ���� ������ ������ �������";
		public String v036 = "���������� ������ � �������� ������ ����������� ����������� � ������ Counterparty Profile";
	}
}
