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
