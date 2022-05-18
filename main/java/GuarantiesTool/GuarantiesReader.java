package GuarantiesTool;

import util.ReadXLSXFile;

import java.io.File;
import java.util.ArrayList;

public class GuarantiesReader {
    private String sFolder;
    private ArrayList<ArrayList<String>> array = new ArrayList<>();

    public GuarantiesReader(String sFolder) {
        this.sFolder = sFolder;
        parseFolder();
        System.out.println("GuarantiesReader->array size = " + array.size());
    }

    private void parseFolder() {

        File folder = new File(sFolder);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            File file = listOfFiles[i];
            if (file.isFile() && file.getName().endsWith(".xlsx")) {
                parse(file.getPath());
            }
        }
    }

    private void getRange(ArrayList<ArrayList<String>> innerArray) {
        int startRow = 0;
        int endRow = innerArray.size() - 1;
        System.out.println("Размер массива - " + endRow);
        for (int i = 0; i < innerArray.size(); i++) {
            if (innerArray.get(i).size() != 0) {
                String value = innerArray.get(i).get(0);
                //System.out.println(value + ";");
                if (value.equals(1) || value.equals("1.0")) {
                    for (int ii = i + 1; ii < innerArray.size(); ii++) {
                        String tmp = innerArray.get(ii).get(0);
                        if (!tmp.equals("")) {
                            startRow = ii;
                            break;
                        }
                    }
                    System.out.println("We find startRow - " + startRow + " with value " + innerArray.get(startRow).get(0));
                    break;
                }
            }
        }
        for (int i = startRow; i < innerArray.size(); i++) {
            if (innerArray.get(i).get(0).equals("")) {
                endRow = i - 1;
                System.out.println("We find endRow - " + endRow + " with value " + innerArray.get(endRow).get(0));
                break;
            }
        }

        for (int i = startRow; i <= endRow; i++) {
            array.add(innerArray.get(i));
        }

    }

    private void parse(String filename) {
        ReadXLSXFile reader = new ReadXLSXFile();
        ArrayList<ArrayList<String>> tmp = reader.importExcelSheet2(filename);
        System.out.println("GuarantiesReader->tmp size = " + tmp.size());
        getRange(tmp);
    }

    public ArrayList<ArrayList<String>> getArray() {
        return array;
    }

    public void printAll() {
        for (ArrayList<String> tmp : array) {
            for (String s : tmp) {
                System.out.print(s + "; ");
            }
            System.out.println();
        }
    }

}
