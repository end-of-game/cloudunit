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

var Module = function () {
    "use strict";
    this.findModule = function (name) {
        return element(by.id(name));
    };
};

describe('E22: Edit Application Add Module', function () {
    "use strict";

    var editApp, dashboard, module;

    beforeEach(function () {
        editApp = components.EditApplicationPage;
        dashboard = components.DashboardPage;
        module = new Module();
    });

    login(browser.params.loginAdmin);

    describe('add mysql module', function () {
        it('should display a spinner when being created', function () {
            // set test environment
            dashboard.createApp('testModule', 1);
            browser.driver.sleep(browser.params.sleep.large);
            
            browser.get('/#/editApplication/testModule/addModule');
            var buttonModule = element(by.repeater('image in modules.moduleImages').row(0));
            buttonModule.$('button').click(function () {
                expect(element(by.css('.spinner')).isPresent()).toBeTruthy();
            })
        });

        it('should appear in installed module list on overview section', function () {
            editApp.overviewTab.click();
            expect(element(by.repeater('module in overview.app.modules').row(0)).isDisplayed()).toBeTruthy();
        });

        it('should have database info', function () {
            var theModule = $('[id$="johndoe-testmodule-mysql-5-5-1"]');
            expect(theModule.element(by.id('database')).getAttribute('value')).not.toBe('');
            expect(theModule.element(by.id('host')).getAttribute('value')).not.toBe('');
            expect(theModule.element(by.id('username')).getAttribute('value')).not.toBe('');
        });

        it('should show/hide password', function () {
            var theModule = $('[id$="johndoe-testmodule-mysql-5-5-1"]');
            var showPassBtn = theModule.element(by.css('.showPass-btn'));
            var password = theModule.element(by.id('password-0'));

            expect(password.isDisplayed()).toBeFalsy();

            showPassBtn.click().then(function () {
                expect(password.isDisplayed()).toBeTruthy();
            });

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
            var theModule = $('[id$="johndoe-testmodule-mysql-5-5-1"]');
            var toggleModal = theModule.element(by.css('.toggle-modal'));
            var modal = $('.modal[id$="johndoe-testmodule-mysql-5-5-1"]');

            toggleModal.click();
            //browser.driver.wait(protractor.until.elementIsVisible(modal), browser.params.sleep.medium);
            browser.driver.sleep(browser.params.sleep.medium);
            expect(modal.getCssValue('display')).toBe('block');
        });

        it('should remove module on confirmation', function () {
            var theModule = $('[id$="johndoe-testmodule-mysql-5-5-1"]');
            var modal = $('.modal[id$="johndoe-testmodule-mysql-5-5-1"]');
            var removeBtn = modal.element(by.css('.remove-btn'));

            removeBtn.click();
            //browser.driver.wait(protractor.until.elementIsVisible(element(by.css('.modules-list .no-data'))), browser.params.sleep.medium);
            browser.driver.sleep(browser.params.sleep.medium);
            expect(element(by.css('.modules-list .no-data')).isDisplayed()).toBeTruthy();
            
            // reset test environment
            browser.get('/#/dashboard');
            dashboard.deleteApp('testmodule');
            logout();
        });
    })
});
