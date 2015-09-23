/**
 * Configuration du module ui.gravatar
 **/

'use strict';

angular
  .module('ui.gravatar')
  .config(configure);

configure.$inject= ['gravatarServiceProvider'];

function configure(gravatarServiceProvider) {
  gravatarServiceProvider.defaults = {
    size: 80,
    'default': 'mm'  // Mystery man as default for missing avatars
  };

  // Use https endpoint
  gravatarServiceProvider.secure = true;
}
