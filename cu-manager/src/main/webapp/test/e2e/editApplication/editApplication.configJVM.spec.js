/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
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

var EditApplicationPage = require('../../pages/EditApplicationPage');
var DashboardPage = require('../../pages/DashboardPage');
var waitForPromise = require('../../pages/waitForPromise');

var ConfigJVMSection = function () {
  this.submitBtn = element(by.css('.config-jvm-btn'));
  this.optionInput = element(by.id('jvm-param-options'));
  this.setOption = function (option) {
    this.optionInput.sendKeys(option);
  }
};


describe('E2E: Edit Application config JVM', function () {
  "use strict";

  var ptor, configJVM, editApp, dashboard;

  login(browser.params.loginAdmin);

  beforeEach(function () {
    ptor = protractor.getInstance();
    ptor.ignoreSynchronization = true;
    configJVM = new ConfigJVMSection();
    editApp = new EditApplicationPage();
    dashboard = new DashboardPage();
  });

  it('should have a default value', function () {
    dashboard.createApp('testJVM', 1);
    browser.driver.sleep(6000);
    browser.get('/#/editApplication/testJVM/configureJVM');
    browser.driver.sleep(2000);
    expect(element(by.id('memory-512')).getAttribute('checked')).toBeTruthy();
  });

  it('should change jvm configuration', function () {
    var selectedMemory = element(by.id('memory-2048'));
    var labelMemory = $('label[for="memory-2048"]');

    var selectedRelease = element(by.id('release-8'));
    var labelRelease = $('label[for="release-8"]');

    labelMemory.click();
    configJVM.optionInput.sendKeys('-Dfoo=bar');
    labelRelease.click();

    browser.driver.sleep(500);
    configJVM.submitBtn.click();
    browser.driver.sleep(20000);
    expect(selectedMemory.getAttribute('checked')).toBeTruthy();
    expect(selectedRelease.getAttribute('checked')).toBeTruthy();
    expect(configJVM.optionInput.getAttribute('value')).toMatch('-Dfoo=bar');
    browser.driver.sleep(500);
    editApp.overviewTab.click();
    browser.driver.sleep(500);
    expect(element(by.id('jvm-memory')).getAttribute('value')).toMatch('2048 Mo');
    expect(element(by.id('jvm-options')).getAttribute('value')).toMatch('-Dfoo=bar');
    expect(element(by.id('jvm-release')).getAttribute('value')).toMatch('8');

    waitForPromise(element(by.binding('editApp.application.status')).getText,
      function (status) {
        return status === 'Start';
      });

    browser.get('/#/dashboard');
    browser.driver.sleep(3000);
    dashboard.deleteApp('testJVM');
    browser.driver.sleep(3000);
    logout();
  });
});
