'use strict';

describe('Controller Tests', function() {

    describe('Reservation Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockReservation, MockUser, MockAppl, MockEnvironment;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockReservation = jasmine.createSpy('MockReservation');
            MockUser = jasmine.createSpy('MockUser');
            MockAppl = jasmine.createSpy('MockAppl');
            MockEnvironment = jasmine.createSpy('MockEnvironment');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Reservation': MockReservation,
                'User': MockUser,
                'Appl': MockAppl,
                'Environment': MockEnvironment
            };
            createController = function() {
                $injector.get('$controller')("ReservationDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'environmentreservationApp:reservationUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
