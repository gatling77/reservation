'use strict';

angular.module('environmentreservationApp').controller('ApplDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Appl', 'Environment',
        function($scope, $stateParams, $uibModalInstance, entity, Appl, Environment) {

        $scope.appl = entity;
        $scope.environments = Environment.query();
        $scope.load = function(id) {
            Appl.get({id : id}, function(result) {
                $scope.appl = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('environmentreservationApp:applUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.appl.id != null) {
                Appl.update($scope.appl, onSaveSuccess, onSaveError);
            } else {
                Appl.save($scope.appl, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
