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
  angular.module ( 'webuiApp.tagsInput', [] )
    .directive ( 'cuTagsInput', CuTagsInput );

  function CuTagsInput () {
    return {
      restrict: 'E',
      template: [
        '<div class="cu__tags-input-container">',
          '<input type="text" placeholder="Add tag..." ng-model="input.tag" ng-keydown="input.handleKeyDown($event, input.tag)" ng-blur="input.handleBlur($event, input.tag)" >',
        '</div>'
      ].join ( '' ),
      scope: {
        onAdd: '&',
        onRemove: '&'
      },
      controller: [function () {
        this.tag = "";
        this.handleKeyDown = function ( event, tag ) {
          // handle enter key
          if ( event.keyCode === 13 ) {
            this.handleAdd ( event, tag );
            event.preventDefault();
            // handle backspace key
          } else if ( !tag.length && event.keyCode === 8 ) {
            this.handleRemove ( event, -1 );
          }
        };

        this.handleBlur = function ( event, tag ) {
          this.handleAdd ( event, tag );
        };

        this.handleAdd = function ( event, tag ) {
          this.onAdd ( { event: event, tag: tag } );
          this.tag = '';
        };

        this.handleRemove = function ( event, index ) {
          return this.onRemove ( { event: event, index: index } );
        };
      }],
      controllerAs: 'input',
      bindToController: true
    }
  }
} ());
