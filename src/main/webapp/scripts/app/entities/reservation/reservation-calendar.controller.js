'use strict';

angular.module('environmentreservationApp')
	.controller('ReservationCalendarController', ['$scope','Reservation', 'Environment','_', function($scope,  Reservation,Environment ,_) {

        $scope.eventSources = [];

        $scope.environments = Environment.query();
        $scope.selectedEnv = null;


        $scope.$watch('selectedEnv',function(){});

	    Reservation.query(function(values){
	      var events = convertToEvent(values);
	      _.reduce(events,function(acc,e){acc.push(e); return acc;}, $scope.eventSources);
	     });

       function convertToEvent(reservations){
            var result =  _.chain(reservations).map(fromReservationToEvent).groupBy(function(e){
                return e.reservation.environment.id;
               })
           .values()
           .map(function(l){
                return {
                    events:l
              };
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
                       editable:r.editAllowed,
                       reservation:r
                       };
                   }
    }]);

