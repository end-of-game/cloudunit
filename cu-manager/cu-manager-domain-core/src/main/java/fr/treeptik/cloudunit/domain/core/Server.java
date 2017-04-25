package fr.treeptik.cloudunit.domain.core;

public class Server extends Service {
    
    public Server() {}
    
    public Server(Application application, Image image) {
        super(application, image);
    }

    @Override
    public <R> R accept(ServiceVisitor<R> visitor) {
        return visitor.visitServer(this);
    }
}
