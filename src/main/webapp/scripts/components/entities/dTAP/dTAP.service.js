'use strict';

angular.module('environmentreservationApp')
    .factory('DTAP', function ($resource, DateUtils) {
        return $resource('api/dTAPs/:id', {}, {
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
