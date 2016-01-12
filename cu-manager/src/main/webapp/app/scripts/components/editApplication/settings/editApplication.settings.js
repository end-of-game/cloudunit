/*
 * LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
 * but CloudUnit is licensed too under a standard commercial license.
 * Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
 * If you are not sure whether the AGPL is right for you,
 * you can always test our software under the AGPL and inspect the source code before you contact us
 * about purchasing a commercial license.
 *
 * LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
 * or promote products derived from this project without prior written permission from Treeptik.
 * Products or services derived from this software may not be called "CloudUnit"
 * nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
 * For any questions, contact us : contact@treeptik.fr
 */


(function () {
  'use strict';
  angular
    .module('webuiApp.editApplication')
    .directive('editAppSettings', ['$compile', '$timeout', Settings]);

  function Settings($compile, $timeout){
    return {
      restrict: 'E',
      template: '<div class="tab-pane vertical-spacing"></div>',
      scope: {
        application: '=app'
      },
      controller: ['$scope', SettingsCtrl],
      controllerAs: 'settings',
      bindToController: true,
      replace: true,
      link: function(scope, element, attrs, ctrl){
        var tpl = [
          '<jvm-component app="settings.application"></jvm-component>',
          '<alias-component app="settings.application"></alias-component>',
          '<ports-component app="settings.application"></ports-component>'
        ].join('');

        angular.element(element).append(tpl);

        $timeout(function(){
          $compile(element.contents())(scope);
        }, 0);
      }
    }
  }

  function SettingsCtrl($scope) {
    console.log(this.application);
  }
})();
