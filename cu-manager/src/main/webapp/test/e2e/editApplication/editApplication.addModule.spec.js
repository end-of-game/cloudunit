var DashboardPage = require('../../pages/DashboardPage');

var EditApplicationPage = require('../../pages/EditApplicationPage');

var Module = function () {
  "use strict";
  this.findModule = function (name) {
    return element(by.id(name));
  };
};


describe('E22: Edit Application Add Module', function () {
  "use strict";

  var ptor, editApp, dashboard, module;

  beforeEach(function () {
    ptor = protractor.getInstance();
    ptor.ignoreSynchronization = true;
    editApp = new EditApplicationPage();
    dashboard = new DashboardPage();
    module = new Module();
  });

  login(browser.params.loginAdmin);

  describe('add mysql module', function () {

    it('should display a spinner when being created', function () {

      // set test environment

      dashboard.createApp('testModule', 1);
      browser.driver.sleep(6000);
      browser.get('/#/editApplication/testModule/addModule');
      browser.driver.sleep(2000);


      browser.driver.sleep(1000);
      element(by.repeater('moduleImage in modules.moduleImages').row(0)).click(function () {
        browser.driver.sleep(500);
        expect(element(by.css('.spinner')).isPresent()).toBeTruthy();
      })
    });

    it('should appear in installed module list on overview section', function () {
      editApp.overviewTab.click();
      browser.driver.sleep(20000);
      expect(editApp.moduleList.count()).toBe(1);
    });


    it('should have database info', function () {
      var theModule = module.findModule('johndoe-testModule-mysql-5-5-1');
      expect(theModule.element(by.id('database')).getAttribute('value')).not.toBe('');
      expect(theModule.element(by.id('host')).getAttribute('value')).not.toBe('');
      expect(theModule.element(by.id('username')).getAttribute('value')).not.toBe('');
    });

    it('should show/hide password', function () {
      var theModule = module.findModule('johndoe-testModule-mysql-5-5-1');
      var showPassBtn = theModule.element(by.css('.showPass-btn'));
      var password = theModule.element(by.id('password-0'));

      expect(password.isDisplayed()).toBeFalsy();

      showPassBtn.click().then(function () {
        expect(password.isDisplayed()).toBeTruthy();
      });

      browser.driver.sleep(500);

      showPassBtn.click().then(function () {
        expect(password.isDisplayed()).toBeFalsy();
      });

    });

  });

  describe('phpMyAdmin', function () {
    it("should open phpMyAdmin in new window", function () {
      element(by.css('.phpmyadmin-link')).click();
      browser.getAllWindowHandles().then(function (handles) {
        var newWindowHandle = handles[1];
        browser.switchTo().window(newWindowHandle).then(function () {
          expect(browser.driver.getCurrentUrl()).toBe("http://phpmyadmin1-testmodule-johndoe-admin.cloudunit.dev/phpmyadmin/");
          //to close the current window
          browser.driver.close().then(function () {
            //to switch to the previous window
            browser.switchTo().window(handles[0]);
          });
        });
      });
    });
  });

  describe('remove module', function () {
    it('should display a modal window', function () {
      var theModule = module.findModule('johndoe-testModule-mysql-5-5-1');
      var toggleModal = theModule.element(by.css('.toggle-modal'));
      var modal = theModule.element(by.id('delete-johndoe-testModule-mysql-5-5-1'));

      toggleModal.click();
      browser.driver.sleep(1000);
      expect(modal.getCssValue('display')).toBe('block');
    });

    it('should remove module on confirmation', function () {
      var theModule = module.findModule('johndoe-testModule-mysql-5-5-1');
      var modal = theModule.element(by.id('delete-johndoe-testModule-mysql-5-5-1'));
      var removeBtn = modal.element(by.css('.remove-btn'));

      removeBtn.click();
      browser.driver.sleep(15000).then(function () {
        expect(element(by.css('.modules-list .no-data')).isDisplayed()).toBeTruthy();
      });

      // reset test environment
      browser.get('/#/dashboard');
      browser.driver.sleep(3000);
      dashboard.deleteApp('testModule');
      browser.driver.sleep(3000);
      logout();
    });
  })
});
