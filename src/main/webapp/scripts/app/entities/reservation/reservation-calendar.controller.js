'use strict';

angular.module('environmentreservationApp')
	.controller('ReservationCalendarController', function($scope,  Reservation) {
        $scope.eventSource =
        {
        events: [
                {
                title: 'Event1',
                start: '2016-03-03'
            },
            {
                title: 'Event2',
                start: '2016-03-06'
            }
        ],
        color: 'yellow',   // an option!
        textColor: 'black' // an option!
       };
        $scope.eventSources = [$scope.eventSource];
    });

