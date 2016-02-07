'use strict';

angular.module('environmentreservationApp')
	.controller('EnvironmentDeleteController', function($scope, $uibModalInstance, entity, Environment) {

        $scope.environment = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            Environment.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
