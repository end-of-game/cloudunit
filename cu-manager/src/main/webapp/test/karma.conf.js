module.exports = function (config) {
  config.set({
    basePath: '../',
    files: [
      'bower_components/angular/angular.js',
      'bower_components/angular-mocks/angular-mocks.js',
      'bower_components/angular-gravatar/build/angular-gravatar.js',
      'bower_components/angular-resource/angular-resource.js',
      'bower_components/angular-cookies/angular-cookies.js',
      'bower_components/angular-sanitize/angular-sanitize.js',
      'bower_components/angular-animate/angular-animate.js',
      'bower_components/angular-route/angular-route.js',
      'bower_components/angular-ui-router/release/angular-ui-router.js',
      'bower_components/angular-file-upload/angular-file-upload.js',
      'bower_components/ng-table/ng-table.js',
      'bower_components/angular-gravatar/build/md5.js',
      'bower_components/angular-gravatar/build/angular-gravatar.js',

      'app/scripts/**/*.js',
      'test/unit/**/*.spec.js'
    ],
    background: true,
    singleRun: false,
    frameworks: ['jasmine'],
    browsers: ['Chrome', 'PhantomJS', 'Firefox'],
    plugins: [
      'karma-junit-reporter',
      'karma-chrome-launcher',
      'karma-firefox-launcher',
      'karma-phantomjs-launcher',
      'karma-jasmine'
    ]
  })
};
