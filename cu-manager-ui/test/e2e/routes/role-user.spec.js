
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

var AppBar = function(){
  this.brand = element(by.css('.brand'));
  this.accountLink = element(by.id("account-link"));
  this.logoutBtn = element(by.id("logout-btn"));
};


describe('E2E: routes : role user', function(){
  var appBar;

  login(browser.params.loginUser);

  beforeEach(function(){
    browser.ignoreSynchronization = true;
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

