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

var DashboardPage = (function () {
  function DashboardPage() {
    "use strict";
    this.container = element(by.id("dashboard"));
    this.createAppForm = element(by.id('create-application-form'));
    this.applications = element.all(by.repeater('application in dashboard.applications'));
    this.findApplication = function (applicationName) {
      return element(by.id('application-' + applicationName));
    };
    this.deleteApp = function(name){
      var appToDelete, toggleModal, modal;
      appToDelete = this.findApplication(name);
      toggleModal = appToDelete.element(by.css('.toggle-modal'));
      modal = appToDelete.element(by.css('.modal'));
      toggleModal.click();
      modal.element(by.css('.delete-btn')).click();
    };
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
  }

  return DashboardPage;

})();

module.exports = DashboardPage;

