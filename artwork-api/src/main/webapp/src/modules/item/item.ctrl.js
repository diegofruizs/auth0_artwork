/*
The MIT License (MIT)

Copyright (c) 2015 Los Andes University

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
(function (ng) {
    var mod = ng.module('itemModule');

    mod.controller('itemCtrl', ['$scope', 'model','Restangular', 'references',
        function ($scope, model,Restangular, references) {
            $scope.references = references;
            $scope.model = model;
            //Alertas
            $scope.alerts = [];
            this.closeAlert = function (index) {
                $scope.alerts.splice(index, 1);
            };

            Restangular.setErrorInterceptor(function(response, deferred, responseHandler) {
                switch(response.status) {
                     case 400:
                        $scope.showError(response.data);
                        return false; 
                        break;
                    case 401:
                        $scope.showError(response.data);
                        return false; 
                        break;
                    case 403:
                        $scope.showError(response.data);
                        return false; 
                        break;
                    case 404:
                        $scope.showError(response.data);
                        return false; 
                        break;
                    case 405:
                        $scope.showError(response.data);
                        return false; 
                        break;
                    case 413:
                        $scope.showError(response.data);
                        return false; 
                        break;
                    case 412:
                        $scope.showError(response.data);
                        return false; 
                        break;       
                    case 500:
                        $scope.showError(response.data);
                        return false; 
                        break;
                    case 503:
                        $scope.showError(response.data);
                        return false; 
                        break;  
                }

                return true; // error not handled
            });

            /* Función showMessage: Recibe el mensaje en String y
             * su tipo con el fin de almacenarlo en el array $scope.alerts.
             */
            function showMessage(msg, type) {
                var types = ["info", "danger", "warning", "success"];
                if (types.some(function (rc) {
                    return type === rc;
                })) {
                    $scope.alerts.push({type: type, msg: msg});
                }
            }

            $scope.showError = function (msg) {
                showMessage(msg, "danger");
            };

            $scope.showSuccess = function (msg) {
                showMessage(msg, "success");
            };
        }]);

})(window.angular);
