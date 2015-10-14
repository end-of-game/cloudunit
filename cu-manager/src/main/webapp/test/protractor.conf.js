/*
 * LICENCE : CloudUnit is available under the Gnu Public License GPL V3 : https://www.gnu.org/licenses/gpl.txt
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

exports.config = {
  seleniumAddress: 'http://localhost:4444/wd/hub',
  specs: [
    '../test/e2e/dashboard/tomcat7.spec.js',
    '../test/e2e/editApplication/editApplication.spec.js',
    '../test/e2e/editApplication/editApplication.overview.spec.js',
    '../test/e2e/editApplication/editApplication.alias.spec.js',
    '../test/e2e/editApplication/editApplication.addModule.spec.js',
    '../test/e2e/editApplication/editApplication.configJVM.spec.js',
    '../test/e2e/editApplication/editApplication.deploy.spec.js'
  ],

  multiCapabilities: [
    /*{
     'browserName': 'firefox'
     },*/
    {
      'browserName': 'chrome',
      'chromeOptions': {
        args: [],
        extensions: []
      }
    }
  ],

  chromeDriver: '/usr/local/lib/node_modules/protractor/selenium/chromedriver',
  params: {
    loginUser: {
      login: 'scott',
      password: 'abc2015'
    },
    loginAdmin: {
      login: 'johndoe',
      password: 'abc2015'
    }
  },
  suites: {
    login: '../test/e2e/login/login.spec.js',
    routeAdmin: '../test/e2e/routes/role-admin.spec.js',
    routeUser: '../test/e2e/routes/role-user.spec.js',
    dashboard: '../test/e2e/dashboard/dashboard.spec.js',
    editApplication: '../test/e2e/editApplication/editApplication.spec.js',
    editApplicationOverview: '../test/e2e/editApplication/editApplication.overview.spec.js',
    editApplicationAlias: '../test/e2e/editApplication/editApplication.alias.spec.js',
    editApplicationAddModule: '../test/e2e/editApplication/editApplication.addModule.spec.js',
    editApplicationConfigJVM: '../test/e2e/editApplication/editApplication.configJVM.spec.js',
    editApplicationDeploy: ['../test/e2e/editApplication/editApplication.deploy.spec.js'],
    deployTomcat6: ['../test/e2e/deployments/tomcat6.spec.js'],
    deployTomcat7: ['../test/e2e/deployments/tomcat7.spec.js'],
    deployTomcat8: ['../test/e2e/deployments/tomcat8.spec.js']
  },
  baseUrl: 'http://localhost:9000',
  framework: 'jasmine',
  jasmineNodeOpts: {
    // If true, display spec names.
    isVerbose: true,
    // If true, print colors to the terminal.
    showColors: true,
    // If true, include stack traces in failures.
    includeStackTrace: true,
    // Default time to wait in ms before a test fails.
    defaultTimeoutInterval: 30000
  },
  onPrepare: function () {
    global.dvr = browser.driver;
    global.login = function (user) {
      browser.driver.get('http://localhost:9000');
      browser.driver.findElement(by.id('username')).sendKeys(user.login);
      browser.driver.findElement(by.id('password')).sendKeys(user.password);
      browser.driver.findElement(by.id('login-btn')).click();
      browser.driver.sleep('1000');
    };
    global.logout = function () {
      browser.driver.findElement(by.id("logout-btn")).click();
    };

    browser.driver.manage().window().maximize();
  }
};
