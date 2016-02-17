'use strict';

angular.module('environmentreservationApp')
    .controller('ReservationController', function ($scope, $state, Reservation, ReservationSearch, ParseLinks, AlertService , _) {

        $scope.reservations = [];
        $scope.predicate = 'id';
        $scope.reverse = true;
        $scope.page = 1;
        $scope.filterMessage;
        $scope.filterFunction;


        $scope.$on('reservation.status.change',function(event,args){
            $scope.refresh();
        });

        $scope.loadAll = function() {
             $scope.filterFunction = $scope.loadAll;
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
            $scope.filterFunction = function(){$scope.getClashing(reservation);};
            $scope.reservations = Reservation.getClashingReservations({id:reservation.id});
            setFilterMessage('reservations clashing with id '+reservation.id);
        }

        $scope.search = function () {
            $scope.filterFunction = $scope.search;
            ReservationSearch.query({query: $scope.searchQuery}, function(result) {
                $scope.reservations = result;
                setFilterMessage($scope.searchQuery);
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.clearFilter = function () {
            $scope.filterMessage = '';
            $scope.searchQuery = '';
            $scope.loadAll();
        }

        $scope.refresh = function () {
            $scope.filterFunction();
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

        function setFilterMessage(message){
            $scope.filterMessage = message;
        }
    });
