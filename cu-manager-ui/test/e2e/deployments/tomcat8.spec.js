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

var fs = require('fs');
var path = require('path');

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

describe('E2E Test: Deploy app with tomcat8 and mysql', function () {
  "use strict";
  var deploy, editApp, dashboard;

  login(browser.params.loginAdmin);
  
  beforeEach(function () {
    editApp = components.EditApplicationPage;
    dashboard = components.DashboardPage;
    deploy = components.DeployPage;
  });

  it('should create a tomcat8 app with mysql database', function() {
    dashboard.createAppByName('tomcat8', 'tomcat 8');
    browser.get('/#/editApplication/tomcat8/addModule');
    element(by.repeater('image in modules.moduleImages').row(0)).click().then(function() {
      editApp.overviewTab.click().then(function() {
        expect(element(by.repeater('module in overview.app.modules').row(0)).isDisplayed()).toBeTruthy();
      });
    });
  });

  it('should display web application', function () {
    browser.get('/#/editApplication/tomcat8/deploy');
    browser.sleep(browser.params.sleep.smallest);
    browser.wait(function() {
        deploy.download('https://github.com/Treeptik/cloudunit/releases/download/1.0/performances.sd.0.1.war',
        'test/uploads/performances.war');
        return true;
    }, browser.params.sleep.medium).then(function () {
      browser.sleep(browser.params.sleep.small);
      /*deploy.deployFile('../../uploads/performances.war');
      deploy.deployFile('../../uploads/performances.war');
      deploy.deployFile('../../uploads/performances.war');
*/    
      browser.driver.sleep(browser.params.sleep.large);
      var lol = path.resolve(__dirname, 'test/uploads/performances.war');
      console.log(lol);
      element(by.id('upload-file')).sendKeys('/home/stagiaire/cloudunit/cu-manager/src/main/webapp/test/uploads/performances.war'); 

      
      deploy.uploadBtn.click();
      //expect(deploy.progressBar.isDisplayed()).toBeTruthy();
      browser.driver.sleep(browser.params.sleep.small);
      editApp.overviewTab.click().then(function() {
        browser.driver.sleep(browser.params.sleep.small);
        editApp.previewLink.click().then(function() {
          browser.getAllWindowHandles().then(function (handles) {
            var newWindowHandle = handles[1];
            browser.switchTo().window(newWindowHandle).then(function () {
              browser.driver.sleep(browser.params.sleep.large);
              var el = browser.driver.findElement(by.css('h1'));
              browser.sleep(browser.params.sleep.small)
              expect(el.getText()).toMatch('Hello World');
              //var el = element(by.css('h1'));
              //expect(el.getText()).toMatch('List of All Pizzaaaaaaaaaaaa');
              //to close the current window
              browser.driver.close().then(function () {
                //to switch to the previous window
                browser.switchTo().window(handles[0]);
                  // reset test environment
                  browser.get('/#/dashboard');
                  dashboard.deleteApp('tomcat8');

                  //delete upload/create file
                  fs.unlinkSync('./test/uploads/performances.war');
                  logout();
              });
            });
          });
        });
      })
    });
  });

});
