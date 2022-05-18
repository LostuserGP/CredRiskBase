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
		public String v050 = "Переработано взаимодействие с SQL, " +
				"интегрирован модуль по работе с гарантиями";
		public String v040 = "Переработано взаимодействие окон между собой, улучшен шрифт для комментариев";
		public String v039 = "Добавлена возможность редактирования контрагентов по двойному щелчку мыши в основном окне";
		public String v038 = "Внесена возможность редактирования гарантий по двойному щелчку мыши в основном окне и в Counterparty Profile";
		public String v037 = "Добавлено окно вывода текста отдалки";
		public String v036 = "Исправлена ошибка с очисткой данных предыдущего контрагента в модуле Counterparty Profile";
	}
}
