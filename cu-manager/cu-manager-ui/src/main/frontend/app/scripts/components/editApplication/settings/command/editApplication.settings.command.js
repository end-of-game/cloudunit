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
    angular
    .module('webuiApp.editApplication')
    .component('commandComponent', commandComponent());

    function commandComponent(){
        return {
            templateUrl: 'scripts/components/editApplication/settings/command/editApplication.settings.command.html',
            bindings: {
                application: '<app'
            },
            controller: [
            '$scope',
            '$stateParams',
            '$q',
            'ApplicationService',
            'ErrorService',
            '$resource',
            '$http',
            commandCtrl
            ],
            controllerAs: 'commandRun',
        }
    }


    function commandCtrl($scope, $stateParams, $q, ApplicationService, ErrorService, $resource, $http) {
        var commandRun = this;

        commandRun.getCommandList = getCommandList;
        commandRun.execCommand = execCommand;

        commandRun.commandList = [];
        commandRun.formsExec = [];

        commandRun.$onInit = function() {
            getContainers();
        }

        ////////////////////////////////////////////////

        function getCommandList(containerSelected) {
            // var data = {
            //     applicationName: $stateParams.name,
            //     containerName: vm.myContainer.name,
            // };
            console.log('command get');

            var urlLink = 'application/' + $stateParams.name + '/container/' + containerSelected + '/command';
            var dir = $resource(urlLink);

            var commandList = dir.query().$promise;
            commandList.then(function(response) {
                var labelform;
                var labelAction = [];
                commandRun.commandList = response;

                for(var i=0 ; i < commandRun.commandList.length ; i++) {
                    commandRun.commandList[i].label = commandRun.commandList[i].name;
                    labelform = commandRun.commandList[i].label;
                    for(var i2=0 ; i2 < labelform.length ; i2++) {
                        if(labelform[i2] === '_') {
                            labelform = labelform.substring(i2, labelform.length);
                            labelform = labelAction + labelform;
                            break;
                        }
                        labelAction.push(labelform[i2].toUpperCase());
                    }
                    labelform = labelform.substring(0, labelform.length - 3);
                    labelform = labelform.replace(/_/g, ' ');
                    labelform = labelform.replace(/,/g, '');
                    commandRun.commandList[i].label = labelform;

                    console.log(labelform);
                }
            });
        }

        function execCommand(form, namefile) {
            var urlLink = 'application' + '/' + $stateParams.name + '/container/' + commandRun.myContainer.name + '/command/' + namefile + '/exec';

            var sizeObject = Object.keys(form).length;
            var objectList = [];

            for(var i=0 ; i < sizeObject; i++) {
                objectList.push(form[i]);
            }

            $scope.actionPending = true;

            $http({
                method: 'POST',
                url: urlLink,
                data: {
                    name: namefile,
                    arguments: objectList
                }
            }).then(function successCallback(response) {
                $scope.actionPending = false;
            }, function errorCallback(response) {
                $scope.actionPending = false;
            });
        }

        function getContainers(selectedContainer) {
            var deferred = $q.defer ();
            commandRun.isLoading = true;
            ApplicationService.listContainers($stateParams.name)
            .then(function(containers) {
                commandRun.containers = containers;
                commandRun.myContainer = selectedContainer || containers[0];
                commandRun.isLoading = false;
                commandRun.getCommandList(commandRun.myContainer.name);
                deferred.resolve(containers);
            })
            .catch (function (response) {
                deferred.reject(response);
            });
            return deferred.promise;
        }
    }
})();
