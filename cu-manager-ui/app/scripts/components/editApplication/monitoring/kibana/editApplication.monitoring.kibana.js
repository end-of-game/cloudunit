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
    .module ( 'webuiApp.editApplication' )
    .component ( 'editAppMonitoringKibana', Monitoring() );

  function Monitoring () {
    return {
      templateUrl: 'scripts/components/editApplication/monitoring/kibana/editApplication.monitoring.kibana.html',
      bindings: {
        app: '='
      },
      controller: [
        '$scope',
        'MonitoringService',
        '$sce',
        'ErrorService',
        MonitoringCtrl
      ],
      controllerAs: 'monitoring',
    };
  }

  function MonitoringCtrl ( $scope, MonitoringService, $sce, ErrorService ) {

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

    vm.$onInit = function() {
      getContainers ();
    }

    vm.$onInit = function() {
      setTimeout(function() {
        MonitoringService.getKibanaLocation()
          .then(function(url) {
            vm.iframeUrl = $sce.trustAsResourceUrl(url + "/app/kibana#/dashboard/Dockbeat-Per-Container-Dashboard-Graph?embed=true&_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now-1h,mode:quick,to:now))&_a=(filters:!(),options:(darkTheme:!f),panels:!((col:1,id:Dockbeat-Per-Container-CPU-Graph,panelIndex:3,row:1,size_x:6,size_y:3,type:visualization),(col:7,id:Dockbeat-Per-Container-Memory-Graph,panelIndex:4,row:1,size_x:6,size_y:3,type:visualization),(col:1,id:Dockbeat-Global-Net-Error-Graph,panelIndex:5,row:4,size_x:6,size_y:3,type:visualization),(col:7,id:Dockbeat-Global-Net-Usage-Graph,panelIndex:6,row:4,size_x:6,size_y:3,type:visualization),(col:1,id:Dockbeat-Global-IO-Usage-Graph,panelIndex:7,row:7,size_x:12,size_y:3,type:visualization)),query:(query_string:(analyze_wildcard:!t,query:'containerName:+" + vm.app.server.name + "')),title:'Dockbeat+Per+Container+Dashboard+Graph',uiState:())");
          })
          .catch(function(response) {
            ErrorService.handle(response);
          })
      }, 0);
    }

    function getContainers ( selectedContainer ) {
        vm.isLoading = true;
          return ApplicationService.listContainers ( $stateParams.name )
              .then ( function onGetContainersComplete ( containers ) {
                  vm.containers = containers;
                  vm.myContainer = selectedContainer || containers[0];

                  vm.monitoringService.initStats ( vm.myContainer.name ).then ( function () {
                      vm.stats = vm.monitoringService.stats;
                      vm.isLoading = false;
                  } )
              } )
              .catch ( function onGetContainersError ( reason ) {
                  console.error ( reason ); //debug
        } );
    }
  }

}) ();
