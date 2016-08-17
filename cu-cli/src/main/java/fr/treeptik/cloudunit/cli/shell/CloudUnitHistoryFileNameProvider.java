package fr.treeptik.cloudunit.cli.shell;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.plugin.HistoryFileNameProvider;
import org.springframework.stereotype.Component;

/**
 * Created by guillaume on 15/10/15.
 */
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class CloudUnitHistoryFileNameProvider implements CommandMarker, HistoryFileNameProvider {


    @Override
    public String getHistoryFileName() {
        return "logs";
    }

    @Override
    public String getProviderName() {
        return null;
    }
}
