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

'use strict';

describe('Unit Test: UserService', function () {
  var UserService, httpBackend, checkRequest, credentials, data, cookieStore;

  beforeEach(module('webuiApp'));

  beforeEach(inject(function ($httpBackend, _UserService_, _$cookieStore_) {

    UserService = _UserService_;
    httpBackend = $httpBackend;
    cookieStore = _$cookieStore_;

  }));

  afterEach(function () {
    httpBackend.verifyNoOutstandingExpectation();
    httpBackend.verifyNoOutstandingRequest();
    httpBackend.resetExpectations();
  });

  it('should exist', function(){
    expect(UserService).toBeDefined();
    expect(typeof UserService).toBe('object');
  });

  describe('when check method is called', function () {
    var checkRequest;

    beforeEach(function () {
      checkRequest = httpBackend.whenPOST('/user/authentication').respond(200, '');
    });

    it('should call the API', function () {
      httpBackend.expectPOST('/user/authentication');
      UserService.check('johndoe', 'aaa');
      httpBackend.flush();
    });

    it('should fail authentication', function () {
      var result, promise;

      checkRequest.respond(401, '');

      promise = UserService.check('johndo', 'bbb');

      promise.then(function (response) {
        result = response;
      }, function (error) {
        result = error;
      });

      httpBackend.flush();

      expect(result.status).toBe(401);
    });

    it('should send credentials to server', function () {

      credentials = 'j_username=johndoe&j_password=aaa&submit=Login';

      httpBackend.expectPOST('/user/authentication', credentials).respond(200, '');

      UserService.check('johndoe', 'aaa');

      httpBackend.flush();

    });

    it('should send correct headers', function () {

      httpBackend.expectPOST('/user/authentication', undefined, function(headers){
        return headers['Content-Type'] == 'application/x-www-form-urlencoded' && headers['Accept'] == 'application/json, text/plain, */*';
      }).respond(200, '');

      UserService.check('johndoe', 'aaa');

      httpBackend.flush();

    });

  });

  describe('when profile method is called', function () {

    it('should return user data', function () {
      var result, promise, respondData;

      respondData = {
        "id": 1,
        "login": "johndoe",
        "firstName": "John",
        "lastName": "Doe",
        "organization": "anonymous",
        "signin": "2013-08-22 09:22",
        "lastConnection": "2014-11-18 16:58",
        "email": "j.doe@treeptik.fr",
        "status": 1,
        "role": {
          "id": 1,
          "description": "ROLE_ADMIN"
        }
      };

      httpBackend.expectGET('/user/status').respond(200, respondData);

      promise = UserService.profile();

      promise.then(function (response) {
        result = response;
      });

      httpBackend.flush();

      expect(result.data).toEqual(respondData);
    });
  });

  describe('when logout method is called', function(){

    var logoutRequest;

    beforeEach(function(){
      logoutRequest = httpBackend.whenGET('/user/logout').respond();

      spyOn(UserService, 'deleteLocalSession').and.callThrough();
    });

    it('should call the API', function(){

      UserService.logout();
      httpBackend.flush();
    });

    it('should call deleteLocalSession function', function(){
      var promise = UserService.logout();

      promise.then(function () {
        UserService.deleteLocalSession();
      });

      httpBackend.flush();

      expect(UserService.deleteLocalSession).toHaveBeenCalled();
    });

  });

  describe('when createLocalSession method is called', function(){

    it('should call put method on $cookieStore module', function(){

      spyOn(cookieStore, 'put').and.callThrough();

      UserService.createLocalSession();

      expect(cookieStore.put).toHaveBeenCalledWith('isLogged', 'true');
    });
  });

  describe('when deleteLocalSession method is called', function(){

    it('should call remove method on $cookieStore module', function(){

      spyOn(cookieStore, 'remove').and.callThrough();

      UserService.deleteLocalSession();

      expect(cookieStore.remove).toHaveBeenCalledWith('isLogged');
    });
  });

  describe('when isLogged method is called', function(){

    it('should call get method on $cookieStore module', function(){

      spyOn(cookieStore, 'get').and.callThrough();

      var result = UserService.isLogged();

      expect(cookieStore.get).toHaveBeenCalledWith('isLogged');
      expect(typeof result).toBe('boolean');
    });
  });
});
