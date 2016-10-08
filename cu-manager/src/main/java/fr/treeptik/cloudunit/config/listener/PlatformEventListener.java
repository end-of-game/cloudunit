package fr.treeptik.cloudunit.config.listener;

import fr.treeptik.cloudunit.config.events.DatabaseConnectionFailEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created by guillaume on 08/10/16.
 */
@Component
public class PlatformEventListener {

    @EventListener
    @Async
    public void onDatasourceConnectionFail(DatabaseConnectionFailEvent databaseConnectionFailEvent){

    }

    @EventListener
    @Async
    public void onUnexpectedContainerState(DatabaseConnectionFailEvent databaseConnectionFailEvent){

    }


}
