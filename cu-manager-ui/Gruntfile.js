// Generated on 2014-07-15 using generator-angular 0.9.5
'use strict';

// # Globbing
// for performance reasons we're only matching one level down:
// 'test/spec/{,*/}*.js'
// use this if you want to recursively match all subfolders:
// 'test/spec/**/*.js'

var proxySnippet = require('grunt-connect-proxy/lib/utils').proxyRequest;

module.exports = function (grunt) {

  // Load grunt tasks automatically
  require('load-grunt-tasks')(grunt);

  // Time how long tasks take. Can help when optimizing build times
  require('time-grunt')(grunt);

  // Configurable paths for the application
  var appConfig = {
    app: require('./bower.json').appPath || 'app',
    dist: 'dist'
  };

  // Define the configuration for all the tasks
  grunt.initConfig({

    // Project settings
    yeoman: appConfig,

    // Watches files for changes and runs tasks based on the changed files
    watch: {
      bower: {
        files: ['bower.json'],
        tasks: ['wiredep']
      },
      js: {
        files: ['<%= yeoman.app %>/scripts/{,*/}*.js'],
        tasks: ['newer:jshint:all'],
        options: {
          livereload: '<%= connect.options.livereload %>'
        }
      },
      jsTest: {
        files: ['test/unit/{,*/}*.js'],
        tasks: ['newer:jshint:test', 'karma']
      },
      /*compass: {
        files: ['<%= yeoman.app %>/styles/{,*!/}*.{scss,sass}'],
        tasks: ['compass:server', 'autoprefixer']
      },*/
      sass: {
        files: ['<%= yeoman.app %>/styles/{,*/}*.{scss,sass}'],
        tasks: ['sass', 'autoprefixer']
      },
      gruntfile: {
        files: ['Gruntfile.js']
      },
      livereload: {
        options: {
          livereload: '<%= connect.options.livereload %>'
        },
        files: [
          '<%= yeoman.app %>/{,*/}*.html',
          '.tmp/styles/{,*/}*.css',
          '<%= yeoman.app %>/images/{,*/}*.{png,jpg,jpeg,gif,webp,svg}'
        ]
      },
      karma: {
        files: ['scripts/**/*.js', 'test/unit/**/*.js'],
        tasks: ['karma:unit:run']
      }/*,
       e2eTest: {
       files: ['spec/{,*//*}*.js',
       '<%= yeoman.app %>/scripts/{,*//*}*.js',
       '<%= yeoman.app %>/{,*//*}*.html',
       '.tmp/styles/{,*//*}*.css',
       '<%= yeoman.app %>/images/{,*//*}*.{png,jpg,jpeg,gif,webp,svg}'],
       tasks: ['test']
       }*/
    },

    karma: {
      unit: {
        configFile: 'test/karma.conf.js'
      }
    },

    sass: {
      options: {
        sourceMap: true,
        relativeAssets: false,
        outputStyle: 'expanded',
        sassDir: '<%= yeoman.app %>/styles',
        cssDir: '.tmp/styles'
      },
      build: {
        files: [{
          expand: true,
          cwd: '<%= yeoman.app %>/styles',
          src: ['main.scss'],
          dest: '.tmp/styles',
          ext: '.css'
        }]
      }
    },

    // The actual grunt server settings
    connect: {
      proxies: [
        {
          context: '/about',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        },
        {
            context: '/homepage',
            host: 'localhost',
            port: 8080,
            https: false,
            changeOrigin: false
        },
        {
          context: '/applications',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        },
        {
          context: '/messages',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        },
        {
          context: '/user',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        },
        {
          context: '/admin',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        },
        {
          context: '/scripting',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        },
        {
          context: '/module',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        },
        {
          context: '/server',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        },
        {
          context: '/logs',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        },
        {
          context: '/monitoring',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        },
        {
          context: '/snapshot',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        },
        {
          context: '/file',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        },
        {
          context: '/scripting',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        },
        {
          context: '/volume',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        },
        {
          context: '/resources',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        },
        {
          context: '/registry',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        },
        {
          context: '/images',
          host: 'localhost',
          port: 8081,
          https: false,
          changeOrigin: false
        },
        {
          context: '/containers',
          host: 'localhost',
          port: 8081,
          https: false,
          changeOrigin: false
        },
        {
          context: '/image',
          host: 'localhost',
          port: 8080,
          https: false,
          changeOrigin: false
        }
      ],
      options: {
        port: 9000,
        // Change this to '0.0.0.0' to access the server from outside.
        hostname: '0.0.0.0',
        livereload: 35728
      },
      livereload: {
        options: {
          open: true,
          middleware: function (connect) {
            return [
              proxySnippet,
              connect.static('.tmp'),
              connect().use(
                '/bower_components',
                connect.static('./bower_components')
              ),
              connect.static(appConfig.app)
            ];
          }
        }
      },
      test: {
        options: {
          port: 9001,
          middleware: function (connect) {
            return [
              connect.static('.tmp'),
              connect.static('test'),
              connect().use(
                '/bower_components',
                connect.static('./bower_components')
              ),
              connect.static(appConfig.app)
            ];
          }
        }
      },
      dist: {
        options: {
          open: true,
          base: '<%= yeoman.dist %>'
        }
      }
    },

    // Make sure code styles are up to par and there are no obvious mistakes
    jshint: {
      options: {
        jshintrc: '.jshintrc',
        reporter: require('jshint-stylish')
      },
      all: {
        src: [
          'Gruntfile.js',
          '<%= yeoman.app %>/scripts/{,*/}*.js'
        ]
      },
      test: {
        options: {
          jshintrc: 'test/.jshintrc'
        },
        src: ['test/spec/{,*/}*.js']
      }
    },

    // Empties folders to start fresh
    clean: {
      dist: {
        files: [
          {
            dot: true,
            src: [
              '.tmp',
              '<%= yeoman.dist %>/{,*/}*',
              '!<%= yeoman.dist %>/.git*'
            ]
          }
        ]
      },
      server: '.tmp'
    },

    // Add vendor prefixed styles
    autoprefixer: {
      options: {
        browsers: ['last 1 version']
      },
      dist: {
        files: [
          {
            expand: true,
            cwd: '.tmp/styles/',
            src: '{,*/}*.css',
            dest: '.tmp/styles/'
          }
        ]
      }
    },

    // Automatically inject Bower components into the app
    wiredep: {
      options: {
        cwd: '<%= yeoman.app %>'
      },
      app: {
        src: ['<%= yeoman.app %>/index.html'],
        ignorePath: /\.\.\//
      },
      sass: {
        src: ['<%= yeoman.app %>/styles/{,*/}*.{scss,sass}'],
        ignorePath: /(\.\.\/){1,2}bower_components\//
      }
    },

    // Compiles Sass to CSS and generates necessary files if requested
    compass: {
      options: {
        sassDir: '<%= yeoman.app %>/styles',
        cssDir: '.tmp/styles',
        generatedImagesDir: '.tmp/images/generated',
        imagesDir: '<%= yeoman.app %>/images',
        javascriptsDir: '<%= yeoman.app %>/scripts',
        fontsDir: '<%= yeoman.app %>/styles/fonts',
        importPath: './bower_components',
        httpImagesPath: '/styles/images',
        httpGeneratedImagesPath: '/images/generated',
        httpFontsPath: '/styles/fonts',
        relativeAssets: false,
        assetCacheBuster: false,
        raw: 'Sass::Script::Number.precision = 10\n'
      },
      dist: {
        options: {
          generatedImagesDir: '<%= yeoman.dist %>/images/generated'
        }
      },
      server: {
        options: {
          debugInfo: true
        }
      }
    },

    // Renames files for browser caching purposes
    filerev: {
      dist: {
        src: [
          '<%= yeoman.dist %>/scripts/{,*/}*.js',
          '<%= yeoman.dist %>/styles/{,*/}*.css'
        ]
      }
    },

    // Reads HTML for usemin blocks to enable smart builds that automatically
    // concat, minify and revision files. Creates configurations in memory so
    // additional tasks can operate on them
    useminPrepare: {
      html: '<%= yeoman.app %>/index.html',
      options: {
        dest: '<%= yeoman.dist %>',
        flow: {
          html: {
            steps: {
              js: ['concat', 'uglifyjs'],
              css: ['cssmin']
            },
            post: {}
          }
        }
      }
    },

    // Performs rewrites based on filerev and the useminPrepare configuration
    usemin: {
      html: ['<%= yeoman.dist %>/{,}*.html'],
      css: ['<%= yeoman.dist %>/styles/{,}*.css'],
      options: {
        assetsDirs: ['<%= yeoman.dist %>', '<%= yeoman.dist %>/images']
      }
    },

    // The following *-min tasks will produce minified files in the dist folder
    // By default, your `index.html`'s <!-- Usemin block --> will take care of
    // minification. These next options are pre-configured if you do not wish
    // to use the Usemin blocks.
    cssmin: {
      dist: {
        files: {
          '<%= yeoman.dist %>/styles/main.css': [
            '.tmp/styles/{,*/}*.css'
          ]
        }
      }
    },
    uglify: {
      dist: {
        files: {
          '<%= yeoman.dist %>/scripts/scripts.js': [
            '<%= yeoman.dist %>/scripts/scripts.js'
          ]
        }
      }
    },
    concat: {
      dist: {}
    },

    imagemin: {
      dist: {
        files: [
          {
            expand: true,
            cwd: '<%= yeoman.app %>/images',
            src: '{,*/}*.{png,jpg,jpeg,gif}',
            dest: '<%= yeoman.dist %>/images'
          }
        ]
      }
    },

    svgmin: {
      dist: {
        files: [
          {
            expand: true,
            cwd: '<%= yeoman.app %>/images',
            src: '{,*/}*.svg',
            dest: '<%= yeoman.dist %>/images'
          }
        ]
      }
    },

    htmlmin: {
      dist: {
        options: {
          collapseWhitespace: true,
          conservativeCollapse: true,
          collapseBooleanAttributes: true,
          removeCommentsFromCDATA: true,
          removeOptionalTags: true
        },
        files: [
          {
            expand: true,
            cwd: '<%= yeoman.dist %>',
            src: ['*.html', 'scripts/{,*/}*.html'],
            dest: '<%= yeoman.dist %>'
          }
        ]
      }
    },

    // ngmin tries to make the code safe for minification automatically by
    // using the Angular long form for dependency injection. It doesn't work on
    // things like resolve or inject so those have to be done manually.
    ngmin: {
      dist: {
        files: [
          {
            expand: true,
            cwd: '.tmp/concat/scripts',
            src: '*.js',
            dest: '.tmp/concat/scripts'
          }
        ]
      }
    },

    ngAnnotate: {
      dist: {
        files: [
          {
            expand: true,
            cwd: '.tmp/concat/scripts',
            src: '*.js',
            dest: '.tmp/concat/scripts'
          }
        ]
      }
    },

    // Replace Google CDN references
    cdnify: {
      dist: {
        html: ['<%= yeoman.dist %>/*.html']
      }
    },

    // Copies remaining files to places other tasks can use
    copy: {
      dist: {
        files: [
          {
            expand: true,
            dot: true,
            cwd: '<%= yeoman.app %>',
            dest: '<%= yeoman.dist %>',
            src: [
              '*.{ico,png,txt}',
              '.htaccess',
              '*.html',
              'scripts/**/*.html',
              'images/{,*/}*.{webp}',
              'styles/fonts/*',
              'styles/images/*'
            ]
          },
          {
            expand: true,
            cwd: '.tmp/images',
            dest: '<%= yeoman.dist %>/images',
            src: ['generated/*']
          }
        ]
      },
      styles: {
        expand: true,
        cwd: '<%= yeoman.app %>/styles',
        dest: '.tmp/styles/',
        src: '{,*/}*.css'
      }
    },

    // Run some tasks in parallel to speed up the build process
    concurrent: {
      server: [
        'sass'
      ],
      test: [
        'sass'
      ],
      dist: [
        'sass',
        'imagemin',
        'svgmin'
      ]
    },
    protractor: {
      options: {
        keepAlive: false,
        configFile: 'test/protractor.conf.js'
      },
      run: {}
    },
    run: {
      selenium: {
        options: {
          wait: false
        },
        // cmd: "node" default
        args: [
          'node_modules/protractor/bin/webdriver-manager', 'start'
        ]
      }
    },
    /*jshint camelcase: false */
    /*protractor_webdriver: {
      start: {
        options: {
          command: 'webdriver-manager start'
        }
      }
    }*/
  });


  grunt.registerTask('serve', 'Compile then start a connect web server', function (target) {
    if (target === 'dist') {
      return grunt.task.run(['build', 'connect:dist:keepalive']);
    }

    grunt.task.run([
      'clean:server',
      //'wiredep',
      'concurrent:server',
      'autoprefixer',
      'configureProxies',
      'connect:livereload',
      'watch'
    ]);
  });

  grunt.registerTask('server', 'DEPRECATED TASK. Use the "serve" task instead', function (target) {
    grunt.log.warn('The `server` task has been deprecated. Use `grunt serve` to start a server.');
    grunt.task.run(['serve:' + target]);
  });


  grunt.loadNpmTasks('grunt-protractor-runner');
  grunt.registerTask('test', [
    'clean:server',
    'concurrent:test',
    'autoprefixer',
    'run:selenium',
    //'karma:unit',
    //'protractor_webdriver',
    'protractor:run'
  ]);

  grunt.registerTask('build', [
    'clean:dist',
    //'wiredep',
    'useminPrepare',
    'concurrent:dist',
    'autoprefixer',
    'concat',
    //'ngmin',
    //'ngAnnotate',
    'copy:dist',
    'cdnify',
    'cssmin',
    'uglify',
    'filerev',
    'usemin',
    'htmlmin'
  ]);

  grunt.registerTask('default', [
    'newer:jshint',
    'test',
    'build'
  ]);
};
