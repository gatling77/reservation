'use strict';

angular.module('environmentreservationApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


