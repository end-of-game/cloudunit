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

'use strict';

var AppBar = function () {
  this.brand = element(by.css('.brand'));
  this.accountLink = element(by.id("account-link"));
  this.logoutBtn = element(by.id("logout-btn"));
};


describe('E2E: routes : role admin', function () {
  var appBar;

  login(browser.params.loginAdmin);

  beforeEach(function () {
    
    browser.ignoreSynchronization = true;
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

