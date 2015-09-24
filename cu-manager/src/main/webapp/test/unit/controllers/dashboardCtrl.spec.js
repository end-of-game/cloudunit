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

describe('Unit Test: DashboardCtrl', function () {
  "use strict";

  var scope, ApplicationService, createController, q, interval;
  beforeEach(module('webuiApp'));

  beforeEach(inject(function ($rootScope, $controller, $q, _ApplicationService_, $interval) {
    scope = $rootScope.$new();
    ApplicationService = _ApplicationService_;
    q = $q;
    interval = $interval;
    createController = function () {
      return $controller('DashboardCtrl as vm', {
        $scope: scope
      })
    }
  }));

  it('should be defined', function () {
    var dashboardCtrl = createController();
    expect(dashboardCtrl).toBeDefined();
  });

  describe('when initialized', function () {
    beforeEach(function () {
      createController();
    });

    it('should have an applications property', function () {
      expect(scope.vm.applications).toBeDefined();
    });

    it('should have a selectedItem property initialized to "all"', function () {
      expect(scope.vm.selectedItem).toBeDefined();
      expect(scope.vm.selectedItem).toBe('All');
    });

    it('should have a search property initialized to empty string', function () {
      expect(scope.vm.search).toBeDefined();
      expect(scope.vm.search).toBe('');
    });

    it('should have a deleteApplication method', function () {
      expect(scope.vm.deleteApplication).toBeDefined();
      expect(typeof scope.vm.deleteApplication).toBe('function');
    });

    it('should have a startApplication method', function () {
      expect(scope.vm.startApplication).toBeDefined();
      expect(typeof scope.vm.startApplication).toBe('function');
    });
  });

  describe('when deleteApplication method is called', function () {

    beforeEach(function () {
      createController();
      spyOn(ApplicationService, 'remove').and.callThrough();

    });

    it('should call remove method on ApplicationService', function () {

      scope.vm.deleteApplication('myApp');

      expect(ApplicationService.remove).toHaveBeenCalledWith('myApp');

    })
  });

  describe('when startApplication method is called', function () {

    beforeEach(function () {
      createController();
      spyOn(ApplicationService, 'start').and.callThrough();

    });

    it('should call start method on ApplicationService', function () {
      scope.vm.startApplication('myApp');
      expect(ApplicationService.start).toHaveBeenCalledWith('myApp');
    })
  });

  describe('when stopApplication method is called', function () {
    beforeEach(function () {
      spyOn(ApplicationService, 'stop').and.callThrough();
      createController();
    });

    it('should call stop method on ApplicationService', function () {
      scope.vm.stopApplication('myApp');
      expect(ApplicationService.stop).toHaveBeenCalledWith('myApp');
    });
  });

  describe('when update method is called', function () {
    var deferred, update,

      mockResponse = [
        {
          name: 'app1'
        },
        {
          name: 'app2'
        }
      ];

    beforeEach(function () {

      deferred = q.defer();

      ApplicationService.list = function () {
        return deferred.promise;
      };

      spyOn(ApplicationService, 'list').and.callThrough();

      createController();

      update = jasmine.createSpy('update');

    });


    it('should update applications list', function () {

      update();

      deferred.resolve(mockResponse);

      scope.$apply();

      expect(ApplicationService.list).toHaveBeenCalled();
      expect(scope.vm.applications).toEqual(mockResponse);

    });
  });

  describe('when scope.$destroy is called', function () {

    beforeEach(function () {
      createController();

      interval.cancel = jasmine.createSpy();
    });

    it('should cancel the interval when scope destroyed', function () {
      scope.$destroy();
      expect(interval.cancel).toHaveBeenCalled();
    })
  });
});
