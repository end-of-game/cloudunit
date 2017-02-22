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
     return traversonService
        .newRequest()
        .follow('applicationResourceList[name:' + applicationName + ']', 'start')
        .post()
        .result;
}

// Démarrer une application
function restart ( applicationName ) {
     return traversonService
        .newRequest()
        .follow('applicationResourceList[name:' + applicationName + ']', 'restart')
        .post()
        .result;
}

// Arrêter une application
function stop ( applicationName ) {
     return traversonService
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
        .withRequestOptions({
            qs: { name: applicationName }
        })
        .withTemplateParameters({name: applicationName})
        .newRequest()
        .getResource()
        .result
        .then(function(res) {
            if(res._embedded) {
                return res._embedded.applicationResourceList.length;
            }
            return false;
        })
}


// Suppression d'une application
function remove ( applicationName ) {
    traversonService
        .newRequest()
        .follow('applicationResourceList[name:' + applicationName + ']', 'delete')
        .delete();
}


function flatTraverse () {
  var q = $q.defer();
  var promises = [];
  var argumentsFollow = arguments;
  var request = traversonService
    .newRequest()
    .follow([].shift.call(argumentsFollow))
    .getResource();

    return request
    .result
    .then(function(response) {
        angular.forEach(argumentsFollow, function(argumentFollow) {
            var intermediatePromise = $q.defer();
            request.continue().then(function(subRequest) {
                var property = argumentFollow[0];
                subRequest
                .newRequest()
                .follow(argumentFollow)
                .getResource()
                .result
                .then(function(subResponse) {
                    if(Object.keys(subResponse).length <= 1) {
                        subResponse = [];
                    }
                    if(subResponse._embedded) {
                        subResponse = subResponse._embedded[Object.keys(subResponse._embedded)[0]];
                    }
                    Object.defineProperty(response, property, {
                            value: subResponse,
                            writable: true,
                            enumerable: true,
                            configurable: true
                    });
                    intermediatePromise.resolve();
                });        
            });
            promises.push(intermediatePromise.promise);
        });

        $q.all(promises).then( function(test) {
          q.resolve(response);
        })
        return q.promise;
    })

}






























function flatTraverseGood () {
  var q = $q.defer();
  var promises = [];
  var argumentsFollow = arguments;
  var request = traversonService
    .newRequest()
    .follow([].shift.call(argumentsFollow))
    .getResource();

    return request
    .result
    .then(function(response) {
        for(var i = 0; i < argumentsFollow.length; i++) {
            let intermediatePromise = $q.defer();
            request.continue().then(function(subRequest) {
                var property = argumentsFollow[0][argumentsFollow[0].length - 1];
                // console.log(property);
                subRequest
                .newRequest()
                .follow([].shift.call(argumentsFollow))
                .getResource()
                .result
                .then(function(subResponse) {
                    if(Object.keys(subResponse).length <= 1) {
                        subResponse = [];
                    }
                    Object.defineProperty(response, property, {
                            value: subResponse,
                            writable: true,
                            enumerable: true,
                            configurable: true
                    });
                    // console.log('SUB ', response);
                    intermediatePromise.resolve();
                    // console.log('after RESOLVE'); 
                    
                });        
            });
            // intermediatePromise.promise.then(function() {
            //     console.log('END PROMISE');
            // })
            // console.log('before ADD in TAB');
            promises.push(intermediatePromise.promise);
        }
        // setTimeout(function() {
        //     q.resolve(response);    
        // }, 2000);
        // recursiveTraverse(request, argumentsFollow)
        //     .then(function(res) {
        //         q.resolve(Object.assign(response, res));
        //     })
        $q.all(promises).then( function(test) {
            // console.log('RESPONSE', test);
          q.resolve(response);
        }).catch(function(err){
            console.error('ERR', err);
        });
        return q.promise;
    })

    
}

function recursiveTraverse(request, links) {
    var q = $q.defer();

    // console.log('links', links[0]);
    // console.log('request', request);
    if(links.length > 0) {
        return request.continue().then(function(subRequest) {
            var property = links[0][links[0].length - 1];
            console.log(property);
            subRequest
            .newRequest()
            .follow([].shift.call(links))
            .getResource()
            .result
            .then(function(response) {
                // var lol = recursiveTraverse(request, links);
                // console.log('#######', lol);
                // var lil = Object.assign(response, lol);
                // response[property] = lol;


                recursiveTraverse(request, links)
                    .then(function(subResponse) {
                        console.log(subResponse);
                        Object.defineProperty(response, property, {
                        value: subResponse,
                        writable: true,
                        enumerable: true,
                        configurable: true
                    });
                    
                    console.log('recursive response', response);
                    return q.resolve(response);
                })
            });
        });
    }
    q.resolve({});
    return q.promise;
}
 
 function findByName (applicationName) {
     var self = this;

     return flatTraverse(
         ['applicationResourceList[name:' + applicationName + ']', 'self'],
         ['modules'],
         ['server'],
         ['deployments'],
         ['containers']
    ).then(function(response) {
        // console.log('RESPONSE', response);
        return response;
    }).catch(function(err) {
        console.error(err);
        stopPolling.call ( self );
    });
 }























































// Récupération d'une application selon son nom
function findByNameOLd ( applicationName ) {
    var self = this;
    var res = null;
    var q = $q.defer();

    var request = traversonService
    .newRequest()
    .follow('applicationResourceList[name:' + applicationName + ']', 'self')
    .getResource();

    request
    .result
    .then(function(app) {
        res = app;
        request.continue().then(function(request) {
            request
            .newRequest()
            .follow('modules')
            .getResource()
            .result
            .then(function(modules) {
                if(modules._embedded) {
                    modules = modules._embedded.modules;
                } else {
                    modules = [];
                }
                res.modules = modules;
                return request
                .newRequest()
                .follow('server')
                .getResource()
                .result
            })
            .then(function(server) {
                    res.server = server;
                    // console.log('res', res);
                     q.resolve(assignObject(self.state, res));
                });
        });
    }).catch ( function () {
        stopPolling.call ( self );
    });
    return q.promise;
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
