/**
 * Created by htomaka on 05/01/16.
 */
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
  angular.module ( 'webuiApp.shared' )
    .directive ( 'cuTags', CuTags );

  function CuTags () {
    return {
      restrict: 'E',
      template: [
        '<div class="cu__tags clearfix">',
          '<p class="label">Tags</p>',
          '<cu-tag ng-repeat="tag in tags.list" tag="tags.list[$index]" tag-index="$index" on-remove="tags.handleRemove(event, index)"></cu-tag>',
          '<cu-tags-input on-add="tags.handleAdd(event, tag)" on-remove="tags.handleRemove(event, index)"></cu-tags-input>',
        '</div>'
      ].join ( '' ),
      scope: {
        list:'=',
        readOnly: '=',
        onRemove: '&',
        onAdd: '&'
      },
      controller: [function(){
        this.handleRemove = function(event, index){
          return this.onRemove({event: event, index: index});
        };

        this.handleAdd = function(event, tag){
          this.onAdd({event: event, tag: tag});
        }

      }],
      controllerAs: 'tags',
      bindToController: true
    }
  }
} ());
