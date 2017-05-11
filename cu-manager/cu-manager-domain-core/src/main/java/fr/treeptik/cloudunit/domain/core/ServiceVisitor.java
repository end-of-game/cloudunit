package fr.treeptik.cloudunit.domain.core;

public interface ServiceVisitor<R> {
    R visitServer(Server server);
    R visitModule(Module module);
}
