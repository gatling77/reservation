'use strict';

angular.module('environmentreservationApp')
    .factory('Reservation', function ($resource, DateUtils) {
        return $resource('api/reservations/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'confirm':{
                method: 'PUT',
                url: 'api/reservations/:id/confirm'
            },
            'close':{
                method: 'PUT',
                url: 'api/reservations/:id/close'
            },
            'getClashingReservations':{
                method: 'GET',
                isArray: true,
                url: 'api/reservations/:id/clashing'
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.startDate = DateUtils.convertLocaleDateFromServer(data.startDate);
                    data.endDate = DateUtils.convertLocaleDateFromServer(data.endDate);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.startDate = DateUtils.convertLocaleDateToServer(data.startDate);
                    data.endDate = DateUtils.convertLocaleDateToServer(data.endDate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.startDate = DateUtils.convertLocaleDateToServer(data.startDate);
                    data.endDate = DateUtils.convertLocaleDateToServer(data.endDate);
                    return angular.toJson(data);
                }
            }
        });
    });
