var waitForPromise = (function () {
  function waitForPromise(promiseFn, testFn) {
    browser.wait(function () {
      var deferred = protractor.promise.defer();
      promiseFn().then(function (data) {
        deferred.fulfill(testFn(data));
      });
      return deferred.promise;
    });
  }
  return waitForPromise;
})();

module.exports = waitForPromise;

