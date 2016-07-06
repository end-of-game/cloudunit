package fr.treeptik.cloudunit.enums;

/**
 * Created by nicolas on 10/06/2016.
 */
public enum JvmMemory {

    SIZE_512("512"),
    SIZE_1024("1024"),
    SIZE_2048("2048"),
    SIZE_3072("3072"),
    SIZE_4096("4096");

    private final String size;

    JvmMemory(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }

}
