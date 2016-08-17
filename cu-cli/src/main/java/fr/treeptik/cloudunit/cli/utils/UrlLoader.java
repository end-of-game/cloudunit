/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
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

package fr.treeptik.cloudunit.cli.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlLoader {

    // Application Rest Url

    @Value("${application.actions}")
    public String actionApplication;

    @Value("${application.create}")
    public String createApplication;

    @Value("${application.list}")
    public String listAllApplications;

    @Value("${application.listTags}")
    public String listTags;

    @Value("${application.deployWar}")
    public String deployWar;

    @Value("${application.deployWarFromTag}")
    public String deployWarFromTag;

    @Value("${application.messages}")
    public String getMessages;

    // Server Rest url
    @Value("${server.prefix}")
    public String serverPrefix;

    @Value("${server.name}")
    public String serverName;

    @Value("${server.logs}")
    public String getLogs;

    @Value("${suffix.start}")
    public String start;

    @Value("${suffix.stop}")
    public String stop;

    @Value("${suffix.restart}")
    public String restart;

    @Value("${server.stream}")
    public String getStream;

    @Value("${server.addserver}")
    public String addServer;

    // Module Rest url

    @Value("${module.prefix}")
    public String modulePrefix;

    @Value("${module.name}")
    public String moduleName;

    // User Rest url

    @Value("${user.connect}")
    public String connect;

    @Value("${user.getcloudunit.instance}")
    public String getCloudUnitInstance;

    // admin prefix

    @Value("${admin}")
    public String adminActions;

    // imagePrefix
    @Value("${image.prefix}")
    public String imagePrefix;

    @Value("${image.find}")
    public String imageFind;

    @Value("${image.enabled}")
    public String imageEnabled;

    @Value("${application.logs}")
    public String logs;

}
