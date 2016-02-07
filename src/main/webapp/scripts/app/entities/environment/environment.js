'use strict';

angular.module('environmentreservationApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('environment', {
                parent: 'entity',
                url: '/environments',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Environments'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/environment/environments.html',
                        controller: 'EnvironmentController'
                    }
                },
                resolve: {
                }
            })
            .state('environment.detail', {
                parent: 'entity',
                url: '/environment/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Environment'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/environment/environment-detail.html',
                        controller: 'EnvironmentDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Environment', function($stateParams, Environment) {
                        return Environment.get({id : $stateParams.id});
                    }]
                }
            })
            .state('environment.new', {
                parent: 'environment',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/environment/environment-dialog.html',
                        controller: 'EnvironmentDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    environmentName: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('environment', null, { reload: true });
                    }, function() {
                        $state.go('environment');
                    })
                }]
            })
            .state('environment.edit', {
                parent: 'environment',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/environment/environment-dialog.html',
                        controller: 'EnvironmentDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Environment', function(Environment) {
                                return Environment.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('environment', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('environment.delete', {
                parent: 'environment',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/environment/environment-delete-dialog.html',
                        controller: 'EnvironmentDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Environment', function(Environment) {
                                return Environment.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('environment', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
