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

/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
 *     but CloudUnit is licensed too under a standard commercial license.
 *     Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 *     If you are not sure whether the GPL is right for you,
 *     you can always test our software under the GPL and inspect the source code before you contact us
 *     about purchasing a commercial license.
 *
 *     LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 *     or promote products derived from this project without prior written permission from Treeptik.
 *     Products or services derived from this software may not be called "CloudUnit"
 *     nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 *     For any questions, contact us : contact@treeptik.fr
 */

package fr.treeptik.cloudunit.docker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DockerContainerBuilder
    extends
    DockerContainerBuilderBase<DockerContainerBuilder> {
    public DockerContainerBuilder() {
        super(new DockerContainer());
    }

    public static DockerContainerBuilder dockerContainer() {
        return new DockerContainerBuilder();
    }

    public DockerContainer build() {
        return getInstance();
    }
}

class DockerContainerBuilderBase<GeneratorT extends DockerContainerBuilderBase<GeneratorT>> {
    private DockerContainer instance;

    protected DockerContainerBuilderBase(DockerContainer aInstance) {
        instance = aInstance;
    }

    protected DockerContainer getInstance() {
        return instance;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withName(String aValue) {
        instance.setName(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withImage(String aValue) {
        instance.setImage(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withPorts(Map<String, String> aValue) {
        instance.setPorts(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withLinks(List<String> aValue) {
        instance.setLinks(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withAddedLink(String aValue) {
        if (instance.getLinks() == null) {
            instance.setLinks(new ArrayList<>());
        }

        ((ArrayList<String>) instance.getLinks()).add(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withCmd(List<String> aValue) {
        instance.setCmd(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withAddedCmdElement(String aValue) {
        if (instance.getCmd() == null) {
            instance.setCmd(new ArrayList<>());
        }

        ((ArrayList<String>) instance.getCmd()).add(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withMemory(Long aValue) {
        instance.setMemory(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withMemorySwap(Long aValue) {
        instance.setMemorySwap(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withHostName(String aValue) {
        instance.setId(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withIp(String aValue) {
        instance.setIp(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withVolumes(Map<String, String> aValue) {
        instance.setVolumes(aValue);

        return (GeneratorT) this;
    }

    @SuppressWarnings("unchecked")
    public GeneratorT withVolumesFrom(List<String> aValue) {
        instance.setVolumesFrom(aValue);

        return (GeneratorT) this;
    }
}
