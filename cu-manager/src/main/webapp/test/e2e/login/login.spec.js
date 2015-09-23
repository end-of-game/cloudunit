'use strict';

var LoginPage = function () {
  this.userNameInput = element(by.model('login.user.username'));
  this.passwordInput = element(by.model('login.user.password'));
  this.loginBtn = element(by.id('login-btn'));
  this.alert = element(by.css('.alert'));
  this.header = element(by.id('header'));

  this.getRootUrl = function () {
    browser.get('/');
  };

  this.getLoginUrl = function () {
    browser.get('/#/login');
  };

  this.setUsername = function (username) {
    this.userNameInput.sendKeys(username);
  };

  this.setPassword = function (password) {
    this.passwordInput.sendKeys(password);
  };
};

describe('e2e test: login', function () {
  var loginPage, ptor, params;
  beforeEach(function () {
    ptor = protractor.getInstance();
    ptor.ignoreSynchronization = true;
    loginPage = new LoginPage();
    params = browser.params;
  });


  it('should redirect user to login if not authenticated', function () {
    browser.get('/#/dashboard');
    browser.driver.sleep(1000);
    expect(browser.getLocationAbsUrl()).toMatch('/login');
  });

  it('should redirect to /login if location hash is /#/login', function () {
    loginPage.getLoginUrl();
    expect(browser.getLocationAbsUrl()).toMatch('/login');
  });

  it('should have header hidden', function () {
    expect(loginPage.header.isDisplayed()).toBeFalsy();
  });


  it('should not authenticate a user when the credentials do not match', function () {
    loginPage.getLoginUrl();
    loginPage.setUsername('admin@app.com');
    loginPage.setPassword('invalid');
    loginPage.loginBtn.click().then(function () {
      var cookie = browser.manage().getCookie('isLogged');
      expect(cookie).toBeFalsy();
      expect(loginPage.alert.isDisplayed()).toBeTruthy();
      expect(browser.getLocationAbsUrl()).toMatch('/login');
    });
  });


  it('should successfully authenticate a user when the credentials match', function () {
    loginPage.getLoginUrl();
    loginPage.setUsername(params.loginUser.login);
    loginPage.setPassword(params.loginUser.password);
    loginPage.loginBtn.click().then(function () {
      browser.sleep(500).then(function () {
        expect(browser.getLocationAbsUrl()).toMatch('/dashboard');
      });
    });
  });

  it('should have an isLogged cookie', function () {
    var cookie = browser.manage().getCookie('isLogged');
    expect(cookie).toBeTruthy();
  });

  it('should not redirect to login if user authenticated', function () {
    browser.get('/#/login');
    expect(browser.getLocationAbsUrl()).toMatch('/dashboard');
  });
});


