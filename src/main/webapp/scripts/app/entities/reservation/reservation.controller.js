'use strict';

angular.module('environmentreservationApp')
    .controller('ReservationController', function ($scope, $state, Reservation, ReservationSearch, ParseLinks, AlertService , _) {

        var colorSets = [
            {color:'Red', textColor:'Black'},
            {color:'Green', textColor:'Black'},
            {color:'Blue', textColor:'Black'},
            {color:'LightGray', textColor:'Black'},
            {color:'Yellow', textColor:'Black'},
            {color:'LightPink', textColor:'Black'},
        ];

        $scope.rc = new Object();
        $scope.rc.searchQuery = "";

        $scope.reservations = [];
        $scope.predicate = 'id';
        $scope.reverse = true;
        $scope.page = 1;
        $scope.filterMessage;
        $scope.filterFunction;

        $scope.calendarMode = false;
        $scope.eventSources = [];



        $scope.$on('reservation.status.change',function(event,args){
            $scope.refresh();
        });

        $scope.$watch('reservations', function(newVal, oldVal){
            initEventSources();
        });


        $scope.loadAll = function() {
             $scope.filterFunction = $scope.loadAll;
             Reservation.query({page: $scope.page - 1, size: 20, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']}, function(result, headers) {
                  $scope.links = ParseLinks.parse(headers('link'));
                  $scope.totalItems = headers('X-Total-Count');
                  $scope.reservations = result;
              });
         };

        $scope.loadAll();

        $scope.toggleCalendarMode = function(){
            $scope.calendarMode = !$scope.calendarMode;
        }

        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };

        $scope.getClashing = function(reservation){
            $scope.filterFunction = function(){$scope.getClashing(reservation);};
            Reservation.getClashingReservations({id:reservation.id}, function(result){
                    $scope.reservations = result;
                    setFilterMessage('reservations clashing with id '+reservation.id);
            });
        }

        $scope.search = function () {
            $scope.filterFunction = $scope.search;
            ReservationSearch.query({query: $scope.rc.searchQuery}, function(result) {
                $scope.reservations = result;
                setFilterMessage($scope.rc.searchQuery);
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.clearFilter = function () {
            $scope.filterMessage = '';
            $scope.rc.searchQuery = '';
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

        // Calendar filter

        function initEventSources(){
            $scope.eventSources.length=0;
            _.reduce(convertToEvent($scope.reservations),accumulate, $scope.eventSources);
        }


       function convertToEvent(reservations){
            var result =  _.chain(reservations).map(fromReservationToEvent).groupBy(function(e){
                return e.reservation.environment.id;
               })
           .values()
           .map(function(l){
                return _.extend({
                    events:l,
                    environment:l[0].reservation.environment.environmentDescription,
                    environmentId:l[0].reservation.environment.id
              }, pickColor());
           })
           .value();
        return result;
       }

       function fromReservationToEvent(r){
                       return {
                       id:r.id,
                       title:r.project,
                       start:r.startDate,
                       end:r.endDate,
                       editable:false,
                       reservation:r
                       };
                   }

       function pickColor(){
            if (colorSets.length>0){
                var color = colorSets.shift();
                colorSets.push(color);
                return color;
            }else{
                return {};
            }
       }

      function accumulate(acc,e){
        acc.push(e); return acc;
       }

    });
