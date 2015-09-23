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
