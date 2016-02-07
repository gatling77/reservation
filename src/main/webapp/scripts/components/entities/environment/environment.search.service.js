'use strict';

angular.module('environmentreservationApp')
    .factory('EnvironmentSearch', function ($resource) {
        return $resource('api/_search/environments/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
