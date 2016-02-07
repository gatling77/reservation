'use strict';

angular.module('environmentreservationApp')
	.controller('ApplDeleteController', function($scope, $uibModalInstance, entity, Appl) {

        $scope.appl = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Appl.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
