(function () {
  'use strict';

  /**
   * @ngdoc service
   * @name webuiApp.imagesservice
   * @description
   * # imagesservice
   * Factory in the webuiApp.
   */
  angular
    .module('webuiApp')
    .factory('ImageService', ImageService);

  ImageService.$inject = ['$resource'];


  function ImageService($resource) {

    return {
      findEnabled: findEnabled,
      findEnabledServer: findEnabledServer,
      findEnabledModule: findEnabledModule,
      findAll: findAll
    };


    //////////////////////////////////////////

    // Liste de toutes les images qui sont activés quelque soit leur type
    function findEnabled() {
      var list = $resource('image/enabled');
      return list.query();
    }

    // Liste de toutes les images de type server
    function findEnabledServer() {
      var list = $resource('image/server/enabled');
      return list.query().$promise;
    }

    // Liste de toutes les images de type module
    function findEnabledModule() {
      var list = $resource('image/module/enabled');
      return list.query().$promise;
    }

    // Liste de toutes les images
    function findAll() {
      var listImages = $resource('image/all');
      return listImages.query();
    }
  }

})();

