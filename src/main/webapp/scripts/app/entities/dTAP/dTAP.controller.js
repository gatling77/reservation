'use strict';

angular.module('environmentreservationApp')
    .controller('DTAPController', function ($scope, $state, DTAP, DTAPSearch) {

        $scope.dTAPs = [];
        $scope.loadAll = function() {
            DTAP.query(function(result) {
               $scope.dTAPs = result;
            });
        };
        $scope.loadAll();


        $scope.search = function () {
            DTAPSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.dTAPs = result;
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.dTAP = {
                levelName: null,
                levelId: null,
                id: null
            };
        };
    });
