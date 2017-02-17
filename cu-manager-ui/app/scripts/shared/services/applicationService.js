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

/**
* @ngdoc service
* @name webuiApp.ApplicationService
* @description
* # ApplicationService
* Factory in the webuiApp.
*/
angular
.module ( 'webuiApp' )
.factory ( 'ApplicationService', ApplicationService );

ApplicationService.$inject = [
'$resource',
'$http',
'$interval',
'$q',
'traverson'
];


function ApplicationService ( $resource, $http, $interval, $q, traverson ) {
    var Application;

    Application = $resource ( 'application/:id', { id: '@name' } );

    traverson.registerMediaType(TraversonJsonHalAdapter.mediaType, TraversonJsonHalAdapter);

    var traversonService = traverson
        .from('/applications')
        .jsonHal()
        .withRequestOptions({ headers: { 'Content-Type': 'application/hal+json'} });

    return {
        about: about,
        list: list,
        create: create,
        start: start,
        stop: stop,
        isValid: isValid,
        remove: remove,
        findByName: findByName,
        listContainers: listContainers,
        createAlias: createAlias,
        removeAlias: removeAlias,
        createPort: createPort,
        removePort: removePort,
        openPort: openPort,
        restart: restart,
        init: init,
        state: {},
        stopPolling: stopPolling
    };


///////////////////////////////////////////////////////

function assignObject(target) {
    target = Object(target);
    for (var index = 1; index < arguments.length; index++) {
        var source = arguments[index];
        if (source != null) {
            for (var key in source) {
                if (Object.prototype.hasOwnProperty.call(source, key)) {
                    target[key] = source[key];
                }
            }
        }
    }
    return target;
};

// A propos du manager
function about () {
    return $http.get ( 'about' ).then ( function ( response ) {
        return angular.copy ( response.data );
    } )
}

// Liste des applications
function list () {
    return traversonService
        .newRequest()
        .getResource()
        .result
        .then(function(res) {
            if(res._embedded) {
                return res._embedded.applicationResourceList;
            } else {
                return [];
            }
        })
}

// Creation d'une application
function create ( applicationName, serverName ) {
    var payload = {
        name: applicationName,
        serverType: serverName 
    };

    return traversonService
        .post(payload)
        .result;
}

// Démarrer une application
function start ( applicationName ) {
     traversonService
        .newRequest()
        .follow('applicationResourceList[name:' + applicationName + ']', 'start')
        .post()
        .result;
}

// Démarrer une application
function restart ( applicationName ) {
     traversonService
        .newRequest()
        .follow('applicationResourceList[name:' + applicationName + ']', 'restart')
        .post()
        .result;
}

// Arrêter une application
function stop ( applicationName ) {
     traversonService
        .newRequest()
        .follow('applicationResourceList[name:' + applicationName + ']', 'stop')
        .post()
        .result;
}

// Teste la validite d'une application avant qu'on puisse la creer
function isValid ( applicationName, serverName ) {
    // var validity = $resource ( 'application/verify/' + applicationName + '/' + serverName );
    // return validity.get ().$promise;
    return traversonService

        .withTemplateParameters({name: applicationName})
        .newRequest()
        .getResource()
        .result
        .then(function(res) {
            console.log('isValid', res)
            if(res._embedded) {
                return res._embedded.applicationResourceList;
            } else {
                return [];
            }
        })
}


// Suppression d'une application
function remove ( applicationName ) {
    traversonService
        .newRequest()
        .follow('applicationResourceList[name:' + applicationName + ']', 'delete')
        .delete();
}

// Récupération d'une application selon son nom
function findByName ( applicationName ) {
    var self = this;
    return $http.get ( 'application/' + applicationName ).then ( function ( response ) {
        return assignObject(self.state, response.data);
    } ).catch ( function () {
        stopPolling.call ( self );
    } )
}

function init ( applicationName ) {
    var self = this;
    if ( !self.timer ) {
        self.timer = pollApp.call ( self, applicationName );
    }
    return findByName.call ( self, applicationName ).then ( function ( response ) {
        self.state = response;
    } );
}

function pollApp ( applicationName ) {
    var self = this;
    return $interval ( function () {
        findByName.call ( self, applicationName ).then ( function ( response ) {
            return self.state = response;
        } );
    }, 2000 )
}

function stopPolling () {
    if ( this.timer ) {
        $interval.cancel ( this.timer );
        this.timer = null;
    }
}

// Liste de toutes les container d'une application en fonction du type server/module
function listContainers ( applicationName ) {
    var container = $resource ( 'application/:applicationName/containers' );
    return container.query ( { applicationName: applicationName } ).$promise;
}

// Gestion des alias

function createAlias ( applicationName, alias ) {
    var data = {
        applicationName: applicationName,
        alias: alias
    };
    return $http.post ( 'application/alias', data );
}

function removeAlias ( applicationName, alias ) {
    return $http.delete ( 'application/' + applicationName + '/alias/' + alias );
}


// Gestion des ports

function createPort ( applicationName, number, nature, isQuickAccess ) {
    var data = {
        applicationName: applicationName,
        portToOpen: number,
        portNature: nature,
        portQuickAccess: isQuickAccess
    };
    return $http.post ( 'application/ports', data );
}

function removePort ( applicationName, number ) {
    return $http.delete ( 'application/' + applicationName + '/ports/' + number );
}

 function openPort(moduleID, statePort, portInContainer) {
    var data = {
        publishPort: statePort
    };

    var dir = $resource ( '/module/:moduleID/ports/:portInContainer' ,
    { 
        moduleID: moduleID,
        portInContainer: portInContainer
    },
    { 
        'update': { 
            method: 'PUT',
            transformResponse: function ( data, headers ) {
                var response = {};
                response = JSON.parse(data);
                return response;
            }
        }
    }
    );
    return dir.update( { }, data ).$promise;
}

}
}) ();
