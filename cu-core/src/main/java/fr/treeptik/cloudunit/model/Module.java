/*
* LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
* but CloudUnit is licensed too under a standard commercial license.
* Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
* If you are not sure whether the AGPL is right for you,
* you can always test our software under the AGPL and inspect the source code before you contact us
* about purchasing a commercial license.
*
* LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
* or promote products derived from this project without prior written permission from Treeptik.
* Products or services derived from this software may not be called "CloudUnit"
* nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
* For any questions, contact us : contact@treeptik.fr
*/
package fr.treeptik.cloudunit.model;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.treeptik.cloudunit.model.action.ModuleAction;

@Entity
public class Module extends Container implements Serializable {

    private static final long serialVersionUID = 1L;


    @Transient
    protected String suffixCU;

    @Transient
    @JsonIgnore
    private ModuleAction moduleAction;

    @OneToMany(mappedBy = "module", cascade = { CascadeType.REMOVE, CascadeType.PERSIST })
    private List<Port> ports;

    private boolean initialized;

    @ManyToOne
    @JsonIgnore
    private Application application;

    public Module() {
        initialized = false;
        this.image = new Image();
    }
    
    public Module(Application application, Image image) {
        super(application, image);
        this.application = application;
        this.ports = image.getExposedPorts().entrySet().stream()
                .map(kv -> new Port(kv.getKey(), kv.getValue(), null, false, this))
                .collect(Collectors.toList());
        this.initialized = false;
    }
    
    protected Module(Builder builder) {
        super(builder);
    }
    
    public static abstract class AbstractBuilder<T extends AbstractBuilder<T>> extends Container.AbstractBuilder<T> {
        
        protected AbstractBuilder(Image image) {
            super(image);
        }
    }
    
    public static final class Builder extends AbstractBuilder<Builder> {

        protected Builder(Image image) {
            super(image);
        }

        @Override
        protected Builder self() {
            return this;
        }
        
        public Module build() {
            return new Module(this);
        }
        
    }

    public ModuleAction getModuleAction() {
        return moduleAction;
    }

    public void setModuleAction(ModuleAction moduleAction) {
        this.moduleAction = moduleAction;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    @PostLoad
    public void initModuleActionFromJPA() {
        ModuleFactory.updateModule(this);
    }

    @Override
    public String toString() {
        return "Module [id=" + id + ", startDate=" + startDate + ", name=" + name + ", cloudId=" + containerID + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Module other = (Module) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public List<Port> getPorts() {
        return ports;
    }

    public void setPorts(List<Port> ports) {
        this.ports = ports;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

}
