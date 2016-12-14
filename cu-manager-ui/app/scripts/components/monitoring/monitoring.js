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
   * @ngdoc function
   * @name webuiApp.controller:MonitoringCtrl
   * @description
   * # MonitoringCtrl
   * Controller of the webuiApp
   */
  angular
    .module ( 'webuiApp.monitoring' )
    .component ( 'monitoring', Monitoring() );


  function Monitoring () {
    return {
      templateUrl: 'scripts/components/monitoring/monitoring.html',
      bindings: {
        app: '='
      },
      controller: [
        'ApplicationService',
        'MonitoringService',
        '$sce',
        'ErrorService',
         MonitoringCtrl
      ],
      controllerAs: 'monitoring',
    };
  }

  function MonitoringCtrl ( ApplicationService, MonitoringService, $sce, ErrorService  ) {
    var vm = this;

    vm.$onInit = function() {
      setTimeout(function() {
        MonitoringService.getKibanaLocation()
            .then(function(url) {
              vm.iframeUrl = $sce.trustAsResourceUrl(url + "/app/kibana#/dashboard/Dasboard-Instant?embed=true&_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now-1m,mode:quick,to:now))&_a=(filters:!(),options:(darkTheme:!f),panels:!((col:1,id:Global-Application-Instant,panelIndex:1,row:1,size_x:7,size_y:7,type:visualization),(col:8,id:Global-Container-Instant,panelIndex:2,row:3,size_x:5,size_y:5,type:visualization),(col:8,id:Global-Reccources-Instant,panelIndex:3,row:1,size_x:5,size_y:2,type:visualization)),query:(query_string:(analyze_wildcard:!t,query:'*')),title:'Dasboard+Instant',uiState:(P-2:(vis:(params:(sort:(columnIndex:!n,direction:!n))))))' style='height: 100vh; width: 100%; border: none;");
            })
            .catch(function(response) {
              ErrorService.handle(response);
            })
      }, 0);
    }

    ////////////////////////////////////////////

  }

}) ();
