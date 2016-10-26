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
package fr.treeptik.cloudunit.cli.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import fr.treeptik.cloudunit.cli.utils.SnapshotUtils;

@Component
public class SnapshotCommands implements CommandMarker {

	@Autowired
	private SnapshotUtils snapshotUtils;

	@CliCommand(value = "create-snapshot", help = "Create a new snapshot for the current application")
	public String createSnapshot(
			@CliOption(key = { "tag" }, mandatory = true, help = "You must name your snapshot") String tag,
			@CliOption(key = {
					"applicationName" }, mandatory = false, help = "You must name your snapshot") String applicationName) {
		return snapshotUtils.createSnapshot(tag, applicationName);
	}

	@CliCommand(value = "rm-snapshot", help = "Remove a snapshot for the current application")
	public String deleteSnapshot(
			@CliOption(key = { "tag" }, mandatory = true, help = "You must name your snapshot") String tag) {
		return snapshotUtils.deleteSnapshot(tag);
	}

	@CliCommand(value = "list-snapshot", help = "Create a new snapshot for the current application")
	public String listSnapshot() {
		return snapshotUtils.listAllSnapshots();
	}

	@CliCommand(value = "clone", help = "Create a new app from a previous image")
	public String clone(
			@CliOption(key = { "tag" }, mandatory = true, help = "You must put the snapshot name") String tag,
			@CliOption(key = {
					"applicationName" }, mandatory = false, help = "You must name your new application") String applicationName) {
		return snapshotUtils.clone(applicationName, tag);

	}
}
