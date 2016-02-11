'use strict';

angular.module('environmentreservationApp')
.directive('rsrvStatusCommand',
    function(Reservation){
        return{
            restrict:'E',
            template:'<div class="btn-group flex-btn-group-container">'+
                                                  ' <button type="submit"'+
                                                          ' ng-click="ctrl.confirm(reservation.id)"'+
                                                          ' class="btn btn-success btn-sm">'+
                                                      ' <span class="glyphicon glyphicon-ok"></span>'+
                                                      ' <span class="hidden-xs hidden-sm"></span>'+
                                                  ' </button>'+
                                                 ' <button type="submit"'+
                                                          ' ng-click="ctrl.close(reservation.id)"'+
                                                          ' class="btn btn-danger btn-sm">'+
                                                      ' <span class="glyphicon glyphicon-remove-circle"></span>'+
                                                      ' <span class="hidden-xs hidden-sm"></span>'+
                                                  ' </button>'+
                                              ' </div>',

            scope={
                reservation: '=reservation'
            },
            controller:function(){
                var ctrl = this;

                ctrl.close = function(id){
                    Reservation.close({id:id},{}, onSuccess);
                }

                ctrl.confirm = function(id){
                    Reservation.confirm({id:id},{}, onSuccess);
                }


               function onSuccess(result){
                alert('ok');
               }


            },
            controllerAs:'ctrl'
        }
    }
);
