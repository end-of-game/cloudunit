'use strict';

var AppBar = function () {
  this.brand = element(by.css('.brand'));
  this.accountLink = element(by.id("account-link"));
  this.logoutBtn = element(by.id("logout-btn"));
};


describe('E2E: routes : role admin', function () {
  var ptor, appBar;

  login(browser.params.loginAdmin);

  beforeEach(function () {
    ptor = protractor.getInstance();
    ptor.ignoreSynchronization = true;
    appBar = new AppBar();
  });

  describe('brand', function () {
    it('should navigate to dashboard', function () {
      browser.get('/#/dashboard');
      appBar.brand.click().then(function () {
        expect(browser.getLocationAbsUrl()).toMatch('/dashboard');
        browser.sleep(1000);
      })
    })
  });

  describe('account link', function () {
    it('should navigate to account', function () {
      appBar.accountLink.click();
      browser.sleep(1000);
      expect(browser.getLocationAbsUrl()).toMatch('/account/admin');

    })
  });

  describe('logout', function () {
    it('should navigate to login', function () {
      appBar.logoutBtn.click();
      browser.sleep(1000);
      expect(browser.getLocationAbsUrl()).toMatch('/login');
    })
  });
});

