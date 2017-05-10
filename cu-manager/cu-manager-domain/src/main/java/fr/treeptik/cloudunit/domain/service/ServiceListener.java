package fr.treeptik.cloudunit.domain.service;

import fr.treeptik.cloudunit.domain.core.Service;

public interface ServiceListener {

	void onServiceCreated(Service service);

	void onServiceDeleted(Service service);

	void onServiceStarted(Service service);

	void onServiceStopped(Service service);

}
