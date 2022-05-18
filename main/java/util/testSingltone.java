package util;

public class testSingltone {
    private static testSingltone ourInstance = new testSingltone();

    public static testSingltone getInstance() {
        return ourInstance;
    }

    private testSingltone() {
    }
}
