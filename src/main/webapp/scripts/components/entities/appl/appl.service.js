'use strict';

angular.module('environmentreservationApp')
    .factory('Appl', function ($resource, DateUtils) {
        return $resource('api/appls/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    });
