'use strict';

angular.module('environmentreservationApp')
	.controller('ReservationDeleteController', function($scope, $uibModalInstance, entity, Reservation) {

        $scope.reservation = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Reservation.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
