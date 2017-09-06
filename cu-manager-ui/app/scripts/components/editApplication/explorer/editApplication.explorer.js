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

(function () {
  'use strict';

  angular
    .module ( 'webuiApp.editApplication' )
    .component ( 'editAppExplorer', Explorer() );

  function Explorer () {
    return {
      templateUrl: 'scripts/components/editApplication/explorer/editApplication.explorer.html',
      bindings: {
        app: '='
      },
      controller: [
        '$stateParams',
        'ApplicationService',
        'ExplorerService',
        '$q',
        '$timeout',
        'FileUploader',
        '$scope',
        ExplorerCtrl
      ],
      controllerAs: 'explorer',
    };
  }


  function ExplorerCtrl ( $stateParams, ApplicationService, ExplorerService, $q, $timeout, FileUploader, $scope ) {
    // ------------------------------------------------------------------------
    // LOGS MANAGEMENT
    // ------------------------------------------------------------------------
    var vm = this,
      rootPath = '/',
      uploader;

    vm.editorOptions = {
      lineWrapping : true,
      lineNumbers: true,
      mode: 'properties',
    };

    vm.newDirectoryName = '';
    vm.containers = [];
    vm.myContainer = {};
    vm.rootFolder = [];
    vm.subFolder = [];
    vm.currentPath = [];
    vm.buildTree = buildTree;
    vm.getContainers = getContainers;
    vm.errors = {
      fileTypeError: false
    };
    vm.upDir = upDir;
    vm.folderClick = folderClick;
    vm.deleteFile = deleteFile;
    vm.unzipFile = unzipFile;
    vm.editFile = editFile;
    vm.downloadCompressedFolder = downloadCompressedFolder;
    vm.fileContent = "";
    vm.getFile = getFile;
    vm.addNewDirectory = addNewDirectory;
    vm.refresh = refresh;

    vm.$onInit = function() {
      displayContainersAndSelectCurrent();
    }

    function displayContainersAndSelectCurrent ( containerIndex ) {
      // Display the list of containers
      getContainers ().then ( function onSuccess ( data ) {
        var index = containerIndex || 0;
        vm.containers = data;
        vm.myContainer = data[index];

        // Construction of tree
        // First level
        buildTree ( rootPath, 'rootFolder' )
          .then ( function ( dirs ) {
            // Second level
            buildTree ( dirs[0].name, 'subFolder' );
            vm.currentPath.push ( dirs[0].name );
          } )
          .catch ( function onGetSubDirError ( reason ) {
            console.error ( reason );
          } );

      } ).catch ( function onGetRootDirError ( reason ) {
        console.error ( reason );
      } );
    }

    // Method to redraw the tree if we change container origin
    function refresh ( index ) {
      vm.rootFolder = [];
      vm.subFolder = [];
      vm.currentPath = [];
      displayContainersAndSelectCurrent ( index );
    }

    function addNewDirectory(containerId, path, newDirectoryName) {
      var slug = '/' + path.join ( '/' ) + '/' + newDirectoryName;

       ExplorerService.addDirectory ( containerId, $stateParams.name, slug )
        .then ( function onDirectoryAdd () {
          vm.isCreatingDirectory = true;
          vm.newDirectoryName = "";
          $timeout ( function () {
            buildTree ( vm.currentPath.join ( '/' ), 'subFolder' ).then ( function () {
              vm.isCreatingDirectory = false;
            } );
          }, 1000 );
        } )
        .catch ( function onDirectoryAddError ( error ) {
          $timeout ( function () {
            buildTree ( vm.currentPath.join ( '/' ), 'subFolder' );
          }, 1000 );
        } )
    }

    function deleteFile ( containerId, path, item ) {

      var slug = '/' + path.join ( '/' ) + '/' + item.name;
      ExplorerService.deleteFile ( containerId, $stateParams.name, slug )
        .then ( function onFileDelete () {
          $timeout ( function () {
            buildTree ( vm.currentPath.join ( '/' ), 'subFolder' );
          }, 1000 );
        } )
        .catch ( function onFileDeleteError ( error ) {
          $timeout ( function () {
            buildTree ( vm.currentPath.join ( '/' ), 'subFolder' );
          }, 1000 );
        } )
    }

    function unzipFile ( containerId, path, item ) {
      var slug = '/' + path.join ( '/' );

      ExplorerService.unzipFile ( containerId, $stateParams.name, slug, item.name )
        .then ( function onFileUnzip (data) {
          $timeout ( function () {
            buildTree ( vm.currentPath.join ( '/' ), 'subFolder' );
          }, 1000 );
        } )
    }

      function downloadCompressedFolder ( containerId, path, item ) {
          var slug = '/' + path.join ( '/' );
          ExplorerService.zipFile ( containerId, $stateParams.name, slug, item.name )
              .then (function onFolderDownload (data) {
                  $timeout ( function () {
                      downloadFile(containerId, path, item.name+".tar");
                  }, 1000 );
              }).then(function onFolderDownload(data) {
                $timeout(function() {
                  item.name = item.name+".tar";
                  deleteFile(containerId, path, item);
                }, 3000);
          })

      }


    function getFile ( containerId, path, item ) {
      var slug = '/' + path.join ( '/' );

      ExplorerService.getFile ( containerId, $stateParams.name, slug, item.name )
        .then ( function onFileUnzip (res) {
          vm.fileContent = res.data;
        } )
    }

      function downloadFile ( containerId, path, itemName ) {
          var url = '/file/container/'+containerId+'/application/'+$stateParams.name+'?path='+path.join('/')+'&fileName='+itemName;
          window.location.assign(url);
      }


    function editFile ( containerId, path, item, fileContent) {
      var slug = '/' + path.join ( '/' );
      ExplorerService.editFile ( containerId, $stateParams.name, slug, item.name, fileContent )
        .then ( function onFileEdit (res) {
          vm.fileContent = res.data;
        } )
    }

    function getContainers () {
      var deferred = $q.defer ();
      ApplicationService.listContainers ( $stateParams.name )
        .then ( function onSuccess ( containers ) {
          deferred.resolve ( containers );
        } )
        .catch ( function onError ( reason ) {
          deferred.reject ( reason );
        } );
      return deferred.promise;
    }

    function buildTree ( path, level ) {
      var deferred = $q.defer (),
        slug;

      if ( path == rootPath ) {
        slug = path;
      } else {
        slug = rootPath + path.replace ( ' ', '/' ) + '/';
      }

      ExplorerService.buildTree ( vm.myContainer.name, slug ).then ( function onSuccess ( data ) {
          deferred.resolve ( data );
          return vm[level] = data;
        } )
        .catch ( function onError ( reason ) {
          deferred.reject ( reason );
        } );
      return deferred.promise;
    }

    // file navigator
    function folderClick ( item, isRoot ) {
      if ( isRoot ) {
        vm.currentPath = [];
      }
      if ( item && item.dir ) {
        vm.currentPath.push ( item.name );
        buildTree ( vm.currentPath.join ( '/' ), 'subFolder' );
      }
    }

    function upDir ( index ) {
      if ( vm.currentPath[0] ) {
        vm.currentPath = vm.currentPath.slice ( 0, index + 1 );
        buildTree ( vm.currentPath.join ( '/' ), 'subFolder' );
      }
    }


    // file upload
    uploader = $scope.uploader = new FileUploader ( {
      autoUpload: false,
      removeAfterUpload: true
    } );


    uploader.onAfterAddingAll = function ( fileItem ) {
      vm.dropped = false;
      fileItem.forEach(function(element, index) {
        fileItem[index].url = fileItem.url = '/file/container/' + vm.myContainer.name + '/application/' + $stateParams.name + '?path=/' + vm.currentPath.join ( '/' ) + '/';
      });

      uploader.uploadAll ();
      vm.isUploading = true;
      vm.dropped = true;
      vm.errors.fileTypeError = false;
    };

    uploader.onCompleteAll = function () {
      vm.dropped = false;
      vm.uploadSuccess = true;
      buildTree ( vm.currentPath.join ( '/' ), 'subFolder' ).then ( function () {
        vm.isUploading = false;
        vm.uploadSuccess = false;
      } );
    };

  }
}) ();
