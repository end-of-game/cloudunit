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

var EditApplicationPage = require('../../pages/EditApplicationPage');
var DashboardPage = require('../../pages/DashboardPage');
var waitForPromise = require('../../pages/waitForPromise');

var ConfigJVMSection = function () {
    this.submitBtn = element(by.css('.config-jvm-btn'));
    this.optionInput = element(by.id('jvm-param-options'));
    this.setOption = function (option) {
        this.optionInput.sendKeys(option);
    }

    this.selectedMemory = element(by.id('memory-2048'));
    this.labelMemory = $('label[for="memory-2048"]');
    this.lastRelease = element.all(by.repeater('jvmReleases in configjvm.jvmReleases')).last();
    //this.lastReleaseRadioElement = element.all(by.repeater('jvmReleases in configjvm.jvmReleases')).last().element(by.css('input[type="radio"]'));
    this.lastReleaseRadioElement = element(by.repeater('jvmReleases in configjvm.jvmReleases').row(0)).$('input[type="radio"]');
};

describe('E2E: Edit Application config JVM', function () {
    "use strict";

    var configJVM, editApp, dashboard, lastReleaseValue;

    login(browser.params.loginAdmin);

    beforeEach(function () {
        configJVM = new ConfigJVMSection();
        editApp = new EditApplicationPage();
        dashboard = new DashboardPage();
    });

    it('should display the config JVM card in settings url', function () {
// set test environment
dashboard.createApp('testJVM', 1);
browser.driver.sleep(20000);
browser.get('/#/editApplication/testJVM/settings');
expect(element(by.id('config-JVM'))).toBeTruthy();
});

    it('should have a default value : 512 Mo', function () {
        browser.get('/#/editApplication/testJVM/settings');
        expect(element(by.css('input[name="selectedJvmMemory"]:checked')).getAttribute('value')).toBe('512');
    });

    it('should change jvm configuration', function () {
        lastReleaseValue = configJVM.lastReleaseRadioElement.getAttribute('value');
        configJVM.labelMemory.click();
        configJVM.optionInput.sendKeys('-Dfoo=bar');
        configJVM.lastRelease.click();
        configJVM.submitBtn.click();
        browser.driver.sleep(20000);

        expect(configJVM.selectedMemory.getAttribute('checked')).toBeTruthy();
        expect(configJVM.lastReleaseRadioElement.getAttribute('checked')).toBeTruthy();
        expect(configJVM.optionInput.getAttribute('value')).toMatch('-Dfoo=bar');

    });

    it('should display change jvm configuration in overview', function () {
        browser.get('/#/editApplication/testJVM/overview');
        expect(element(by.id('jvm-memory')).getAttribute('value')).toMatch('2048 Mo');
        expect(element(by.id('jvm-options')).getAttribute('value')).toMatch('-Dfoo=bar');
        expect(element(by.id('jvm-release')).getAttribute('value')).toMatch(lastReleaseValue);

/*
waitForPromise(element(by.binding('overview.app.status')).getText,
function (status) {
return status === 'Start';
});
*/
expect(element(by.binding('overview.app.status')).getText()).toEqual('Start');

browser.get('/#/dashboard');
browser.driver.sleep(2000);
dashboard.deleteApp('testjvm');
browser.driver.sleep(2000);
logout();
});
});
