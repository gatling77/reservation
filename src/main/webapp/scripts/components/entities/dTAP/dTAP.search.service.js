'use strict';

angular.module('environmentreservationApp')
    .factory('DTAPSearch', function ($resource) {
        return $resource('api/_search/dTAPs/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
