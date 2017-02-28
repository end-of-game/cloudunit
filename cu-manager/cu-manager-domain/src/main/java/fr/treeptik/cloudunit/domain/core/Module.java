package fr.treeptik.cloudunit.domain.core;

public class Module extends Service {
    
    public Module() {}
    
    public Module(Application application, Image image) {
        super(application, image);
    }

    @Override
    public <R> R accept(ServiceVisitor<R> visitor) {
        return visitor.visitModule(this);
    }

}
