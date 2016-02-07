'use strict';

angular.module('environmentreservationApp').controller('ReservationDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Reservation', 'User', 'Appl', 'Environment',
        function($scope, $stateParams, $uibModalInstance, entity, Reservation, User, Appl, Environment) {

        $scope.reservation = entity;
        $scope.users = User.query();
        $scope.appls = Appl.query();

        $scope.load = function(id) {
            Reservation.get({id : id}, function(result) {
                $scope.reservation = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('environmentreservationApp:reservationUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.reservation.id != null) {
                Reservation.update($scope.reservation, onSaveSuccess, onSaveError);
            } else {
                Reservation.save($scope.reservation, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForStartDate = {};

        $scope.datePickerForStartDate.status = {
            opened: false
        };

        $scope.datePickerForStartDateOpen = function($event) {
            $scope.datePickerForStartDate.status.opened = true;
        };
        $scope.datePickerForEndDate = {};

        $scope.datePickerForEndDate.status = {
            opened: false
        };

        $scope.datePickerForEndDateOpen = function($event) {
            $scope.datePickerForEndDate.status.opened = true;
        };
}]);
