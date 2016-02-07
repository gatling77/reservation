'use strict';

angular.module('environmentreservationApp').controller('DTAPDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'DTAP',
        function($scope, $stateParams, $uibModalInstance, entity, DTAP) {

        $scope.dTAP = entity;
        $scope.load = function(id) {
            DTAP.get({id : id}, function(result) {
                $scope.dTAP = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('environmentreservationApp:dTAPUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.dTAP.id != null) {
                DTAP.update($scope.dTAP, onSaveSuccess, onSaveError);
            } else {
                DTAP.save($scope.dTAP, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
}]);
