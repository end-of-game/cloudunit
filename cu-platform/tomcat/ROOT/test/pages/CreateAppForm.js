/**
 * Created by htomaka on 27/03/15.
 */

var CreateAppForm = (function () {
  function CreateAppForm() {
    "use strict";
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

  return CreateAppForm;

})();

module.exports = CreateAppForm;





