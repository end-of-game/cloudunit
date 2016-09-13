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

var https = require('https');
var fs = require('fs');

var DeploySection = function () {
    "use strict";
    this.path = require('path');
    this.uploadFileInput = element(by.id('upload-file'));
    this.uploadBtn = element(by.css('.upload-btn'));
    this.infoName = element(by.binding('uploader.queue[0].file.name'));
    this.infoSize = element(by.binding('uploader.queue[0].file.size'));
    this.uploadMeta = element(by.css('.upload-meta'));
    this.clearBtn = element(by.css('.clear-btn'));
    this.cancelBtn = element(by.css('.cancel-btn'));
    this.fileTypeError = element(by.css('.file-type-error'));
    this.appStatusError = element(by.css('.app-status-error'));
    this.progressBar = element(by.css('.upload-progress'));
    this.deployFile = function (filePath) {
        var absolutePath = this.path.resolve(__dirname, filePath);
        return element(by.id('upload-file')).sendKeys(absolutePath);
    };
    this.download = function(url, dest) {
        var file = fs.createWriteStream(dest);
        var request = https.get(url, function(response) {
            https.get(response.headers.location, function(response) {
                response.pipe(file);
                file.on('finish', function() {
                    file.close();  // close() is async, call cb after close completes.
                    return true;
                });
            }).on('error', function(err) {
                fs.unlink(dest);
                console.log(err);
            });

        }).on('error', function(err) { // Handle errors
            fs.unlink(dest); // Delete the file async. (But we don't check the result)
            console.log(err);
        });
    };
};


describe('E2E Test: Edit Application Deploy War', function () {
    "use strict";

    var deploy, editApp, dashboard;

    login(browser.params.loginAdmin);

    beforeEach(function () {
        deploy = new DeploySection();
        editApp = components.EditApplicationPage;
        dashboard = components.DashboardPage;
    });

    describe('on adding file', function () {

        it('should display file infos', function () {
            browser.wait(function() {
                deploy.download('https://github.com/Treeptik/cloudunit/releases/download/1.0/performances.sd.0.1.war',
                'test/uploads/performances.war');
                return true;
            }, browser.params.sleep.medium).then(function () {
                 // set test environment
                dashboard.createApp('testDeploy', 1);
                browser.get('/#/editApplication/testDeploy/deploy');

                deploy.deployFile('../../uploads/performances.war');
                expect(deploy.infoName.getText()).not.toBe('');
                expect(deploy.infoSize.getText()).not.toBe('');   
            });    
        });

        it('remove file on clear button click', function () {
            deploy.clearBtn.click().then( function() {
                expect(element(by.css('.upload-meta')).getAttribute('class')).toContain('ng-hide');
            });
        });

        xit('should show an error message if file type not authorized', function () {
            var fs = require('fs');
            /*fs.writeFile("./test/uploads/deploy.txt", "Test deploy .txt", function(err) {
                if(err) {
                    return console.log(err);
                }*/
                browser.wait(function() {
                    deploy.download('https://github.com/Treeptik/cloudunit/releases/download/1.0/phpredis.zip',
                    'test/uploads/test.zip');
                    return true;
                }, browser.params.sleep.medium).then(function () {
                    deploy.deployFile('../../uploads/test.zip').then(function () {
                        browser.driver.sleep(browser.params.sleep.large);
                        expect(deploy.fileTypeError.isPresent()).toBeTruthy();
                    }); 
                });
           // });
        });

        describe('on file upload', function () {
            it('should show a progress bar', function () {
                deploy.deployFile('../../uploads/performances.war');
                deploy.uploadBtn.click();
                expect(deploy.progressBar.isDisplayed()).toBeTruthy();
                editApp.overviewTab.click();
            });

            it('should show a preview button after file upload', function () {
                browser.driver.sleep(browser.params.sleep.large).then(function () {
                    expect(editApp.previewLink.getAttribute('class')).not.toContain('disabled');
                });
            })
        });

        describe('preview application', function () {
            it('should open a new browser window', function () {
                editApp.previewLink.click();
                browser.getAllWindowHandles().then(function (handles) {
                    var newWindowHandle = handles[1];
                    browser.switchTo().window(newWindowHandle).then(function () {
                        expect(browser.driver.getCurrentUrl()).toBe('http://testdeploy-johndoe-admin.cloudunit.dev/');
                    });
                });
            });

            it('should display content', function () {
                browser.ignoreSynchronization = true;
                browser.getAllWindowHandles().then(function (handles) {
                    var newWindowHandle = handles[1];
                    browser.switchTo().window(newWindowHandle).then(function () {
                        var el = browser.driver.findElement(by.css('h1'));
                        browser.sleep(browser.params.sleep.small)
                        expect(el.getText()).toMatch('Hello World');
                        //to close the current window
                        browser.driver.close().then(function () {
                            //to switch to the previous window
                            browser.switchTo().window(handles[0]);
                        });
                    });
                });
                browser.ignoreSynchronization = false;

                // reset test environment
                browser.get('/#/dashboard');
                browser.driver.sleep(browser.params.sleep.small);
                dashboard.deleteApp('testdeploy');
                 
                //delete upload/create file
                fs.unlinkSync('./test/uploads/performances.war');
                //fs.unlinkSync('./test/uploads/test.zip');
                logout();
            });
        });
    });
});
