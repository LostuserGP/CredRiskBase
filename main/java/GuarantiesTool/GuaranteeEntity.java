package GuarantiesTool;

import java.time.LocalDate;

public class GuaranteeEntity {
    private LocalDate reportMonth;
    private int daughterCode;
    private String id;
    private LocalDate startGuaranteeDate;
    private LocalDate finishGuaranteeDate;
    private String bank;
    private String bankINN;
    private String currency;
    private long firstSum;
    private long startSum;
    private long differencySum;
    private long finishSum;
    private long operationSum;
    private String garType;
    private String counterparty;
    private String beneficiary;
    private int beneficiaryCode;
    private String appDocType;
    private String appDocDate;
    private String appDocNumber;
    private String note;
    private String filename;

    public GuaranteeEntity(LocalDate reportMonth, int daughterCode, String id, LocalDate startGuaranteeDate,
                           LocalDate finishGuaranteeDate, String bank, String bankINN, String currency,
                           long firstSum, long startSum, long differencySum, long finishSum, long operationSum,
                           String garType, String counterparty, String beneficiary, int beneficiaryCode,
                           String appDocType, String appDocDate, String appDocNumber, String note, String filename) {
        this.reportMonth = reportMonth;
        this.daughterCode = daughterCode;
        this.id = id;
        this.startGuaranteeDate = startGuaranteeDate;
        this.finishGuaranteeDate = finishGuaranteeDate;
        this.bank = bank;
        this.bankINN = bankINN;
        this.currency = currency;
        this.firstSum = firstSum;
        this.startSum = startSum;
        this.differencySum = differencySum;
        this.finishSum = finishSum;
        this.operationSum = operationSum;
        this.garType = garType;
        this.counterparty = counterparty;
        this.beneficiary = beneficiary;
        this.beneficiaryCode = beneficiaryCode;
        this.appDocType = appDocType;
        this.appDocDate = appDocDate;
        this.appDocNumber = appDocNumber;
        this.note = note;
        this.filename = filename;
    }

    public String[] getFieldNames() {
        return null;
    }

    public LocalDate getReportMonth() {
        return reportMonth;
    }

    public int getDaughterCode() {
        return daughterCode;
    }

    public String getId() {
        return id;
    }

    public LocalDate getStartGuaranteeDate() {
        return startGuaranteeDate;
    }

    public LocalDate getFinishGuaranteeDate() {
        return finishGuaranteeDate;
    }

    public String getBank() {
        return bank;
    }

    public String getBankINN() {
        return bankINN;
    }

    public String getCurrency() {
        return currency;
    }

    public long getFirstSum() {
        return firstSum;
    }

    public long getStartSum() {
        return startSum;
    }

    public long getDifferencySum() {
        return differencySum;
    }

    public long getFinishSum() {
        return finishSum;
    }

    public long getOperationSum() {
        return operationSum;
    }

    public String getGarType() {
        return garType;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public int getBeneficiaryCode() {
        return beneficiaryCode;
    }

    public String getAppDocType() {
        return appDocType;
    }

    public String getAppDocDate() {
        return appDocDate;
    }

    public String getAppDocNumber() {
        return appDocNumber;
    }

    public String getNote() {
        return note;
    }

    public String getFilename() {
        return filename;
    }

    public void setCounterparty(String name) {
        this.counterparty = name;
    }

    public void setBank(String name) {
        this.bank = name;
    }

    public void setFirstSum(long firstSum) {
        this.firstSum = firstSum;
    }

    public void setStartSum(long startSum) {
        this.startSum = startSum;
    }

    public void setDifferencySum(long differencySum) {
        this.differencySum = differencySum;
    }

    public void setFinishSum(long finishSum) {
        this.finishSum = finishSum;
    }

    public void setOperationSum(long operationSum) {
        this.operationSum = operationSum;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
