
'use strict';

var AppBar = function(){
  this.brand = element(by.css('.brand'));
  this.accountLink = element(by.id("account-link"));
  this.logoutBtn = element(by.id("logout-btn"));
};


describe('E2E: routes : role user', function(){
  var ptor, appBar;

  login(browser.params.loginUser);

  beforeEach(function(){
    ptor = protractor.getInstance();
    ptor.ignoreSynchronization = true;
    appBar = new AppBar();
  });

  describe('account link', function(){
    it('should be hidden', function(){
      expect(appBar.accountLink.isPresent()).toBeFalsy();
    })
  });

  describe('route restriction', function(){
    it('should not access to admin account', function(){
      browser.get('/#/account/admin');
      browser.driver.sleep(1000);
      expect(browser.getLocationAbsUrl()).toMatch('/dashboard');
    });

    it('should not access to admin user logs', function(){
      browser.get('/#/account/logs/johndoe');
      browser.driver.sleep(1000);
      expect(browser.getLocationAbsUrl()).toMatch('/dashboard');
      logout();
    })
  });
});

