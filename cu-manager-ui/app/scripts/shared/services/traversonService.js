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
   * @name webuiApp.TraversonService
   * @description
   * # TraversonService
   * Factory in the webuiApp.
   */
	angular
		.module('webuiApp')
		.factory('TraversonService', TraversonService);

	TraversonService.$inject = [
		'$q',
		'traverson'
	];


	function TraversonService($q, traverson) {

		traverson.registerMediaType(TraversonJsonHalAdapter.mediaType, TraversonJsonHalAdapter);

		return {
			Instance: Instance
		}

		function Instance(traversonFrom) {
			var that = this;
			this.traversonService = traverson
				.from(traversonFrom)
				.jsonHal()
				.withRequestOptions({ headers: { 'Content-Type': 'application/hal+json' } });

			/**
			 * @name flatTraverson
			 * @description
			 * public fonction wich allow to request a set of resource and flatten them in a object
			 * @params {String[][]} with
			 * first index the route request where all subRequest could be begin
			 * all other index are request wich be added in property of result
			 * the property name which be the first index
			 * 
			 * @returns {Object} wich contains all Resource requested
			 */
			this.flatTraverson = function () {
				var q = $q.defer();
				var promises = [];
				var argumentsFollow = arguments;
				var request = that.traversonService
					.newRequest()
					.follow([].shift.call(argumentsFollow))
					.getResource();

				return request
					.result
					.then(function (response) {
						angular.forEach(argumentsFollow, function (argumentFollow) {
							var intermediatePromise = $q.defer();
							request.continue().then(function (subRequest) {
								var property = argumentFollow[0];
								subRequest
									.newRequest()
									.follow(argumentFollow)
									.getResource()
									.result
									.then(function (subResponse) {
										if (Object.keys(subResponse).length <= 1) {
											subResponse = [];
										}
										if (subResponse._embedded) {
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

						$q.all(promises).then(function (test) {
							q.resolve(response);
						})
						return q.promise;
					});
			}

			/**
			 * @name concatTraverson
			 * @description
			 * public fonction wich allow to request a set of resource and concat them in a array
			 * each request have to return an array
			 * @params {String[][]} with
			 * first index the route request where all subRequest could be begin
			 * all other index are request wich be concat and return in a array so each of them have to return a array
			 * @returns {Array} Array with all subResource array concat
			 */
			this.concatTraverson = function () {

				var q = $q.defer();
				var promises = [];
				var argumentsFollow = arguments;
				var request = that.traversonService
					.newRequest()
					.follow([].shift.call(argumentsFollow))
					.getResource();
				return request
					.result
					.then(function (response) {
						var result = [];
						angular.forEach(argumentsFollow, function (argumentFollow) {
							var intermediatePromise = $q.defer();
							request.continue().then(function (subRequest) {
								subRequest
									.newRequest()
									.follow(argumentFollow)
									.getResource()
									.result
									.then(function (subResponse) {
										if (subResponse._embedded) {
											subResponse = subResponse._embedded[Object.keys(subResponse._embedded)[0]];
										}
										else if(subResponse._links){
											subResponse = [];
										}
										if (result.length > 0) {
											result.concat(subResponse);
										} else {
											result = subResponse;
										}
										intermediatePromise.resolve();
									});
							});
							promises.push(intermediatePromise.promise);
						});

						$q.all(promises).then(function () {
							q.resolve(result);
						})
						return q.promise;
					})
			}
		}

	}
})();
