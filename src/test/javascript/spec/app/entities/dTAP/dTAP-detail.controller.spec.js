'use strict';

describe('Controller Tests', function() {

    describe('DTAP Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockDTAP;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockDTAP = jasmine.createSpy('MockDTAP');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'DTAP': MockDTAP
            };
            createController = function() {
                $injector.get('$controller')("DTAPDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'environmentreservationApp:dTAPUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
