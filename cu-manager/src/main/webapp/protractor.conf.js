exports.config = {
		
  seleniumAddress: 'http://localhost:4444/wd/hub',
  
  allScriptsTimeout: 11000,

  specs: [
    'test/e2e/*.js'
  ],

  capabilities: {
    'browserName': 'chrome'
  },

  baseUrl: 'http://localhost:9001/',

  framework: 'jasmine',

  jasmineNodeOpts: {
    defaultTimeoutInterval: 30000
  }
};

