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

(function() {
  'use strict';

  angular
    .module('webuiApp')
    .factory('ErrorService', ErrorService);

  ErrorService.$inject = ['$rootScope'];

  function ErrorService($rootScope) {
    return {
      handle: handle,
    };

    function handle(error) {

      if (error.status === 400) {
        console.log(error);
        $rootScope.$broadcast(':unauthorized', {
          message: 'The username or password you entered was incorrect.',
        });
      }

      if (error.status === 401) {
        console.log(error);
        $rootScope.$broadcast(':unauthorized', {
          message: 'The username or password you entered was incorrect.',
        });
      }

      if (error.status === 500 || error.status === 404) {
        $rootScope.$broadcast(':systemError', {
          message: 'Sorry, an internal error has occurred. Please login later or contact support',
        });
      }

      if (error.status === 403) {
        $rootScope.$broadcast(':forbidden', {
          message: 'Sorry, access to this page is restricted',
        });
      }
    }
  }
})();

