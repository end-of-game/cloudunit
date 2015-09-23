var EditApplicationPage = require('../../pages/EditApplicationPage');
var DashboardPage = require('../../pages/DashboardPage');

var OverviewSection = function () {
  'use strict';
  this.serverBtn = element(by.css('.server-btn'));
  this.serverStatus = element(by.binding('editApp.application.status'));
  this.creationDate = element(by.id('creation-date'));
  this.serverName = element(by.id('server'));
  this.gitAddress = element(by.id('git-address'));
  this.jvmMemory = element(by.id('jvm-memory'));
  this.jvmOption = element(by.id('jvm-options'));
  this.jvmRelease = element(by.id('jvm-release'));
  this.serverManagerLink = element(by.id('server-manager'));
};

describe('E2E: Edit Application Overview', function () {
  'use strict';

  var ptor, overview, editApp, dashboard;

  login(browser.params.loginAdmin);

  beforeEach(function () {
    ptor = protractor.getInstance();
    ptor.ignoreSynchronization = true;
    editApp = new EditApplicationPage();
    overview = new OverviewSection();
    dashboard = new DashboardPage();
  });

  describe('Application details', function () {
    it('should display application main properties', function () {

      // set test environment
      dashboard.createApp('testOverview', 1);
      browser.driver.sleep(6000);
      browser.get('/#/editApplication/testOverview/overview');
      browser.driver.sleep(2000);

      browser.driver.sleep(1000);
      expect(overview.creationDate.getAttribute('value')).not.toEqual('');
      expect(overview.serverName.getAttribute('value')).not.toEqual('');
      expect(overview.gitAddress.getAttribute('value')).not.toEqual('');
      expect(overview.jvmMemory.getAttribute('value')).not.toEqual('');
      expect(overview.jvmRelease.getAttribute('value')).not.toEqual('');
      browser.driver.sleep(1000);
    })
  });

  describe('toggle server', function () {
    it('should stop server', function () {
      overview.serverBtn.click();
      browser.driver.sleep(3000);
      expect(overview.serverStatus.getText()).toEqual('Stop');
      browser.driver.sleep(500);
    });

    it('should start server', function () {
      overview.serverBtn.click();
      browser.driver.sleep(3000);
      expect(overview.serverStatus.getText()).toEqual('Start');
      browser.driver.sleep(500);

      // reset test environment
      browser.get('/#/dashboard');
      browser.driver.sleep(3000);
      dashboard.deleteApp('testOverview');
      browser.driver.sleep(3000);
      logout();
    })
  });
});
