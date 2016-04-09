'use strict';

angular.module('environmentreservationApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('reservation', {
                parent: 'entity',
                url: '/reservations',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Reservations'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/reservation/reservations.html',
                        controller: 'ReservationController'
                    }
                },
                resolve: {
                }
            })
          .state('reservation.detail', {
                parent: 'entity',
                url: '/reservation/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Reservation'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/reservation/reservation-detail.html',
                        controller: 'ReservationDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Reservation', function($stateParams, Reservation) {
                        return Reservation.get({id : $stateParams.id});
                    }]
                }
            })
            .state('reservation.new', {
                parent: 'reservation',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/reservation/reservation-dialog.html',
                        controller: 'ReservationDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    startDate: null,
                                    endDate: null,
                                    project: null,
                                    status: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('reservation', null, { reload: true });
                    }, function() {
                        $state.go('reservation');
                    })
                }]
            })
            .state('reservation.edit', {
                parent: 'reservation',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/reservation/reservation-dialog.html',
                        controller: 'ReservationDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Reservation', function(Reservation) {
                                return Reservation.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('reservation', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('reservation.delete', {
                parent: 'reservation',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/reservation/reservation-delete-dialog.html',
                        controller: 'ReservationDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Reservation', function(Reservation) {
                                return Reservation.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('reservation', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
