'use strict';

angular.module('environmentreservationApp')
    .controller('ReservationController', function ($scope, $state, Reservation, ReservationSearch, ParseLinks, AlertService) {

        $scope.reservations = [];
        $scope.predicate = 'id';
        $scope.reverse = true;
        $scope.page = 1;

        $scope.$on('reservation.list.reload',function(event,args){
            $scope.loadAll();
         });

        $scope.loadAll = function() {
             Reservation.query({page: $scope.page - 1, size: 20, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']}, function(result, headers) {
                  $scope.links = ParseLinks.parse(headers('link'));
                  $scope.totalItems = headers('X-Total-Count');
                  $scope.reservations = result;
              });
         };

        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.getClashing = function(reservation){
            $scope.reservations = Reservation.getClashingReservations({id:reservation.id});
        }

        $scope.search = function () {
            ReservationSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.reservations = result;
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
            $scope.reservation = {
                startDate: null,
                endDate: null,
                project: null,
                status: null,
                id: null
            };
        };
    });