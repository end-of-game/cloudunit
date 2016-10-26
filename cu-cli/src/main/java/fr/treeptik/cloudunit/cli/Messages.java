package fr.treeptik.cloudunit.cli;

import java.util.ResourceBundle;

public class Messages {
    private static final String BUNDLE_NAME = "fr.treeptik.cloudunit.cli.messages";
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
    
    private static final String DEFAULT_VALUE = "!%s!";
    
    private Messages() {}
    
    public static String getString(String key) {
        if (!BUNDLE.containsKey(key)) {
            return String.format(DEFAULT_VALUE, key);
        }
        
        return BUNDLE.getString(key);
    }
}
