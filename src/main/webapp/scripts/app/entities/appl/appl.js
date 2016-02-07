'use strict';

angular.module('environmentreservationApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('appl', {
                parent: 'entity',
                url: '/appls',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Appls'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/appl/appls.html',
                        controller: 'ApplController'
                    }
                },
                resolve: {
                }
            })
            .state('appl.detail', {
                parent: 'entity',
                url: '/appl/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Appl'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/appl/appl-detail.html',
                        controller: 'ApplDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Appl', function($stateParams, Appl) {
                        return Appl.get({id : $stateParams.id});
                    }]
                }
            })
            .state('appl.new', {
                parent: 'appl',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/appl/appl-dialog.html',
                        controller: 'ApplDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    applName: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('appl', null, { reload: true });
                    }, function() {
                        $state.go('appl');
                    })
                }]
            })
            .state('appl.edit', {
                parent: 'appl',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/appl/appl-dialog.html',
                        controller: 'ApplDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Appl', function(Appl) {
                                return Appl.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('appl', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('appl.delete', {
                parent: 'appl',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/appl/appl-delete-dialog.html',
                        controller: 'ApplDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Appl', function(Appl) {
                                return Appl.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('appl', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
