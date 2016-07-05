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

var importer = require('../../pages/importerE2EComponents');
var components = new importer();

describe('E2E: EditApplication', function () {
  "use strict";
  var editApp, dashboard;

  login(browser.params.loginAdmin);

  beforeEach(function () {
    browser.ignoreSynchronization=true;
    editApp = components.EditApplicationPage;
    dashboard = components.DashboardPage;
  });

  describe('create an application', function () {

    it('should display a spinner when being created', function () {
      // set test environment
      dashboard.createApp('testApp', 1);
      browser.driver.sleep(browser.params.sleep.small);
      expect(element(by.css('.pending')).isPresent()).toBeTruthy();
    });

    it('should stop display a spinner after being created', function () {
      browser.driver.sleep(browser.params.sleep.large);
      expect(element(by.css('.pending')).isPresent()).toBeFalsy();
    });

    it('should be in start status', function() {
      var dashboardTestApp = dashboard.findApplication('testapp');
      expect(dashboardTestApp.element(by.css('.status')).getText()).toMatch('Start');
      //expect(element(by.id('application-testapp')).element(by.css('.status')).getText()).toMatch('Start');
      //var statusProperty = dashboard.getAppProperty('status', 0);
      //expect(statusProperty.getText()).toMatch('Start');
    });

    it('should appear the server choice in service section', function() {
      var dashboardTestApp = dashboard.findApplication('testapp');
      expect(dashboardTestApp.element(by.css('.features p:nth-child(1)')).getText()).toMatch(dashboard.serverChoice(1).getText());
    });

  });


  describe('page header', function () {

    it('should have application name in page title', function () {
      browser.get('/#/editApplication/testApp/overview');
      expect(editApp.pageTitle.getText()).toMatch('Application testApp');
    });

    describe('go back link', function () {
      it('should exist', function () {
        expect(editApp.goBackLink.isPresent()).toBeTruthy();
      });

      it('should navigate to dashboard', function () {
        editApp.goBackLink.click();
        expect(browser.getLocationAbsUrl()).toMatch('/dashboard');
      })

    });

    // tests tabs about editApplication
    describe('Tabs menu', function () {

      describe('Overview Tab', function () {
        it('should be displayed by default', function () {
          browser.get('/#/editApplication/testApp/overview');
          expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp/overview');
        })
      });

      describe('Add Module Tab', function () {
        it('should display add module section', function () {
          editApp.addModuleTab.click();
          expect(editApp.addModuleTab.isPresent()).toBeTruthy();
          expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp/addModule');
        })
      });

      describe('Deploy Tab', function () {
       it('should display deploy section', function () {
          editApp.deployTab.click();
          expect(editApp.deployTab.isPresent()).toBeTruthy();
          expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp/deploy');
        });
      });

      describe('Explorer Tab', function () {
        it('should display explorer section', function () {
          editApp.explorerTab.click();
          expect(editApp.explorerTab.isPresent()).toBeTruthy();
          expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp/explorer');
        });
      });

      describe('Logs Tab', function () {
        it('should display logs section', function () {
          editApp.logsTab.click();
          expect(editApp.logsTab.isPresent()).toBeTruthy();
          expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp/logs');
        })
      });

      describe('Monitoring Tab', function () {
        it('should display monitoring section', function () {
          editApp.monitoringTab.click();
          expect(editApp.monitoringTab.isPresent()).toBeTruthy();
          expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp/monitoring');
        })
      });

      describe('Snapshot Tab', function () {
        it('should display snapshot section', function () {
          editApp.snapshotTab.click();
          expect(editApp.snapshotTab.isPresent()).toBeTruthy();
          expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp/snapshot');
        });
      });

      describe('Settings Tab', function () {
        it('should display settings section', function () {
          editApp.settingsTab.click();
          expect(editApp.settingsTab.isPresent()).toBeTruthy();
          expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp/settings');
          browser.driver.sleep(browser.params.sleep.smallest);
        });
      });
    })
  });

  describe('delete an application', function () {
      it('should delete an application', function() {
        // reset test environment
        browser.get('/#/dashboard');
        dashboard.deleteApp('testapp');
        expect(element(by.id('application-testApp')).isPresent()).toBeFalsy();
        logout();
      });
  });
});
