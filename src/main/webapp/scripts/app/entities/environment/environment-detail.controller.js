'use strict';

angular.module('environmentreservationApp')
    .controller('EnvironmentDetailController', function ($scope, $rootScope, $stateParams, entity, Environment, DTAP) {
        $scope.environment = entity;
        $scope.load = function (id) {
            Environment.get({id: id}, function(result) {
                $scope.environment = result;
            });
        };
        var unsubscribe = $rootScope.$on('environmentreservationApp:environmentUpdate', function(event, result) {
            $scope.environment = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
