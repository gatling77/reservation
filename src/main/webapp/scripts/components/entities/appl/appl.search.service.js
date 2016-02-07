'use strict';

angular.module('environmentreservationApp')
    .factory('ApplSearch', function ($resource) {
        return $resource('api/_search/appls/:query', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
