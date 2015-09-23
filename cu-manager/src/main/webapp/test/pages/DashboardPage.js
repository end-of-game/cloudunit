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

