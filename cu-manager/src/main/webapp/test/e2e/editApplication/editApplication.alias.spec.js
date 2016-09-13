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

var importer = require('../../pages/importerE2EComponents');
var components = new importer();

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
  var editApp, alias, dashboard;

  login(browser.params.loginAdmin);

  beforeEach(function () {
    editApp = components.EditApplicationPage;
    dashboard = components.DashboardPage;
    alias = new AliasSection();
  });

  describe('E2E: Edit Application Alias', function () {
    it('should display the alias card in settings url', function () {
      // set test environment
      dashboard.createApp('testAlias', 1);
      browser.get('/#/editApplication/testAlias/settings');

      expect(alias.domain.getAttribute('value')).not.toEqual('');
    });

    describe('Add new alias', function () {
      it('should display an error message if alias doesn\'t respect a valid domain name pattern', function () {
        browser.get('/#/editApplication/testAlias/settings');
        var aliasList;
        alias.setAlias('test');
        alias.addAliasBtn.click();

        //browser.driver.wait(protractor.until.elementTextContains(alias.errorMsg, 'This alias must respect a valid domain name pattern'), 8000);
        browser.driver.sleep(browser.params.sleep.medium);
        expect(alias.errorMsg.getText()).toContain('This alias must respect a valid domain name pattern');
      });

      it('should create a new alias', function () {
        browser.get('/#/editApplication/testAlias/settings');
        var aliasList;
        alias.setAlias('treeptik.fr');
        alias.addAliasBtn.click().then(function(){
          browser.driver.sleep(browser.params.sleep.medium);
          aliasList = element.all(by.repeater('aliase in alias.application.aliases'));
          expect(aliasList.count()).toBe(1);
        });
        
      });

      it('should display an error message if alias already exists', function () {
        browser.get('/#/editApplication/testAlias/settings');
        alias.setAlias('treeptik.fr');
        alias.addAliasBtn.click().then(function(){
          expect(alias.errorMsg.getText()).toContain('This alias is already used by another application in CloudUnit instance(s)');
        });        
      });
    });

    describe('remove alias', function () {
      it('should remove alias on click', function () {
        browser.get('/#/editApplication/testAlias/settings');
        var aliasList;
        alias.removeAliasBtn.click().then(function(){
          browser.wait(protractor.ExpectedConditions.presenceOf(alias.noAliasMsg), browser.params.sleep.medium);
          expect(alias.noAliasMsg.isPresent()).toBeTruthy();

          // reset test environment
          browser.get('/#/dashboard');
          browser.sleep(browser.params.sleep.medium);
          dashboard.deleteApp('testalias');
          logout();
        });
      })
    })
  });
});
