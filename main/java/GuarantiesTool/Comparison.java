package GuarantiesTool;

import database.Database;

import java.util.ArrayList;

public class Comparison {
    private ArrayList<ArrayList<String>> result;

    public Comparison() {
        GuarantiesList gl = GuarantiesList.getInstance();
        ArrayList<String> nameList = gl.getNameList();
        System.out.println("Comparison->nameList size = " + nameList.size());
        Database db = Database.getInstance();
        result = new ArrayList<>();
        try {
            ArrayList<String> counterpartyList = db.getCounterpartyList();
            for (String outBase : nameList) {
                boolean found = false;
                for (String inBase : counterpartyList) {
                    if (outBase.equals(inBase)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    ArrayList<String> tmp = new ArrayList<>();
                    tmp.add(outBase);
                    tmp.add("добавить");
                    tmp.add("переименовать");
                    result.add(tmp);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<String>> getResult() {
        if (result.size() == 0) {
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add("–асхождений в наименовани€х контрагентов/банков не найдено");
            result.add(tmp);
        }
        return result;
    }
}
