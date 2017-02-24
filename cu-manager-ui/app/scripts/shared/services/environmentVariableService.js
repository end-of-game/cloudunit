/*
 * LICENCE : CloudUnit is available under the Affero Gnu Public License GPL V3 : https://www.gnu.org/licenses/agpl-3.0.html
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

(function () {
	'use strict';

  /**
   * @ngdoc service
   * @name webuiApp.EnvironmentVariableService
   * @description
   * # EnvironmentVariableService
   * Factory in the webuiApp.
   */
	angular
		.module('webuiApp')
		.factory('EnvironmentVariableService', EnvironmentVariableService);

	EnvironmentVariableService.$inject = [
		'$resource',
		'TraversonService'
	];


	function EnvironmentVariableService($resource, TraversonService) {

		var environmentVariableTraversonService = new TraversonService.Instance('/applications');

		return {
			getVariableEnvironment: getVariableEnvironment,
			getListSettingsEnvironmentVariable: getListSettingsEnvironmentVariable,
			addEnvironmentVariable: addEnvironmentVariable,
			deleteEnvironmentVariable: deleteEnvironmentVariable
		};

		function getListSettingsEnvironmentVariable(applicationName, container) {
			return environmentVariableTraversonService
				.concatTraverson(
				['applicationResourceList[name:' + applicationName + ']', 'self', 'containers', 'self'],
				['containerResourceList[name:' + container.name + ']', 'env-vars']);
		}

		function addEnvironmentVariable(applicationName, container, environmentVariableKey, environmentVariableValue) {
			var payload = {
				key: environmentVariableKey,
				value: environmentVariableValue
			};
			console.log('addEnvironmentVariable', payload);
			return environmentVariableTraversonService
				.traversonService
				.newRequest()
				.follow('applicationResourceList[name:' + applicationName + ']', 'self',
				 'containers', 'containerResourceList[name:' + container.name + ']', 'self', 'env-vars')
				.post(payload)
				.result
				   .then ( function(response) {
        console.log(response)
        } )
        .catch (function(err){
			console.error(err);
		});
		}

		function deleteEnvironmentVariable(applicationName, container, environmentVariableKey) {
			return environmentVariableTraversonService
				.traversonService
				.newRequest()
				.follow('applicationResourceList[name:' + applicationName + ']', 'self',
				'containers','containerResourceList[name:' + container.name + ']', 'self',
				'env-vars', 'environmentVariableResourceList[key:' + environmentVariableKey + ']', 'self' )
				.delete()
				.result;
		}

		function getVariableEnvironment(applicationName, containers) {
			var data = [
				['applicationResourceList[name:' + applicationName + ']', 'self', 'containers', 'self'],
			]
			angular.forEach(containers, function (container) {
				data.push(
					['containerResourceList[name:' + container.name + ']', 'env']
				)
			});
			return environmentVariableTraversonService
				.concatTraverson.apply(this, data);
		}
	}
})();
