package GuarantiesTool;

import util.DateHelper;
import util.Helper;

import java.time.LocalDate;
import java.util.ArrayList;

public class GuarantiesList {
    private ArrayList<GuaranteeEntity> guaranties;
    private ArrayList<ArrayList<String>> guarantiesArray;

    private static GuarantiesList ourInstance = new GuarantiesList();

    public static GuarantiesList getInstance() {
        return ourInstance;
    }

    public ArrayList<GuaranteeEntity> getList() {
        return guaranties;
    }

    public LocalDate getReportDate() {
        GuaranteeEntity firstEntity = guaranties.get(0);
        return firstEntity.getReportMonth();
    }

    public void create(String folder) {
        GuarantiesReader gr = new GuarantiesReader(folder);
        guarantiesArray = gr.getArray();
        createFromArray(guarantiesArray);
    }

    private void createFromArray(ArrayList<ArrayList<String>> array) {
        guaranties = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
//            String reportMonth = DateHelper.createDate(array.get(i).get(0), "-", true);
            LocalDate reportMonth = LocalDate.parse(array.get(i).get(0));
            int daughterCode = Math.round(Helper.makeFloat(array.get(i).get(1)));
            String id = array.get(i).get(2);
            LocalDate startGuaranteeDate = LocalDate.parse(array.get(i).get(3));
            LocalDate finishGuaranteeDate = LocalDate.parse(array.get(i).get(4));
            String bank = array.get(i).get(5).replace("\n", " ");
            String bankINN = array.get(i).get(6);
            String currency = array.get(i).get(7);
            //long - Данные по остаткам хранятся в копейках. метод makeMoney возвращает значение ячейки умноженное на 100
            long firstSum = Helper.makeMoney(array.get(i).get(8));
            long startSum = Helper.makeMoney(array.get(i).get(9));
            long differencySum = Helper.makeMoney(array.get(i).get(10));
            long finishSum = Helper.makeMoney(array.get(i).get(11));
            long operationSum = Helper.makeMoney(array.get(i).get(12));
            String garType = array.get(i).get(13);
            String counterparty = array.get(i).get(14).replace("\n", " ");
            String beneficiary = array.get(i).get(15).replace("\n", " ");
            int beneficiaryCode = Math.round(Helper.makeFloat(array.get(i).get(16)));
            String appDocType = array.get(i).get(17);
            String appDocDate = DateHelper.createDate(array.get(i).get(18), "-", true);
            String appDocNumber = array.get(i).get(19);
            String note = array.get(i).get(20);
            String filename = "";

            GuaranteeEntity guaranteeEntity = new GuaranteeEntity(reportMonth, daughterCode, id, startGuaranteeDate,
                    finishGuaranteeDate, bank, bankINN, currency, firstSum, startSum, differencySum, finishSum,
                    operationSum, garType, counterparty, beneficiary, beneficiaryCode, appDocType, appDocDate,
                    appDocNumber, note, filename);

            guaranties.add(guaranteeEntity);
        }
    }

    public ArrayList<String> getNameList() {
        ArrayList<String> answer = new ArrayList<>();
        for (GuaranteeEntity entity : guaranties) {
            boolean foundCounterparty = false;
            boolean foundBank = false;
            for (String s : answer) {
                if (s.equals(entity.getCounterparty())) {
                    foundCounterparty = true;
                }
                if (s.equals(entity.getBank())) {
                    foundBank = true;
                }
            }
            if (!foundCounterparty) {
                answer.add(entity.getCounterparty());
            }
            if (!foundBank) {
                answer.add(entity.getBank());
            }
        }
        return answer;
    }

    public void clear() {
        guaranties = null;
    }

    public void renameCounterparty(String oldName, String newName) {
        for (GuaranteeEntity gar : guaranties) {
            if (gar.getCounterparty().equals(oldName)) {
                gar.setCounterparty(newName);
            }
            if (gar.getBank().equals(oldName)) {
                gar.setBank(newName);
            }
        }
        for (ArrayList<String> gar : guarantiesArray) {
            if (gar.get(14).equals(oldName)) {
                gar.set(14, newName);
            }
            if (gar.get(5).equals(oldName)) {
                gar.set(5, newName);
            }
        }
    }

    public ArrayList<ArrayList<String>> getArray() {
        return guarantiesArray;
    }

}
