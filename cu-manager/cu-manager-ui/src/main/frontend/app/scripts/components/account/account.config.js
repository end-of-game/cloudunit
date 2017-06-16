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

'use strict';

angular.module('webuiApp.account', [])
  .config([
    '$stateProvider',
    function ($stateProvider) {
      $stateProvider
        .state('account', {
          abstract: true,
          url: '/account',
          template: '<ui-view/>'
        })
        .state('account.admin', {
          url: '/admin',
          template: '<account-admin></account-admin>',
          data: {
            isFree: false
          }
        })
        .state('account.profil', {
          url: '/profil',
          template: '<account-profil></account-profil>',
          data: {
            isFree: false
          }
        })
        .state('account.image', {
          url: '/image',
          template: '<account-image></account-image>',
          data: {
            isFree: false
          }
        })
        .state('account.registry', {
          url: '/registry',
          template: '<account-registry></account-registry>',
          data: {
            isFree: false
          }
        })
        .state('account.logs', {
          url: '/logs/:login',
          template: '<account-logs></account-logs>',
          data: {
            isFree: false
          }
        });
    }]);
