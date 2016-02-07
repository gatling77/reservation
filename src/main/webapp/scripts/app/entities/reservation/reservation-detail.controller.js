'use strict';

angular.module('environmentreservationApp')
    .controller('ReservationDetailController', function ($scope, $rootScope, $stateParams, entity, Reservation, User, Appl, Environment) {
        $scope.reservation = entity;
        $scope.load = function (id) {
            Reservation.get({id: id}, function(result) {
                $scope.reservation = result;
            });
        };
        var unsubscribe = $rootScope.$on('environmentreservationApp:reservationUpdate', function(event, result) {
            $scope.reservation = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
