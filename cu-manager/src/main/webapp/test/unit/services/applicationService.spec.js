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

describe('Unit Test: Application Service', function(){
  var ApplicationService, httpBackend;
  beforeEach(
    module('webuiApp')
  );

  beforeEach(inject(function ($httpBackend, _ApplicationService_) {

    ApplicationService = _ApplicationService_;
    httpBackend = $httpBackend;
  }));

  afterEach(function () {
    httpBackend.verifyNoOutstandingExpectation();
    httpBackend.verifyNoOutstandingRequest();
    httpBackend.resetExpectations();
  });

  it('should be defined', function(){
    expect(ApplicationService).toBeDefined();
    expect(typeof ApplicationService).toBe('object');
  });

  it('should have a list method', function(){
    expect(ApplicationService.list).toBeDefined();
    expect(typeof ApplicationService.list).toBe('function');
  });

  it('should have a create method', function(){
    expect(ApplicationService.create).toBeDefined();
    expect(typeof ApplicationService.create).toBe('function');
  });

  it('should have a start method', function(){
    expect(ApplicationService.start).toBeDefined();
    expect(typeof ApplicationService.start).toBe('function');
  });

  it('should have a stop method', function(){
    expect(ApplicationService.stop).toBeDefined();
    expect(typeof ApplicationService.stop).toBe('function');
  });

  it('should have a isValid method', function(){
    expect(ApplicationService.isValid).toBeDefined();
    expect(typeof ApplicationService.isValid).toBe('function');
  });

  it('should have a remove method', function(){
    expect(ApplicationService.remove).toBeDefined();
    expect(typeof ApplicationService.remove).toBe('function');
  });

  it('should have a findByName method', function(){
    expect(ApplicationService.findByName).toBeDefined();
    expect(typeof ApplicationService.findByName).toBe('function');
  });

  it('should have a listContainers method', function(){
    expect(ApplicationService.listContainers).toBeDefined();
    expect(typeof ApplicationService.listContainers).toBe('function');
  });

  it('should have a createAlias method', function(){
    expect(ApplicationService.createAlias).toBeDefined();
    expect(typeof ApplicationService.createAlias).toBe('function');
  });

  it('should have a removeAlias method', function(){
    expect(ApplicationService.removeAlias).toBeDefined();
    expect(typeof ApplicationService.removeAlias).toBe('function');
  });

  //tester les méthodes

});
