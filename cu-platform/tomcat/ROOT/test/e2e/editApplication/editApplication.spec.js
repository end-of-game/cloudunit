var EditApplicationPage = require('../../pages/EditApplicationPage');
var DashboardPage = require('../../pages/DashboardPage');

describe('E2E: EditApplication', function () {
  "use strict";
  var ptor, editApp, dashboard;

  login(browser.params.loginAdmin);

  beforeEach(function () {
    ptor = protractor.getInstance();
    ptor.ignoreSynchronization = true;
    editApp = new EditApplicationPage();
    dashboard = new DashboardPage();
  });

  // test du header de la vue editApplication

  describe('page header', function () {
    it('should have application name in page title', function () {

      // set test environment
      dashboard.createApp('testApp', 1);
      browser.driver.sleep(6000);
      browser.get('/#/editApplication/testApp/overview');
      browser.driver.sleep(2000);

      browser.driver.sleep(2000);
      expect(editApp.pageTitle.getText()).toMatch('Application testApp');
    });

    describe('go back link', function () {
      it('should exist', function () {
        browser.driver.sleep(2000);
        expect(editApp.goBackLink.isPresent()).toBeTruthy();
      });

      it('should navigate to dashboard', function () {
        editApp.goBackLink.click();
        browser.driver.sleep(2000);
        expect(browser.getLocationAbsUrl()).toMatch('/dashboard');
      })
    });

    // tests des tabs de la vue editApplication

    describe('Tabs menu', function () {

      describe('Overview Tab', function () {
        it('should be displayed by default', function () {
          browser.get('/#/editApplication/testApp/overview');
          browser.driver.sleep(1000);
          expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp/overview');
        })
      });

      describe('Alias Tab', function () {
        it('should display alias section', function () {
          editApp.aliasTab.click();
          browser.driver.sleep(1000);
          expect(editApp.aliasContent.isPresent()).toBeTruthy();
          expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp/alias');
        })

      });

      describe('Add Module Tab', function () {
        it('should display add module section', function () {
          editApp.addModuleTab.click();
          browser.driver.sleep(1000);
          expect(editApp.addModuleContent.isPresent()).toBeTruthy();
          expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp/addModule');
        })
      });

      describe('Configure JVM Tab', function () {
        it('should display configure JVM section', function () {
          editApp.jvmConfigTab.click();
          browser.driver.sleep(1000);
          expect(editApp.jvmConfigContent.isPresent()).toBeTruthy();
          expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp/configureJVM');
        })
      });

      describe('Logs Tab', function () {
        it('should display logs section', function () {
          editApp.logsTab.click();
          browser.driver.sleep(3000);
          expect(editApp.logsContent.isPresent()).toBeTruthy();
          expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp/logs');
        })
      });

      describe('Monitoring Tab', function () {
        it('should display monitoring section', function () {
          editApp.monitoringTab.click();
          browser.driver.sleep(3000);
          expect(editApp.monitoringContent.isPresent()).toBeTruthy();
          expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp/monitoring');
        })
      });

      describe('Deploy Tab', function () {
        it('should display deploy section', function () {
          editApp.deployTab.click();
          browser.driver.sleep(1000);
          expect(editApp.deployContent.isPresent()).toBeTruthy();
          expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp/deploy');

        });
      });

      describe('Snapshot Tab', function () {
        it('should display snapshot section', function () {
          editApp.snapshotTab.click();
          browser.driver.sleep(1000);
          expect(editApp.snapshotContent.isPresent()).toBeTruthy();
          expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp/snapshot');
          browser.driver.sleep(1000);

          // reset test environment
          browser.get('/#/dashboard');
          browser.driver.sleep(3000);
          dashboard.deleteApp('testApp');
          browser.driver.sleep(3000);
          logout();
        });
      });
    })
  });
});
