'use strict';

describe('Unit Test: LoginCtrl', function () {
  var scope, deferred, UserService, createController, $location;

  beforeEach(module('webuiApp'));


  beforeEach(inject(function ($rootScope, $controller, $q, _UserService_, _$location_) {
    scope = $rootScope.$new();
    UserService = _UserService_;
    deferred = $q.defer();
    $location = _$location_;
    createController = function () {
      return $controller('LoginCtrl as vm', {
        $scope: scope
      })
    }
  }));

  it('should exist', function() {

    var controller = createController();

    expect(controller).toBeDefined();
  });

  it('should have a message property', function () {
    createController();
    expect(scope.vm.message).toBeDefined();
  });

  it('should have a user property', function(){
    createController();
    expect(scope.vm.user).toBeDefined();
    expect(scope.vm.user instanceof Object).toBe(true);
  });


  describe('when user logs in', function () {

    beforeEach(function () {
      createController();

      spyOn(UserService, 'check').and.callFake(function () {
        return deferred.promise;
      });

      spyOn($location, 'path');
    });


    it('should call the check method on UserService', function () {

      scope.vm.login('johndoe', 'aaa');

      deferred.resolve();

      scope.$digest();

      // test si la méthode a été appelée avec les bons arguments
      expect(UserService.check).toHaveBeenCalledWith('johndoe', 'aaa');

      // test si l'utilisateur est redirigé vers le dashboard
      expect($location.path).toHaveBeenCalledWith('/dashboard');
    });

    it('should have an error message if authentification failed', function () {
      var fakeResponseError = {
        status: 401
      };

      scope.vm.login('wrongUser', 'badPassword');

      deferred.reject(fakeResponseError);

      scope.$digest();

      // test qu'il y a un message d'erreur en cas d'échec
      expect(scope.vm.message).not.toBeNull();
    });
  });

  describe('when user logs out', function () {

    beforeEach(function () {
      createController();

      spyOn(UserService, 'logout').and.callFake(function () {
        return deferred.promise;
      });

      spyOn($location, 'path');
    });

    it('should call the logout method on UserService', function () {

      scope.vm.logout();

      deferred.resolve();

      scope.$digest();

      // test si la méthode logout est bien appelée sur le service
      expect(UserService.logout).toHaveBeenCalled();

      // test si l'utilisateur est redirigé vers le login
      expect($location.path).toHaveBeenCalledWith('/login');
    });
  });

  describe('user is logged', function () {
    beforeEach(function () {
      createController();

      spyOn(UserService, 'isLogged').and.returnValue(true);
    });

    it('should call isLogged method on Userservice', function () {
      scope.vm.isLogged();

      expect(UserService.isLogged).toHaveBeenCalled();
    });

    it('should return a boolean', function () {

      var result = scope.vm.isLogged();

      expect(typeof result).toBe('boolean');
    })
  })
});
