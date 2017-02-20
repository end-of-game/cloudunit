package fr.treeptik.cloudunit.cli.tables;

import fr.treeptik.cloudunit.model.EnvironmentVariable;

public enum EnvironmentVariableTableColumn implements TableColumn<EnvironmentVariable> {
    KEY(30) {
        @Override
        public String getValue(EnvironmentVariable item) {
            return item.getKey();
        }
    },
    VALUE(50) {
        @Override
        public String getValue(EnvironmentVariable item) {
            return item.getValue();
        }
    };

    private int width;
    
    private EnvironmentVariableTableColumn(int width) {
        this.width = width;
    }
    
    @Override
    public String getHeader() {
        return name();
    }
    
    @Override
    public int getWidth() {
        return width;
    }
}
