'use strict';

angular.module('environmentreservationApp')
    .controller('DTAPDetailController', function ($scope, $rootScope, $stateParams, entity, DTAP) {
        $scope.dTAP = entity;
        $scope.load = function (id) {
            DTAP.get({id: id}, function(result) {
                $scope.dTAP = result;
            });
        };
        var unsubscribe = $rootScope.$on('environmentreservationApp:dTAPUpdate', function(event, result) {
            $scope.dTAP = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
