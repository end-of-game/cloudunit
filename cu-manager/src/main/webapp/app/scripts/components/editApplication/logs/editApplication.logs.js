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
    .directive ( 'editAppLogs', Logs );


  function Logs () {
    return {
      restrict: 'E',
      templateUrl: 'scripts/components/editApplication/logs/editApplication.logs.html',
      scope: {
        app: '=',
        state: '='
      },
      controller: [
        '$scope',
        '$interval',
        'LogService',
        'ngTableParams',
        '$filter',
        '$stateParams',
        'ApplicationService',
        '$q',
        LogsCtrl
      ],
      controllerAs: 'logs',
      bindToController: true
    };
  }

  function LogsCtrl ( $scope, $interval, LogService, NgTableParams, $filter, $stateParams, ApplicationService, $q ) {
    // ------------------------------------------------------------------------
    // LOGS MANAGENENT
    // ------------------------------------------------------------------------
    var timer, vm = this;
    vm.userdata = [];
    vm.selectedSource = '';
    vm.sources = [];
    vm.containers = [];
    vm.myContainer = {};
    vm.updateLogs = updateLogs;

    vm.rows = [
      { value: 100 },
      { value: 500 },
      { value: 1000 },
      { value: 5000 }
    ];
    vm.myRows = vm.rows[0];
    vm.isLoading = true;

    init ();

    timer = $interval ( function () {
      updateLogs ();
    }, 2000 );


    $scope.$on ( '$destroy', function () {
      $interval.cancel ( timer );
    } );


    function init () {
      getContainers ()
        .then ( function onGetContainersComplete () {
          getSources ()
            .then ( function onGetSourcesComplete () {
              updateLogs ();
            } )
        } );
    }

    function updateLogs () {
      LogService.gatherNbRows ( $stateParams.name, vm.myContainer.id, vm.selectedSource, vm.myRows.value )
        .then ( function onComplete ( data ) {
            vm.userdata = data;
            vm.isLoading = false;
            $scope.tableParams.reload ();
          }
        )
        .catch ( function onError ( reason ) {
          console.log ( reason.statusText ); //debug
        } );
    }

    function getContainers () {
      var deferred = $q.defer ();
      ApplicationService.listContainers ( $stateParams.name )
        .then ( function onComplete ( containers ) {
          deferred.resolve ();
          vm.containers = containers;
          vm.myContainer = containers[0];
          return vm.containers;
        } )
        .catch ( function onError ( reason ) {
          deferred.reject ( reason.statusText );
        } );


      return deferred.promise;
    }

    function getSources () {
      var deferred = $q.defer ();
      LogService.getSources ( $stateParams.name, vm.myContainer.id )
        .then ( function onGetSourceComplete ( sources ) {
          angular.forEach ( sources, function ( source ) {
            if ( source.name === 'catalina.out' || source.name === 'server.log' || source.name === 'system.out' ) {
              vm.selectedSource = source.name;
            }
          } );

          vm.sources = sources;

          deferred.resolve ();
        } )
        .catch ( function onGetSourcesError ( reason ) {
          deferred.reject ( reason.statusText );
        } );

      return deferred.promise;
    }


    $scope.tableParams = new NgTableParams (
      {
        page: 1,            // show first page
        count: 25,          // count per page
        filter: {
          message: ''      // initial filter
        }
      },
      {
        $scope: $scope,
        total: vm.userdata.length, // length of data
        getData: function ( $defer, params ) {
          // use build-in angular filter
          var orderedData = params.filter () ? $filter ( 'filter' ) ( vm.userdata, params.filter () ) : vm.userdata;
          vm.users = orderedData.slice ( (params.page () - 1) * params.count (), params.page () * params.count () );
          params.total ( orderedData.length ); // set total for recalc pagination
          $defer.resolve ( vm.users );
        }
      } );
  }
}) ();

