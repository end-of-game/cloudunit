package fr.treeptik.cloudunit.cli.tables;

import java.text.MessageFormat;

import fr.treeptik.cloudunit.cli.utils.DateUtils;
import fr.treeptik.cloudunit.model.Application;

public enum ApplicationTableColumn implements TableColumn<Application> {
    NAME(30) {
        @Override
        public String getValue(Application item) {
            return item.getName();
        }
    },
    OWNER(20) {
        @Override
        public String getValue(Application item) {
            return MessageFormat.format("{0} {1}",
                    item.getUser().getFirstName(),
                    item.getUser().getLastName());
        }
    },
    CREATED(20) {
        @Override
        public String getValue(Application item) {
            return DateUtils.formatDate(item.getDate());
        }
    },
    SERVER(10) {
        @Override
        public String getValue(Application item) {
            return item.getServer().getImage().getName();
        }
    },
    STATUS(10) {
        @Override
        public String getValue(Application item) {
            return item.getStatus().toString();
        }
    };
    
    private int width;
    
    private ApplicationTableColumn(int width) {
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
