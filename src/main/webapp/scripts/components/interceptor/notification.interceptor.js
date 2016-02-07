 'use strict';

angular.module('environmentreservationApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-environmentreservationApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-environmentreservationApp-params')});
                }

                var warningKey = response.headers('X-environmentreservationApp-warning');
                if (angular.isString(warningKey)) {
                    AlertService.warning(warningKey, { param : response.headers('X-environmentreservationApp-params')});
                }

                return response;
            }
        };
    });
