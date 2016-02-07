'use strict';

angular.module('environmentreservationApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('dTAP', {
                parent: 'entity',
                url: '/dTAPs',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'DTAPs'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/dTAP/dTAPs.html',
                        controller: 'DTAPController'
                    }
                },
                resolve: {
                }
            })
            .state('dTAP.detail', {
                parent: 'entity',
                url: '/dTAP/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'DTAP'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/dTAP/dTAP-detail.html',
                        controller: 'DTAPDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'DTAP', function($stateParams, DTAP) {
                        return DTAP.get({id : $stateParams.id});
                    }]
                }
            })
            .state('dTAP.new', {
                parent: 'dTAP',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/dTAP/dTAP-dialog.html',
                        controller: 'DTAPDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    levelName: null,
                                    levelId: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('dTAP', null, { reload: true });
                    }, function() {
                        $state.go('dTAP');
                    })
                }]
            })
            .state('dTAP.edit', {
                parent: 'dTAP',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/dTAP/dTAP-dialog.html',
                        controller: 'DTAPDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['DTAP', function(DTAP) {
                                return DTAP.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('dTAP', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('dTAP.delete', {
                parent: 'dTAP',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/dTAP/dTAP-delete-dialog.html',
                        controller: 'DTAPDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['DTAP', function(DTAP) {
                                return DTAP.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('dTAP', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
