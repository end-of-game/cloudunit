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

//var waitForPromise = require('../../pages/waitForPromise');

var importer = require('../../pages/importerE2EComponents');
var components = new importer();


var OverviewSection = function () {
  'use strict';
  this.serverBtn = element(by.css('.server-btn'));
  this.serverStatus = element(by.binding('overview.app.status'));
  this.creationDate = element(by.id('creation-date'));
  this.serverName = element(by.id('server'));
  this.jvmMemory = element(by.id('jvm-memory'));
  this.jvmOption = element(by.id('jvm-options'));
  this.jvmRelease = element(by.id('jvm-release'));
  this.serverAdminLink = element(by.id('server-admin-link'));
};

describe('E2E: Edit Application Overview', function () {
  'use strict';

  var overview, editApp, dashboard;
  
  
login(browser.params.loginAdmin);
  beforeEach(function () {
    editApp = components.EditApplicationPage;
    dashboard = components.DashboardPage;
    overview = new OverviewSection();
  });

  describe('Application details', function () {
    it('should display application main properties', function () {
      // set test environment
      browser.get('/#/dashboard');
      dashboard.createApp('testOverview', 1);
      browser.driver.sleep(browser.params.sleep.large);
      
      browser.get('/#/editApplication/testOverview/overview');

      expect(overview.creationDate.getAttribute('value')).not.toEqual('');
      expect(overview.serverName.getAttribute('value')).not.toEqual('');
      expect(overview.jvmMemory.getAttribute('value')).not.toEqual('');
      expect(overview.jvmRelease.getAttribute('value')).not.toEqual('');
      expect(overview.serverAdminLink.getAttribute('value')).not.toEqual('');
    })
  });

  describe('toggle server', function () {
    it('should stop server', function () {
      browser.get('/#/editApplication/testOverview/overview');
      overview.serverBtn.click();
      
      /*
        waitForPromise(overview.serverStatus.getText,
        function (status) {
          return status === 'Stop';
        });
      */
      browser.driver.sleep(browser.params.sleep.large);
      expect(overview.serverStatus.getText()).toEqual('Stop');
    });

    it('should start server', function () {

      browser.get('/#/editApplication/testOverview/overview');
      overview.serverBtn.click();

      browser.sleep(browser.params.sleep.large);
      expect(overview.serverStatus.getText()).toEqual('Start');
      
      /*
        waitForPromise(overview.serverStatus.getText,
        function (status) {
          return status === 'Start';
        }):
      */

      // reset test environment
      browser.get('/#/dashboard');
      dashboard.deleteApp('testoverview');
      logout();

    })
  });
});
