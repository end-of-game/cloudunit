package fr.treeptik.cloudunit.cli.tables;

import fr.treeptik.cloudunit.dto.Command;

public enum CommandTableColumn implements TableColumn<Command> {
    NAME(20) {
        @Override
        public String getValue(Command item) {
            return item.getName();
        }
    },
    ARGUMENTS(50) {
        @Override
        public String getValue(Command item) {
            return String.join(",", item.getArguments());
        }
    };
    
    private int width;
    
    private CommandTableColumn(int width) {
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
