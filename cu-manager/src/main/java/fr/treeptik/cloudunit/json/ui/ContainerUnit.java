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

package fr.treeptik.cloudunit.json.ui;

/**
 * Created by nicolas on 27/08/2014.
 */

/**
 *
 * Represente le JSON retourné aux CLI et WebUI pour l'affichage
 *
 * [
 *    {name:'Instance-1', id:"a11cea6b5a16", type:"server"},
 *    {name:'Instance-2', id:"bfrcea6c5rf8", type:"server"}
 * ];
 *
 */
public class ContainerUnit {

    private String name;
    private String id;
    private String type;

    public ContainerUnit(final String name, final String id, final String type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

}
