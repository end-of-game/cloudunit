package fr.treeptik.cloudunit.cli.commands;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.tables.TableColumn;

@Component
public class CliFormatter {
    @Value("${cloudunit.cli.quiet:false}")
    private boolean quiet;

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }
    
    public boolean isQuiet() {
        return quiet;
    }
    
    /**
     * Return given string unless in quiet mode.
     * 
     * @see #isQuiet()
     * 
     * @param s  any string
     * @return the given string, if not in quiet mode; an empty string otherwise.
     */
    public String unlessQuiet(String s) {
        return quiet ? "" : s;
    }
    
    public <T> String table(TableColumn<T>[] columns, Collection<T> items) {
        StringBuilder sb = new StringBuilder();
        
        for (TableColumn<T> column : columns) {
            sb.append(String.format("%"+column.getWidth()+"s", column.getHeader()));
        }
        sb.append("\n");
        
        for (T item : items) {
            for (TableColumn<T> column : columns) {
                sb.append(String.format("%"+column.getWidth()+"s", column.getValue(item)));
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
    
    public <T> String list(List<String> items) {
        return String.join("\n", items);
    }
}
