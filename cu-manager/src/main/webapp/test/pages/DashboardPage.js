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

var DashboardPage = (function () {
  function DashboardPage() {
    "use strict";
    this.container = element(by.id("dashboard"));
    this.createAppForm = element(by.id('create-application-form'));
    this.applications = element.all(by.repeater('application in dashboard.applications'));

    this.formContainer = element(by.id('create-application'));
    this.applicationNameInput = element(by.model('createApp.applicationName'));
    this.dropdownToggle = this.createAppForm.element(by.css('.selectize-control input'));
    this.createBtn = element(by.id('create-btn'));
    this.errorMessage = element(by.binding('createApp.message'));
    this.formatErrorMessage = element(by.css('.format'));
    this.spinner = this.formContainer.element(by.css('.spinner'));
    this.setApplicationName = function (name) {
         browser.driver.sleep(browser.params.sleep.small);
        return this.applicationNameInput.sendKeys(name);
    };
    this.createApp = function (appName, serverChoice) {
      var self = this;
      self.setApplicationName(appName);
      self.dropdownToggle.click().then(function () {
          //console.log(element(by.repeater('serverImage in createApp.serverImages').row(1));
          /*
            element(by.repeater('serverImage in createApp.serverImages').row(serverChoice)).click()
            .then(function () {
              self.createBtn.click()
            })
          */
          //self.dropdownToggle.sendKeys(serverChoice);
         
          self.dropdownToggle.sendKeys(protractor.Key.ENTER);
          for(var i = 0; i < serverChoice; i++) {
            self.dropdownToggle.sendKeys(protractor.Key.ARROW_DOWN);  
          }
          self.dropdownToggle.sendKeys(protractor.Key.ENTER);
          browser.actions().sendKeys(protractor.Key.ENTER).perform();
          //self.createBtn.click();
      });

      let selectorAppNameQuery = '#application-' + appName; 
      browser.driver.wait(protractor.until.elementIsVisible($('.pending', selectorAppNameQuery)), 4000);
    }
    this.serverChoice = function (serverChoice) {
      var self = this;
      self.dropdownToggle.sendKeys(protractor.Key.ENTER);
      for(var i = 0; i < serverChoice; i++) {
        self.dropdownToggle.sendKeys(protractor.Key.ARROW_DOWN);  
      }
      return self.dropdownToggle.getAttribute('value');
      //return element(by.repeater('serverImage in createApp.serverImages').row(serverChoice));
    }
    this.getAppProperty = function (propertyName, appChoice) {
      return element(by.repeater('application in dashboard.applications').row(appChoice).column('application.' + propertyName));
    }
    this.findApplication = function (applicationName) {
      return element(by.id('application-' + applicationName));
    };
    this.deleteApp = function (name) {
      var appToDelete, toggleModal, modal;
      appToDelete = this.findApplication(name);
      toggleModal = appToDelete.element(by.css('.toggle-modal'));
      modal = appToDelete.element(by.css('.modal'));
      toggleModal.click();
      modal.element(by.css('.delete-btn')).click();
    };
  }

  return DashboardPage;

})();

module.exports = DashboardPage;

