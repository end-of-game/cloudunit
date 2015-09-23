(function () {
  'use strict';

  angular
    .module ( 'webuiApp.editApplication' )
    .controller ( 'ExplorerCtrl', ExplorerCtrl );

  ExplorerCtrl.$inject = [
    '$stateParams',
    'ApplicationService',
    'ExplorerService',
    '$q',
    '$timeout',
    'FileUploader',
    '$scope'
  ];


  function ExplorerCtrl ( $stateParams, ApplicationService, ExplorerService, $q, $timeout, FileUploader, $scope ) {
    // ------------------------------------------------------------------------
    // GESTION DES LOGS
    // ------------------------------------------------------------------------
    var vm = this,
      rootPath = '__',
      uploader;

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
    vm.downloadFile = downloadFile;
    vm.deleteFile = deleteFile;
    vm.refresh = refresh;

    init ();

    function init ( containerIndex ) {
      // affichage de la liste de container
      getContainers ().then ( function onSuccess ( data ) {
        var index = containerIndex || 0;
        vm.containers = data;
        vm.myContainer = data[index];

        // construction de l'arbre
        // 1er niveau
        buildTree ( rootPath, 'rootFolder' )
          .then ( function ( dirs ) {
          // 2eme niveau
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

    // méthode pour redessiner l'arbre lorsqu'on change de container
    function refresh ( index ) {
      vm.rootFolder = [];
      vm.subFolder = [];
      vm.currentPath = [];
      init ( index );
    }

    function deleteFile ( containerId, path, item ) {

      var slug = '__' + path.join ( '__' ) + '__' + item.name;

      ExplorerService.deleteFile ( containerId, $stateParams.name, slug )
        .then ( function onFileDelete () {
        $timeout(function(){
          buildTree ( vm.currentPath.join ( '__' ), 'subFolder' );
        }, 1000);
      } )
        .catch ( function onFileDeleteError ( error ) {
        console.log ( 'Cannot delete file : ' + error )
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
        slug = rootPath + path.replace ( ' ', '__' ) + '__';
      }

      ExplorerService.buildTree ( vm.myContainer.id, slug ).then ( function onSuccess ( data ) {
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
        buildTree ( vm.currentPath.join ( '__' ), 'subFolder' );
      }
    }

    function upDir ( index ) {
      if ( vm.currentPath[0] ) {
        vm.currentPath = vm.currentPath.slice ( 0, index + 1 );
        buildTree ( vm.currentPath.join ( '__' ), 'subFolder' );
      }
    }


    // file upload
    uploader = $scope.uploader = new FileUploader ( {
      autoUpload: false,
      removeAfterUpload: true,
      queueLimit: 1
    } );


    uploader.onAfterAddingFile = function ( fileItem ) {
      vm.dropped = false;
      fileItem.url = '/file/container/' + vm.myContainer.id + '/application/' + $stateParams.name + '/path/__' + vm.currentPath.join ( '__' ) + '__';
      uploader.uploadAll ();
      vm.isUploading = true;
      vm.dropped = true;
      vm.errors.fileTypeError = false;
    };

    uploader.onCompleteAll = function () {
      vm.dropped = false;
      vm.uploadSuccess = true;
      vm.isUploading = false;
      buildTree ( vm.currentPath.join ( '__' ), 'subFolder' ).then ( function () {
        vm.uploadSuccess = false;
      } );
    };


    function downloadFile ( containerId, path, file ) {
      if ( file.dir ) {
        return;
      }
      ExplorerService.downloadFile ( containerId, $stateParams.name, '__' + vm.currentPath.join ( '__' ), file.name ).then ( function ( result ) {
        var blob = new Blob ( [result.data], { type: result.headers['content-type'] } );
        saveAs ( blob, file.name );
      } )
    }
  }
}) ();

