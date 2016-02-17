'use strict';

angular.module('environmentreservationApp')
.directive('rsrvStatusCommand',
    function(){
        return{
            restrict:'E',
            template:'<div class="btn-group flex-btn-group-container">'+
                                                  ' <button ng-show="ctrl.canConfirm()" type="submit"'+
                                                          ' ng-click="ctrl.confirm()"'+
                                                          ' class="btn btn-success btn-sm">'+
                                                      ' <span class="glyphicon glyphicon-ok"></span>'+
                                                      ' <span class="hidden-xs hidden-sm"></span>'+
                                                  ' </button>'+
                                                 ' <button ng-show="ctrl.canClose()" type="submit"'+
                                                          ' ng-click="ctrl.close(id)"'+
                                                          ' class="btn btn-danger btn-sm">'+
                                                      ' <span class="glyphicon glyphicon-remove-circle"></span>'+
                                                      ' <span class="hidden-xs hidden-sm"></span>'+
                                                  ' </button>'+
                                              ' </div>',

            scope:{
                reservation: '=reservation'
            },
            controller:["$scope","Reservation",function($scope,Reservation){
                var ctrl = this;
                ctrl.close = function(){
                    Reservation.close({id:ctrl.reservation.id},{}, onSuccess(ctrl.reservation, 'CLOSED'));
                }

                ctrl.confirm = function(){
                    Reservation.confirm({id:ctrl.reservation.id},{}, onSuccess(ctrl.reservation, 'CONFIRMED'));
                }

                ctrl.canConfirm = function(){
                    return ctrl.reservation.confirmAllowed;
                }

                ctrl.canClose = function(){
                    return ctrl.reservation.closeAllowed;
                }

               function onSuccess(reservation,status){
                    return function(result){
                        $scope.$emit('reservation.status.change',{reservation:reservation,status:status});
                    }
               }
            }],
            controllerAs:'ctrl',
            bindToController: true
        }
    }
)
.directive('rsrvStatus',
    function(){
        return{
            restrict:'E',
            scope:{
                reservation: '=reservation'
            },
            template:'<span class="label {{ctrl.getClass()}}">{{ctrl.reservation.status}}</span>',
            controller:function(){
                var ctrl = this;

                ctrl.getClass = function(){
                    var mapping = {
                        'CONFIRMED':'label-success',
                        'CLOSED':'label-default',
                        'CONFLICT':'label-danger'
                    };
                    return mapping[ctrl.reservation.status];
                }
            },
            controllerAs:'ctrl',
            bindToController: true
        }
    }
);
