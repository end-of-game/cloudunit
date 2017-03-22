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
   * @name webuiApp.imagesservice
   * @description
   * # imagesservice
   * Factory in the webuiApp.
   */
  angular
    .module('webuiApp')
    .factory('ImageService', ImageService);

  ImageService.$inject = ['$resource', 'TraversonService'];


  function ImageService($resource, TraversonService) {

    var imageTraversonService = new TraversonService.Instance('/images');

    return {
      findEnabled: findEnabled,
      findEnabledServer: findEnabledServer,
      findEnabledModule: findEnabledModule,
      findAll: findAll,
      enable: enable,
      disable: disable,
      remove: remove,
      pull: pull,
      list: list
    };


    //////////////////////////////////////////

    // Liste de toutes les images
    function list() {
			return imageTraversonService
				.traversonService
				.newRequest()
				.getResource()
				.result
				.then(function (res) {
					if (res._embedded) {
						return res._embedded.imageResourceList;
					} else {
						return [];
					}
				}).catch(function (err) { console.error(err); });
    }

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
      return listImages.query().$promise;
    }
    
    // Supprime une image
    function remove(imageID) {
      var dir = $resource ( 'image/:imageID' );
      return dir.delete ( { 
            imageID: imageID
        }, {} ).$promise; 
    }

    // Ajout d'une image
    function pull(image) {
      var dir = $resource ( 'image/pull' );
      return dir.save ( {}, image ).$promise; 
    }

    // rend indisponible l'image
    function disable(imageName) {
      var dir = $resource ( 'image/:imageName/disabled' ,
      { 
          imageName: imageName
      },
      { 
          'update': { 
              method: 'PUT',
              transformResponse: function ( data, headers ) {
                  var response = {};
                  return response;
              }
          }
      }
      );
      return dir.update( { }, {} ).$promise;
    }

    // rend disponible l'image
    function enable(imageName) {

      var dir = $resource ( 'image/:imageName/enabled' ,
      { 
          imageName: imageName
      },
      { 
          'update': { 
              method: 'PUT',
              transformResponse: function ( data, headers ) {
                  var response = {};
                  return response;
              }
          }
      }
      );
      return dir.update( { }, {} ).$promise;
    }

  }

})();
