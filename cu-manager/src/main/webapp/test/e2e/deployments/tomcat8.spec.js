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

var EditApplicationPage = require('../../pages/EditApplicationPage');

var DashboardPage = require('../../pages/DashboardPage');

var DeploySection = function () {
  "use strict";
  this.path = require('path');
  this.uploadFileInput = element(by.id('upload-file'));
  this.uploadBtn = element(by.css('.upload-btn'));
  this.infoName = element(by.binding('uploader.queue[0].file.name'));
  this.infoSize = element(by.binding('uploader.queue[0].file.size'));
  this.uploadMeta = element(by.css('.upload-meta'));
  this.clearBtn = element(by.css('.clear-btn'));
  this.cancelBtn = element(by.css('.cancel-btn'));
  this.fileTypeError = element(by.css('.file-type-error'));
  this.appStatusError = element(by.css('.app-status-error'));
  this.progressBar = element(by.css('.upload-progress'));
  this.deployFile = function (filePath) {
    var absolutePath = this.path.resolve(__dirname, filePath);
    return this.uploadFileInput.sendKeys(absolutePath);
  }
};

var CreateAppForm = function () {
  this.formContainer = element(by.id('create-application'));
  this.createAppForm = element(by.id('create-application-form'));
  this.applicationNameInput = element(by.model('createApplication.applicationName'));
  this.dropdownToggle = this.createAppForm.element(by.css('.dropdown-toggle'));
  this.createBtn = element(by.id('create-btn'));
  this.errorMessage = element(by.binding('createApplication.message'));
  this.formatErrorMessage = element(by.css('.format'));
  this.spinner = this.formContainer.element(by.css('.spinner'));
  this.setApplicationName = function (name) {
    return this.applicationNameInput.sendKeys(name);
  };
  this.createApp = function (appName, serverChoice) {
    var self = this;
    self.setApplicationName(appName);
    self.dropdownToggle.click().then(function () {
      element(by.repeater('serverImage in createApplication.serverImages').row(serverChoice)).click()
        .then(function () {
          self.createBtn.click()
        })
    });
  }
};


describe('E2E Test: Deploy app with tomcat8 and mysql', function () {
  "use strict";
  var ptor, deploy, editApp, createAppForm, dashboard;

  login(browser.params.loginAdmin);
  createAppForm = new CreateAppForm();
  createAppForm.createApp('tomcat8', 2);
  browser.driver.sleep(6000);
  browser.get('/#/editApplication/tomcat8/addModule');
  browser.driver.sleep(2000);
  element(by.repeater('moduleImage in modules.moduleImages').row(0)).click();
  browser.driver.sleep(15000);

  beforeEach(function () {
    ptor = protractor.getInstance();
    ptor.ignoreSynchronization = true;
    deploy = new DeploySection();
    editApp = new EditApplicationPage();
    dashboard = new DashboardPage();
  });


  it('should display web application', function () {
    browser.get('/#/editApplication/tomcat8/deploy');
    browser.driver.sleep(6000);
    deploy.deployFile('../../uploads/pizzashop-0.0.1-SNAPSHOT.war');
    deploy.uploadBtn.click();
    browser.driver.sleep(15000);
    editApp.previewLink.click();
    browser.getAllWindowHandles().then(function (handles) {
      var newWindowHandle = handles[1];
      browser.switchTo().window(newWindowHandle).then(function () {
        var el = element(by.css('h1'));
        expect(el.getText()).toMatch('List of All Pizzaaaaaaaaaaaa');
        //to close the current window
        browser.driver.close().then(function () {
          //to switch to the previous window
          browser.switchTo().window(handles[0]);
        });
      });
    });
  });

  afterEach(function(){
    browser.get('/#/dashboard');
    browser.driver.sleep(3000);
    dashboard.deleteApp('tomcat8');
    browser.driver.sleep(3000);
    logout();
  })
});
