'use strict';

angular.module('environmentreservationApp')
    .controller('ApplDetailController', function ($scope, $rootScope, $stateParams, entity, Appl, Environment) {
        $scope.appl = entity;
        $scope.load = function (id) {
            Appl.get({id: id}, function(result) {
                $scope.appl = result;
            });
        };
        var unsubscribe = $rootScope.$on('environmentreservationApp:applUpdate', function(event, result) {
            $scope.appl = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
