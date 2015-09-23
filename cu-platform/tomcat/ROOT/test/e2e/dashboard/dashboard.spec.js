var DashboardPage = require('../../pages/DashboardPage');

describe('E2E: dashboard', function () {
  var ptor, dashboard;

  login(browser.params.loginAdmin);

  beforeEach(function () {
    ptor = protractor.getInstance();
    ptor.ignoreSynchronization = true;
    dashboard = new DashboardPage();
  });

  describe('create application', function () {
    beforeEach(function () {
      dashboard.applicationNameInput.clear();
    });
    it('should have a create application form', function () {
      expect(dashboard.createAppForm.isPresent()).toBeTruthy();
    });

    it('should have submit button disabled', function () {
      expect(dashboard.createBtn.getAttribute('disabled')).toBeTruthy();
    });

    it('should create an application', function () {
      dashboard.setApplicationName('testApp');
      dashboard.dropdownToggle.click().then(function () {
        element(by.repeater('serverImage in createApplication.serverImages').row(1)).click()
          .then(function () {
            dashboard.createBtn.click()
              .then(function () {
                var newApp = dashboard.findApplication('testApp');
                browser.driver.sleep(20000)
                  .then(function () {
                    expect(newApp.isPresent()).toBe(true);
                  })
              })
          })
      });
    });

    it('should display error message if application already exists', function () {
      dashboard.setApplicationName('testApp');

      browser.driver.sleep(500);
    });

    it('should display error message if application contains invalid caracters', function () {
      dashboard.setApplicationName('test@App');
      browser.driver.sleep(500);
      expect(dashboard.formatErrorMessage.isPresent()).toBe(true);
    });

    it('should display a spinner when application is being created', function () {
      dashboard.setApplicationName('testApp2');
      dashboard.dropdownToggle.click().then(function () {
        element(by.repeater('serverImage in createApplication.serverImages').row(1)).click()
          .then(function () {
            dashboard.createBtn.click()
              .then(function () {
                expect(dashboard.spinner.getCssValue('display')).toBe('block');
              })
          })
      });
    })
  });

  describe('application list', function () {
    it('should have two applications', function () {
      browser.driver.sleep(20000);
      expect(dashboard.applications.count()).toBe(2);
    })
  });

  describe('delete application', function () {
    var appToDelete, toggleModal, modal, deleteBtn;
    beforeEach(function () {
      appToDelete = dashboard.findApplication('testApp2');
      toggleModal = appToDelete.element(by.css('.toggle-modal'));
      modal = appToDelete.element(by.css('.modal'));
      deleteBtn = modal.element(by.css('.delete-btn'));
      browser.driver.sleep(1000);
    });

    it('should toggle a modal window', function () {
      toggleModal.click();
      browser.driver.sleep(1000);
      expect(modal.getCssValue('display')).toBe('block');
    });

    it('should delete application', function () {
      deleteBtn.click()
        .then(function () {
          browser.driver.sleep(10000)
            .then(function () {
              expect(appToDelete.isPresent()).toBe(false);
              expect(dashboard.applications.count()).toBe(1);
            })
        });
    })
  });

  describe('link to editApplication application', function () {
    var appToEdit, editBtn;
    it('should navigate to editApplication application view', function () {
      appToEdit = dashboard.findApplication('testApp');
      editBtn = appToEdit.element(by.css('.edit-btn'));

      editBtn.click().then(function () {
        expect(browser.getLocationAbsUrl()).toMatch('/editApplication/testApp');
      });
      browser.get('/#/dashboard');
    })
  });

  describe('toggle server', function () {
    var appToEdit, serverBtn;
    beforeEach(function () {
      appToEdit = dashboard.findApplication('testApp');
      serverBtn = appToEdit.element(by.css('.server-btn'));
    });
    it('should stop server', function () {
      var before, after;
      serverBtn.click();
      browser.driver.sleep(2000).then(function () {
        after = appToEdit.element(by.binding('application.status')).getText();
        expect(after).toEqual('Stop');
      })
    });

    it('should start server', function () {
      var before, after;
      serverBtn.click();
      browser.driver.sleep(2000).then(function () {
        after = appToEdit.element(by.binding('application.status')).getText();
        expect(after).toEqual('Start');
      });


      // reset test environment
      dashboard.deleteApp('testApp');
      browser.driver.sleep(3000);
      logout();
    });
  });
});
