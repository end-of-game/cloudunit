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
        .component('editAppMonitoringKibana', Monitoring());

    function Monitoring() {
        return {
            templateUrl: 'scripts/components/editApplication/monitoring/kibana/editApplication.monitoring.kibana.html',
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
                MonitoringCtrl
            ],
            controllerAs: 'monitoring',
        };
    }

    function MonitoringCtrl($scope, ApplicationService, $stateParams, MonitoringService, $sce, ErrorService) {

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
            console.log(selectedContainer);
            vm.isLoading = true;
            return ApplicationService.listContainers($stateParams.name)
                .then(function onGetContainersComplete(containers) {
                    vm.containers = containers;
                    vm.myContainer = selectedContainer || containers[0];

                    setTimeout(function () {
                        MonitoringService.getKibanaLocation()
                            .then(function (url) {
                                vm.isLoading = false;
                                //console.log(vm.myContainer.name.contains("mysql"))
                                //if (vm.myContainer.name.contains("mysql")){
                                var str = vm.myContainer.name.split("-")
                                if ((str.length) != 2) {
                                    console.log(str)
                                    vm.iframeUrl = $sce.trustAsResourceUrl(url + "/app/kibana#/dashboard/"+ str[1] +"?embed=true&_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now-1h,mode:quick,to:now))&_a=(filters:!(),options:(darkTheme:!f),query:(query_string:(analyze_wildcard:!t,query:'beat.hostname: +" + vm.myContainer.name + " OR docker.container.name: +" + vm.myContainer.name + "')),title:"+ str[1] +",uiState:())");
                                    //vm.iframeUrl = $sce.trustAsResourceUrl(url + "/app/kibana#/dashboard/Per-Container-Dashboard-Graph?embed=true&_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now-1h,mode:quick,to:now))&_a=(filters:!(),options:(darkTheme:!f),query:(query_string:(analyze_wildcard:!t,query:'docker.container.name: +" + vm.myContainer.name + "')),title:'Per+Container+Dashboard+Graph',uiState:())");
                                } else {
                                    vm.iframeUrl = $sce.trustAsResourceUrl(url + "/app/kibana#/dashboard/Container-Dashboard-Graph?embed=true&_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now-1h,mode:quick,to:now))&_a=(filters:!(),options:(darkTheme:!f),query:(query_string:(analyze_wildcard:!t,query:'docker.container.name: +" + vm.myContainer.name + "  OR docker.container.name: +" + vm.myContainer.name + "')),title:'Container+Dashboard+Graph',uiState:())");
                                }
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
