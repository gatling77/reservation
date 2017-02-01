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

        $scope.isAuthenticated = Principal.isAuthenticated;

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

        if($scope.isAuthenticated()) {
            $scope.enviornments = Environment.query();
            $scope.applications = Appl.query();            
        }
        $scope.reservations = [];
        $scope.predicate = 'id';
        $scope.reverse = true;
        $scope.page = 1;
        $scope.filterMessage;
        $scope.filterFunction;

        $scope.calendarMode = false;
        $scope.eventSources = [];

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

        $scope.$watch('reservations', function(newVal, oldVal){
           initEvents();
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
                    //refresh calendar
                    initEvents();
                    var tmp = $scope.viewDate;
                    $scope.viewDate = new Date(0);
                    $scope.viewDate = tmp;
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

        var colourSets = [
            {primary:'rgb(26, 188, 156)', secondary:'rgba(26, 188, 156,0.2)'},
            {primary:'rgb(52, 152, 219)', secondary:'rgba(52, 152, 219,0.2)'},
            {primary:'rgb(155, 89, 182)', secondary:'rgba(155, 89, 182,0.2)'},
            {primary:'rgb(52, 73, 94)', secondary:'rgba(52, 73, 94,0.2)'},
            {primary:'rgb(241, 196, 15)', secondary:'rgba(241, 196, 15,0.2)'},
            {primary:'rgb(230, 126, 34)', secondary:'rgba(230, 126, 34,0.2)'},
        ];
       function pickColourPalette() {
           if (colourSets.length>0){
                var colour = colourSets.shift();
                colourSets.push(colour);
                return colour;
            }else{
                return {};
            }
       }

      function accumulate(acc,e){
        acc.push(e); return acc;
       }
    

      

//Calendar stuff
        $scope.calendarView = 'month';
        $scope.viewDate = new Date();
        $scope.cellIsOpen = false;
        $scope.events = [];
        
        //function handles calendar event click
        $scope.eventClicked = function(event) {
            console.log("click");
            console.log(event);
        };


        //funcion handles a calendar event drag
        $scope.eventTimesChanged = function(calEvent, newStart, newEnd) {
            calEvent.startsAt = newStart;
            calEvent.endsAt = newEnd;
            calEvent.reservation.endDate = DateUtils.convertLocaleDateToServer(newEnd);
            calEvent.reservation.startDate = DateUtils.convertLocaleDateToServer(newStart);
            Reservation.update(calEvent.reservation, function(result) {
                //$scope.$emit('environmentreservationApp:reservationUpdate', result);
               // console.log("success");
               // console.log(result);
            }, function(result) {
                //console.log("fail");
                //console.log(result);
            });
        }

        $scope.viewChangeClicked = function(nextView) { //this functions disables day view on the calendar
            if (nextView === 'day') {
                return false;
            }
        };

        function initEvents() {
            $scope.events.length=0;
            _.forEach($scope.reservations, function(r) {
                $scope.events.push({
                    title: r.project + ", " + r.appl.applName + ", " + r.environment.environmentDescription,
                    startsAt: new Date(r.startDate),
                    endsAt: new Date(r.endDate),
                    color: pickColourPalette(),
                    actions: $scope.isAuthenticated()?[
                          { 
                            label: '<i class=\'glyphicon glyphicon-eye-open\'></i>',                    
                            onClick: function(args) {                             
                                    $state.go('reservation.detail', {id:args.calendarEvent.reservation.id});                  
                            }
                         },
                         { // an array of actions that will be displayed next to the event title
                            label: '<i class=\'glyphicon glyphicon-pencil\'></i>', // the label of the action
                            cssClass: r.editAllowed?'':'hidden', // a CSS class that will be added to the action element so you can implement custom styling
                            onClick: function(args) { // the action that occurs when it is clicked. The first argument will be an object containing the parent event
                                if(args.calendarEvent.reservation.editAllowed) {
                                    $state.go('reservation.edit', {id:args.calendarEvent.reservation.id});
                                }                            
                            }
                        },
                        { 
                            label: '<i class=\'glyphicon glyphicon-trash\'></i>',                    
                            onClick: function(args) {                             
                                    $state.go('reservation.delete', {id:args.calendarEvent.reservation.id});                  
                            }
                        }
                    ]:[],
                    draggable:  $scope.isAuthenticated(),
                    resizable:  $scope.isAuthenticated(),
                    allDay: true,
                   // cssClass: 'a-css-class-name',
                    //incrementsBadgeTotal: true,
                    reservation: r
                });
            });
        };
    });
