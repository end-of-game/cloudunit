(function () {
  'use strict';

  /**
   * @ngdoc service
   * @name webuiApp.ApplicationService
   * @description
   * # ApplicationService
   * Factory in the webuiApp.
   */
  angular
    .module('webuiApp')
    .factory('ApplicationService', ApplicationService);

  ApplicationService.$inject = [
    '$resource',
    '$http'
  ];


  function ApplicationService($resource, $http) {
    var Application;

    Application = $resource('application/:id', {id: '@name'});


    return {
      list: list,
      create: create,
      start: start,
      stop: stop,
      isValid: isValid,
      remove: remove,
      findByName: findByName,
      listContainers: listContainers,
      createAlias: createAlias,
      removeAlias: removeAlias
    };


    ///////////////////////////////////////////////////////


    // Liste des applications
    function list() {
      return Application.query().$promise;
    }

    // Creation d'une application
    function create(applicationName, serverName) {
      var output = {};
      output.applicationName = applicationName;
      output.serverName = serverName;

      return Application.save(JSON.stringify(output)).$promise;
    }

    // Démarrer une application
    function start(applicationName) {
      var output = {};
      output.applicationName = applicationName;
      var Application = $resource('application/start');
      return Application.save(JSON.stringify(output));
    }

    // Arrêter une application
    function stop(applicationName) {
      var output = {};
      output.applicationName = applicationName;
      var Application = $resource('application/stop');
      return Application.save(JSON.stringify(output));
    }

    // Teste la validite d'une application avant qu'on puisse la creer
    function isValid(applicationName, serverName) {
      var validity = $resource('application/verify/' + applicationName + '/' + serverName);
      return validity.get().$promise;
    }

    // Suppression d'une application
    function remove(applicationName) {
      Application.get({id: applicationName}, function (ref) {
        ref.$delete();
      });
    }

    // Récupération d'une application selon son nom
    function findByName(applicationName) {
      return Application.get({id: applicationName}).$promise;
    }

    // Liste de toutes les containers d'une application en fonction du type server/module
    function listContainers(applicationName) {
      var containers = $resource('application/:applicationName/containers');
      return containers.query({applicationName: applicationName}).$promise;
    }

    // Gestion des alias

    function createAlias(applicationName, alias) {
      var data = {
        applicationName: applicationName,
        alias: alias
      };
      return $http.post('application/alias', data);
    }

    function removeAlias(applicationName, alias) {
      return $http.delete('application/' + applicationName + '/alias/' + alias);
    }
  }
})();

