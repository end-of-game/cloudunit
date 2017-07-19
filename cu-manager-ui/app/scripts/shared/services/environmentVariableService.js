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
    .module ( 'webuiApp' )
    .factory ( 'EnvironmentVariableService', EnvironmentVariableService );

    EnvironmentVariableService.$inject = [
      '$resource'
    ];


  function EnvironmentVariableService ( $resource ) {

    return {
        getVariableEnvironment: getVariableEnvironment,
        getListSettingsEnvironmentVariable: getListSettingsEnvironmentVariable,
        addEnvironmentVariable: addEnvironmentVariable,
        deleteEnvironmentVariable: deleteEnvironmentVariable,
        getContainerOptions: getContainerOptions

    };

    function getListSettingsEnvironmentVariable ( applicationName, containerName ) {
        var dir = $resource ( 'application/:applicationName/container/:containerName/environmentVariables' );
        return dir.query ( {
            applicationName: applicationName,
            containerName: containerName
        } ).$promise;      
    }

    function addEnvironmentVariable ( applicationName, containerName, environmentVariableKey, environmentVariableValue ) {
        var data = {
            keyEnv: environmentVariableKey,
            valueEnv: environmentVariableValue
        };

        var dir = $resource ( 'application/:applicationName/container/:containerName/environmentVariables' );
        return dir.save ( {
            applicationName: applicationName,
            containerName: containerName
        }, data ).$promise;
    }

    function deleteEnvironmentVariable ( applicationName, containerName, environmentVariableID ) {
        var dir = $resource ( 'application/:applicationName/container/:containerName/environmentVariables/:id' );
        return dir.delete ( { 
            applicationName: applicationName,
            containerName: containerName,
            id: environmentVariableID
        }, {} ).$promise; 
    }

    function getContainerOptions(applicationName, containerName, infoName) {
        var dir = $resource ( 'application/:applicationName/container/:containerName/'+infoName );
        return dir.query ( {
            applicationName: applicationName,
            containerName: containerName
        } ).$promise;
    }

    function getVariableEnvironment ( applicationName, containerName ) {
        var dir = $resource ( 'application/:applicationName/container/:containerName/env');
        return dir.query ( {
            applicationName: applicationName,
            containerName: containerName
        } ).$promise;      
    }

  }
}) ();
