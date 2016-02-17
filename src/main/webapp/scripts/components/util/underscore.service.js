'use strict';

angular.module('environmentreservationApp')
    .service('_', ['$window',function($window){
        return $window._;
    }]);
