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
        .component('editAppJmxMonitoringKibana', JmxMonitoring());

    function JmxMonitoring() {
        return {
            templateUrl: 'scripts/components/editApplication/monitoring/jmx/editApplication.monitoring.kibana.html',
            bindings: {
                app: '='
            },
            controller: [
                '$scope',
                'ApplicationService',
                '$stateParams',
                'MonitoringService',
                '$sce',
                'ErrorService',
                JmxMonitoringCtrl
            ],
            controllerAs: 'jmx',
        };
    }

    function JmxMonitoringCtrl($scope, ApplicationService, $stateParams, MonitoringService, $sce, ErrorService) {

        // ------------------------------------------------------------------------
        // MONITORING
        // ------------------------------------------------------------------------

        var vm = this;
        vm.containers = [];
        vm.myContainer = {};
        vm.isLoading = true;
        vm.getContainers = getContainers;
        vm.monitoringService = MonitoringService;

        vm.$onDestroy = function () {
            vm.monitoringService.stopPollStats();
        };

        vm.$onInit = function () {
            getContainers();
        }

        function getContainers(selectedContainer) {
            vm.isLoading = true;
            return ApplicationService.listContainers($stateParams.name)
                .then(function onGetContainersComplete(containers) {
                    vm.containers = containers;
                    vm.myContainer = selectedContainer || containers[0];
                    setTimeout(function () {
                        MonitoringService.getKibanaLocation()
                            .then(function (url) {
                                vm.isLoading = false;
                                var str = vm.myContainer.name.split("-")
                                  vm.iframeUrl = $sce.trustAsResourceUrl(url + "/app/kibana#/dashboard/jmx-tomcat?embed=true&_g=(refreshInterval:('$$hashKey':'object:573',display:'5+seconds',pause:!f,section:1,value:5000),time:(from:now-15m,mode:quick,to:now))&_a=(filters:!(),options:(darkTheme:!f),query:(query_string:(analyze_wildcard:!t,query:'host.keyword:+"+ vm.myContainer.name +"')),title:jmx-tomcat,uiState:())");
                            })
                            .catch(function (response) {
                                ErrorService.handle(response);
                            })
                    }, 0);
                })
                .catch(function onGetContainersError(reason) {
                    console.error(reason); //debug
                });
        }
    }
})();
