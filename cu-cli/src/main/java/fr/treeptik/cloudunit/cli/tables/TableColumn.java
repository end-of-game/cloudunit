package fr.treeptik.cloudunit.cli.tables;

public interface TableColumn<T> {
    String getValue(T item);
    String getHeader();
    int getWidth();
}
