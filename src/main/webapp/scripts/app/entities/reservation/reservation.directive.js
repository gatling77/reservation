'use strict';

angular.module('environmentreservationApp')
    .directive('rsrvStatusCommand',
    function () {
        return {
            restrict: 'E',
            template: '<div class="btn-group flex-btn-group-container">' +
            ' <button ng-show="ctrl.canConfirm()" type="submit"' +
            ' ng-click="ctrl.confirm()"' +
            ' class="btn btn-success btn-sm">' +
            ' <span class="glyphicon glyphicon-ok"></span>' +
            ' <span class="hidden-xs hidden-sm"></span>' +
            ' </button>' +
            ' <button ng-show="ctrl.canClose()" type="submit"' +
            ' ng-click="ctrl.close(id)"' +
            ' class="btn btn-danger btn-sm">' +
            ' <span class="glyphicon glyphicon-remove-circle"></span>' +
            ' <span class="hidden-xs hidden-sm"></span>' +
            ' </button>' +
            ' </div>',

            scope: {
                reservation: '=reservation'
            },
            controller: ["$scope", "Reservation", function ($scope, Reservation) {
                var ctrl = this;
                ctrl.close = function () {
                    Reservation.close({ id: ctrl.reservation.id }, {}, onSuccess(ctrl.reservation, 'CLOSED'));
                }

                ctrl.confirm = function () {
                    Reservation.confirm({ id: ctrl.reservation.id }, {}, onSuccess(ctrl.reservation, 'CONFIRMED'));
                }

                ctrl.canConfirm = function () {
                    return ctrl.reservation.confirmAllowed;
                }

                ctrl.canClose = function () {
                    return ctrl.reservation.closeAllowed;
                }

                function onSuccess(reservation, status) {
                    return function (result) {
                        $scope.$emit('reservation.status.change', { reservation: reservation, status: status });
                    }
                }
            }],
            controllerAs: 'ctrl',
            bindToController: true
        }
    }
    )
    .directive('rsrvStatus',
    function () {
        return {
            restrict: 'E',
            scope: {
                reservation: '=reservation'
            },
            template: '<span class="label {{ctrl.getClass()}}">{{ctrl.reservation.status}}</span>',
            controller: function () {
                var ctrl = this;

                ctrl.getClass = function () {
                    var mapping = {
                        'CONFIRMED': 'label-success',
                        'CLOSED': 'label-default',
                        'CONFLICT': 'label-danger'
                    };
                    return mapping[ctrl.reservation.status];
                }
            },
            controllerAs: 'ctrl',
            bindToController: true
        }
    }
    ).directive('reservationCalendar',
    function () {
        return {
            restrict: 'E',
            scope: {
                reservations: '=',
                loggedIn: '='
            },
            templateUrl: '/scripts/app/entities/reservation/reservation-calendar-template.html',
            controller: function ($scope, $state, _, Reservation, DateUtils) {
                var ctrl = this;
                ctrl.events = [];
                var colourSets = [
                    { primary: 'rgb(26, 188, 156)', secondary: 'rgba(26, 188, 156,0.2)' },
                    { primary: 'rgb(52, 152, 219)', secondary: 'rgba(52, 152, 219,0.2)' },
                    { primary: 'rgb(155, 89, 182)', secondary: 'rgba(155, 89, 182,0.2)' },
                    { primary: 'rgb(52, 73, 94)', secondary: 'rgba(52, 73, 94,0.2)' },
                    { primary: 'rgb(241, 196, 15)', secondary: 'rgba(241, 196, 15,0.2)' },
                    { primary: 'rgb(230, 126, 34)', secondary: 'rgba(230, 126, 34,0.2)' },
                ];


                $scope.$watch('ctrl.reservations', function () {
                    ctrl.initEvents();
                });


                ctrl.initEvents = function () {
                    ctrl.events.length = 0;
                    _.forEach(ctrl.reservations, function (r) {
                        ctrl.events.push({
                            title: r.project + ", " + r.appl.applName + ", " + r.environment.environmentDescription,
                            startsAt: new Date(r.startDate),
                            endsAt: new Date(r.endDate),
                            color: ctrl.pickColourPalette(),
                            actions: ctrl.loggedIn ? [
                                {
                                    label: '<i class=\'glyphicon glyphicon-eye-open\'></i>',
                                    onClick: function (args) {
                                        $state.go('reservation.detail', { id: args.calendarEvent.reservation.id });
                                    }
                                },
                                { // an array of actions that will be displayed next to the event title
                                    label: '<i class=\'glyphicon glyphicon-pencil\'></i>', // the label of the action
                                    cssClass: r.editAllowed ? '' : 'hidden', // a CSS class that will be added to the action element so you can implement custom styling
                                    onClick: function (args) { // the action that occurs when it is clicked. The first argument will be an object containing the parent event
                                        if (args.calendarEvent.reservation.editAllowed) {
                                            $state.go('reservation.edit', { id: args.calendarEvent.reservation.id });
                                        }
                                    }
                                },
                                {
                                    label: '<i class=\'glyphicon glyphicon-trash\'></i>',
                                    onClick: function (args) {
                                        $state.go('reservation.delete', { id: args.calendarEvent.reservation.id });
                                    }
                                }
                            ] : [],
                            draggable: ctrl.loggedIn,
                            resizable: ctrl.loggedIn,
                            allDay: true,
                            // cssClass: 'a-css-class-name',
                            //incrementsBadgeTotal: true,
                            reservation: r
                        });
                    });
                };



                ctrl.pickColourPalette = function () {
                    if (colourSets.length > 0) {
                        var colour = colourSets.shift();
                        colourSets.push(colour);
                        return colour;
                    } else {
                        return {};
                    }
                }

                ctrl.calendarView = 'month';
                ctrl.viewDate = new Date();
                ctrl.cellIsOpen = false;
                ctrl.events = [];

                //function handles calendar event click
                ctrl.eventClicked = function (event) {
                    // console.log("click");
                    // console.log(event);
                };


                //funcion handles a calendar event drag
                ctrl.eventTimesChanged = function (calEvent, newStart, newEnd) {
                    calEvent.startsAt = newStart;
                    calEvent.endsAt = newEnd;
                    calEvent.reservation.endDate = DateUtils.convertLocaleDateToServer(newEnd);
                    calEvent.reservation.startDate = DateUtils.convertLocaleDateToServer(newStart);

                    //TODO

                    Reservation.update(calEvent.reservation, function (result) {
                        //     //$scope.$emit('environmentreservationApp:reservationUpdate', result);
                        //     // console.log("success");
                        //     // console.log(result);
                    }, function (result) {
                        //     //console.log("fail");
                        //     //console.log(result);
                    });
                }

                ctrl.viewChangeClicked = function (nextView) { //this functions disables day view on the calendar
                    if (nextView === 'day') {
                        return false;
                    }
                };

            },
            controllerAs: 'ctrl',
            bindToController: true
        }
    }
    );
