(function () {
  'use strict';

  angular
    .module ( 'webuiApp.editApplication' )
    .controller ( 'LogsCtrl', ApplicationLogsCtrl );

  ApplicationLogsCtrl.$inject = [
    '$scope',
    '$interval',
    'LogService',
    'ngTableParams',
    '$filter',
    '$stateParams',
    'ApplicationService',
    '$q'
  ];


  function ApplicationLogsCtrl ( $scope, $interval, LogService, NgTableParams, $filter, $stateParams, ApplicationService, $q ) {
    // ------------------------------------------------------------------------
    // GESTION DES LOGS
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


    // Pour des raisons de performance, arrête le polling
    // lorsque le scope est détruit
    $scope.$on ( '$destroy', function () {
      $interval.cancel ( timer );
    } );

    // si on change de tab on arrête le polling
    $scope.$watch ( $scope.editApp.currentTab, function () {
      if ( $scope.editApp.currentTab !== 'logs' )
        $interval.cancel ( timer );
    } );

    function init () {
      getContainers ()
        .then ( function onGetContainersComplete() {
        getSources ()
          .then ( function onGetSourcesComplete() {
          updateLogs ();
        } )
      } );
    }

    function updateLogs () {
      // on attend que myContainer soit défini
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
            if ( source.name === 'catalina.out' || source.name === 'server.log' ) {
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

