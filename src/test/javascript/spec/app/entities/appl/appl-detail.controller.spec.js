'use strict';

describe('Controller Tests', function() {

    describe('Appl Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockAppl, MockEnvironment;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockAppl = jasmine.createSpy('MockAppl');
            MockEnvironment = jasmine.createSpy('MockEnvironment');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Appl': MockAppl,
                'Environment': MockEnvironment
            };
            createController = function() {
                $injector.get('$controller')("ApplDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'environmentreservationApp:applUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
