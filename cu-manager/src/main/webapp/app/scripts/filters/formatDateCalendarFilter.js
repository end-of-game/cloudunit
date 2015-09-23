(function () {
  'use strict';
  angular
    .module('webuiApp.filters')
    .filter('formatdatecalendar', formatdatecalendarfilter);

  function formatdatecalendarfilter() {
    return function (timestamp) {
      if (!timestamp) {
        return;
      }
      return moment(timestamp).calendar();
    };
  }
})();




