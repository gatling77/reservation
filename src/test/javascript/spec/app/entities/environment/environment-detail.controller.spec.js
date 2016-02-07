'use strict';

describe('Controller Tests', function() {

    describe('Environment Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockEnvironment, MockDTAP;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockEnvironment = jasmine.createSpy('MockEnvironment');
            MockDTAP = jasmine.createSpy('MockDTAP');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Environment': MockEnvironment,
                'DTAP': MockDTAP
            };
            createController = function() {
                $injector.get('$controller')("EnvironmentDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'environmentreservationApp:environmentUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
