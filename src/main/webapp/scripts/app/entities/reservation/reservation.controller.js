'use strict';

angular.module('environmentreservationApp')
    .controller('ReservationController', function ($scope, $state, DateUtils, calendarConfig, Reservation, ReservationSearch,  Principal, Environment, Appl, ParseLinks, AlertService , _, $filter) {

        var colorSets = [
            {color:'Red', textColor:'Black'},
            {color:'Green', textColor:'Black'},
            {color:'Blue', textColor:'Black'},
            {color:'LightGray', textColor:'Black'},
            {color:'Yellow', textColor:'Black'},
            {color:'LightPink', textColor:'Black'},
        ];

        // $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.loggedIn = Principal.isAuthenticated();
        $scope.rc = new Object();
        $scope.rc.searchQuery = "";
        $scope.rc.searchId = "";
        $scope.rc.searchStart = "";
        $scope.rc.searchEnd = "";
        $scope.rc.searchProject = "";
        $scope.rc.searchStatus = "";
        $scope.rc.searchRequestor = "";
        $scope.rc.searchApp = "";
        $scope.rc.searchEnvoirnment = "";

        if($scope.loggedIn) {
            $scope.enviornments = Environment.query();
            $scope.applications = Appl.query();            
        }
        $scope.predicate = 'id';
        $scope.reverse = true;
        $scope.page = 1;
        $scope.filterMessage;
        $scope.filterFunction;

        $scope.calendarMode = false;

            var acc = document.getElementsByClassName("accordion");
            var i;

            for (i = 0; i < acc.length; i++) {
                acc[i].onclick = function(){
                    this.classList.toggle("active");
                    this.nextElementSibling.classList.toggle("show");
                }
            }


        $scope.$on('reservation.status.change',function(event,args){
            $scope.refresh();
        });



        $scope.loadAll = function() {
             $scope.filterFunction = $scope.loadAll;
             Reservation.query({page: $scope.page - 1, size: 20, sort: [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'), 'id']}, function(result, headers) {
                  $scope.links = ParseLinks.parse(headers('link'));
                  $scope.totalItems = headers('X-Total-Count');    
                  $scope.reservations =  result;         
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
            var data = {};
            //if($scope.rc.searchQuery)
                //data.query = $scope.rc.searchQuery;
            console.log($scope.rc.searchStart);
            if($scope.rc.searchStart)
                data.dateFrom = $filter("date")($scope.rc.searchStart,"yyyy-MM-dd");
            if($scope.rc.searchEnd) {
                data.dateTo = $filter("date")($scope.rc.searchEnd,"yyyy-MM-dd");
            }
            if($scope.rc.searchProject)
                data.project = $scope.rc.searchProject;
            if($scope.rc.searchStatus)
                data.status = $scope.rc.searchStatus;
            if($scope.rc.searchRequestor)
                data.reservationRequestor = $scope.rc.searchRequestor;
            if($scope.rc.searchApp)
                data.appName = $scope.rc.searchApp.applName;
            if($scope.rc.searchEnvoirnment)
                data.envDescription = $scope.rc.searchEnvoirnment.environmentDescription;
            ReservationSearch.query(data, function(result) {
                $scope.reservations = result;
                //setFilterMessage($scope.rc.searchQuery);
            }, function(response) {
                if(response.status === 404) {
                    $scope.loadAll();
                }
            });
        };

        $scope.clearFilter = function () {
            $scope.filterMessage = '';
            $scope.rc.searchQuery = "";
            $scope.rc.searchId = "";
            $scope.rc.searchStart = "";
            $scope.rc.searchEnd = "";
            $scope.rc.searchProject = "";
            $scope.rc.searchStatus = "";
            $scope.rc.searchRequestor = "";
            $scope.rc.searchApp = "";
            $scope.rc.searchEnvoirnment = "";
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
