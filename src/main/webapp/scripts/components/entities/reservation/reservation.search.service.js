'use strict';

angular.module('environmentreservationApp')
    .factory('ReservationSearch', function ($resource) {
        return $resource('api/_search/reservations', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
