'use strict';

angular.module('environmentreservationApp').controller('EnvironmentDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Environment', 'DTAP',
        function($scope, $stateParams, $uibModalInstance, entity, Environment, DTAP) {

        $scope.environment = entity;
        $scope.dtaps = DTAP.query();
        $scope.load = function(id) {
            Environment.get({id : id}, function(result) {
                $scope.environment = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('environmentreservationApp:environmentUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.environment.id != null) {
                Environment.update($scope.environment, onSaveSuccess, onSaveError);
            } else {
                Environment.save($scope.environment, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
