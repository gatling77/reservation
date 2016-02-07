'use strict';

angular.module('environmentreservationApp')
	.controller('DTAPDeleteController', function($scope, $uibModalInstance, entity, DTAP) {

        $scope.dTAP = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            DTAP.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
