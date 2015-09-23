var EditApplicationPage = require('../../pages/EditApplicationPage');
var DashboardPage = require('../../pages/DashboardPage');

var AliasSection = function () {
  "use strict";
  this.domain = element(by.id('domain'));
  this.alias = element(by.id('new-alias'));
  this.addAliasBtn = element(by.css('.alias-btn'));
  this.removeAliasBtn = element(by.css('.remove-alias-btn'));
  this.errorMsg = element(by.binding('alias.errorMsg'));
  this.noAliasMsg = element(by.css('.no-alias'));
  this.setAlias = function (domain) {
    return this.alias.sendKeys(domain);
  };
};

describe('E2E: EditApplication', function () {
  "use strict";
  var ptor, editApp, alias, dashboard;

  beforeEach(function () {
    ptor = protractor.getInstance();
    ptor.ignoreSynchronization = true;
    editApp = new EditApplicationPage();
    alias = new AliasSection();
    dashboard = new DashboardPage();
    browser.driver.sleep(1000);
  });

  login(browser.params.loginAdmin);

  describe('E2E: Edit Application Alias', function () {
    it('should display the application url', function () {
      // set test environment

      dashboard.createApp('testAlias', 1);
      browser.driver.sleep(6000);
      browser.get('/#/editApplication/testAlias/alias');
      browser.driver.sleep(2000);
      expect(alias.domain.getAttribute('value')).not.toEqual('');
    });

    describe('Add new alias', function () {
      it('should create a new alias', function () {
        var aliasList;
        alias.setAlias('test');
        browser.driver.sleep(1000);
        alias.addAliasBtn.click();
        browser.driver.sleep(2000);
        aliasList = element.all(by.repeater('aliase in editApp.application.aliases'));
        expect(aliasList.count()).toBe(1);
      });

      it('should display an error message if alias already exists', function () {
        alias.setAlias('test');
        browser.driver.sleep(1000);
        alias.addAliasBtn.click();
        browser.driver.sleep(2000);
        expect(alias.errorMsg.getText()).toMatch('This alias is already used by another application on this CloudUnit instance');
      });
    });

    describe('remove alias', function () {
      it('should remove alias on click', function () {
        var aliasList;
        alias.removeAliasBtn.click();
        browser.driver.sleep(2000);
        expect(alias.noAliasMsg.isPresent()).toBeTruthy();

        // rest test environment
        browser.get('/#/dashboard');
        browser.driver.sleep(3000);
        dashboard.deleteApp('testAlias');
        browser.driver.sleep(3000);
        logout();
      })
    })
  });
});
