'use strict';

angular.module('environmentreservationApp')
	.controller('ReservationCalendarController', ['$scope','Reservation', 'Environment','_', function($scope,  Reservation,Environment ,_) {

        var colorSets = [
            {color:'Red', textColor:'Black'},
            {color:'Green', textColor:'Black'},
            {color:'Blue', textColor:'Black'},
            {color:'LightGray', textColor:'Black'},
            {color:'Yellow', textColor:'Black'},
            {color:'LightPink', textColor:'Black'},
        ];


        $scope.allEventSources = [];
        $scope.eventSources = [];

        $scope.selectedEnv = null;


        $scope.$watch('selectedEnv',function(newVal,oldVal){
            if (_.isNull(oldVal) && _.isNull(newVal))return;
            applyFilter();
         });

	    Reservation.query(function(values){
	       _.reduce(convertToEvent(values),accumulate, $scope.allEventSources);
	       applyFilter();
	      });


        function applyFilter(){
            $scope.eventSources.length = 0;
            var filtered = _.filter($scope.allEventSources,function(val){

                return _.isNull($scope.selectedEnv) ||  (val.environmentId == $scope.selectedEnv.environmentId);

             });
           _.reduce(filtered, accumulate, $scope.eventSources);
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
                return colorSets.pop();
            }else{
                return {};
            }
       }


      function accumulate(acc,e){
        acc.push(e); return acc;
       }
    }]);

