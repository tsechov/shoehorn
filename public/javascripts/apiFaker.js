'use strict';

(function (ng) {

    var customers = [
        {
            "_id": "13acbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "Aigner Katalin",
            "taxExemptNumber": "65789017-2-28",
            "lineOfBusiness": "Ã‰rtÃ©kesÃ­tÃ©s",
            "site": "sales",
            "typeOfCompanyId": "12ecbdf0500000e102795d33",
            "addresses": [
                {
                    "typeOfAddressId": '13ecbdf0500000e102795d34',
                    "country": "MagyarorszÃ¡g",
                    "district": "GyÅ‘r-Moson-Sopron",
                    "postalcode": 9024,
                    "city": "GyÅ‘r",
                    "address": "Babits u 3"
                },
                {
                    "typeOfAddressId": '23ecbdf0500000e102795d34',
                    "country": "MagyarorszÃ¡g",
                    "district": "GyÅ‘r-Moson-Sopron",
                    "postalcode": 9024,
                    "city": "GyÅ‘r",
                    "address": "Babits u 3"
                }
            ]
        },
        {
            "_id": "13bcbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "TÃ¶kmag",
            "taxExemptNumber": "65789017-2-28",
            "lineOfBusiness": "Ã‰rtÃ©kesÃ­tÃ©s",
            "site": "sales",
            "typeOfCompanyId": "12ecbdf0500000e102795d34",
            "addresses": [
                {
                    "typeOfAddressId": '13ecbdf0500000e102795d34',
                    "country": "MagyarorszÃ¡g",
                    "district": "GyÅ‘r-Moson-Sopron",
                    "postalcode": 9024,
                    "city": "GyÅ‘r",
                    "address": "Babits u 3"
                },
                {
                    "typeOfAddressId": '23ecbdf0500000e102795d34',
                    "country": "MagyarorszÃ¡g",
                    "district": "GyÅ‘r-Moson-Sopron",
                    "postalcode": 9024,
                    "city": "GyÅ‘r",
                    "address": "Babits u 3"
                }
            ]
        },
        {
            "_id": "13ccbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "CsibecipÅ‘ Katalin",
            "taxExemptNumber": "65789017-2-28",
            "lineOfBusiness": "Ã‰rtÃ©kesÃ­tÃ©s",
            "site": "sales",
            "typeOfCompanyId": "11ecbdf0500000e102795d34",
            "addresses": [
                {
                    "typeOfAddressId": '33ecbdf0500000e102795d34',
                    "country": "MagyarorszÃ¡g",
                    "district": "GyÅ‘r-Moson-Sopron",
                    "postalcode": 9024,
                    "city": "GyÅ‘r",
                    "address": "Babits u 3"
                },
                {
                    "typeOfAddressId": '43ecbdf0500000e102795d34',
                    "country": "MagyarorszÃ¡g",
                    "district": "GyÅ‘r-Moson-Sopron",
                    "postalcode": 9024,
                    "city": "GyÅ‘r",
                    "address": "Babits u 3"
                }
            ]
        }
    ];

    var shops = [
        {
            "_id": "23ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "Ã¼zlet neve",
            "status": "Ã¼zlet stÃ¡tusza",
            "address": {
                "typeOfAddressId": '13ecbdf0500000e102795d34',
                "country": "MagyarorszÃ¡g",
                "district": "GyÅ‘r-Moson-Sopron",
                "postalcode": 9024,
                "city": "GyÅ‘r",
                "address": "Babits u 3"
            }
        },
        {
            "_id": "24ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "Ã¼zlet neve2",
            "status": "Ã¼zlet stÃ¡tusza2",
            "address": {
                "typeOfAddressId": '13ecbdf0500000e102795d34',
                "country": "MagyarorszÃ¡g",
                "district": "GyÅ‘r-Moson-Sopron",
                "postalcode": 9024,
                "city": "GyÅ‘r",
                "address": "Babits u 3"
            }
        }
    ];

    var products = [
        {
            "_id": "53ecbedd1700004c00d115b1",
            "createdAt": "2014-08-14T13:51:25+0000",
            "lastModifiedAt": "2014-08-14T21:08:34+0000",
            "active": true,
            "description": "from runscope",
            "name": "name jool0",
            "itemNumber": "itemnumber001",
            "image": "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/1011-22432-full.jpg",
            "image-thumb": "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/1011-22432-thumb.jpg",
            "catalogs": [
                {
                    "catalogId": "53ecbdf0500000e102795d34",
                    "sizeGroups": ['53ecbdf0500000e102795d34', '53ecbdf0500000e102795d35']
                }
            ]
        },
        {
            "_id": "53ecc3f51800001600929c83",
            "createdAt": "2014-08-14T14:13:09+0000",
            "lastModifiedAt": "2014-08-14T21:08:34+0000",
            "active": true,
            "description": "from runscope",
            "name": "name jool1",
            "itemNumber": "itemnumber002",
            "image": "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/1095-52719-full.jpg",
            "image-thumb": "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/shoes/1095-52719-thumb.jpg",
            "catalogs": [
                {
                    "catalogId": "53ecbdf0500000e102795d34",
                    "sizeGroups": ['53ecbdf0500000e102795d34', '53ecbdf0500000e102795d35']
                }
            ]
        }
    ];

    var agents = [
        {
            "_id": "23ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "typeOfContact": "agent",
            "status": "szerepe",
            "title": "Dr.",
            "firstname": "1",
            "lastname": "ÃœgynÃ¶k"
        },
        {
            "_id": "23ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "typeOfContact": "agent",
            "status": "szerepe",
            "title": "Dr.",
            "firstname": "2",
            "lastname": "ÃœgynÃ¶k"
        }
    ];

    var places = [
        {
            "_id": "23ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "year": 2014,
            "season": "TÃ©l-Tavasz",
            "place": 1
        },
        {
            "_id": "23ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "year": 2014,
            "season": "TÃ©l-Tavasz",
            "place": 2
        },
        {
            "_id": "23ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "year": 2014,
            "season": "TÃ©l-Tavasz",
            "place": 3
        }
    ];

    var contacts = [
        {
            "_id": "23ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "typeOfContact": "KapcsolattartÃ³",
            "status": "boltvezetÅ‘",
            "title": "",
            "firstname": "Pista",
            "lastname": "BoltvezetÅ‘"
        },
        {
            "_id": "23ecbdf0501000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "typeOfContact": "KapcsolattartÃ³",
            "status": "boltvezetÅ‘",
            "title": "",
            "firstname": "MÃ¡rta",
            "lastname": "BoltvezetÅ‘nÃ©"
        }
    ];

    var creaditinformations = [
        {
            "_id": "23ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami"
        }
    ];

    var districts = [
        {
            "_id": "23ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "Keleti rÃ©giÃ³"
        }
    ];

    var discounts = [
        {
            "_id": "23ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami"
        }
    ];

    var payments = [
        {
            "_id": "23ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami"
        }
    ];

    var warehouses = [
        {
            "_id": "11ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "KÃ¼lsÅ‘ raktÃ¡r 1",
            "status": "bÃ©relt",
            "url": "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/warehouse2.jpg"
        },
        {
            "_id": "11ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "KÃ¼lsÅ‘ raktÃ¡r 2",
            "status": "bÃ©relt",
            "url": "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/warehouse1.png"
        },
        {
            "_id": "11ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "KÃ¼lsÅ‘ raktÃ¡r 3",
            "status": "bÃ©relt",
            "url": "https://dl.dropboxusercontent.com/u/14779005/szamos/szamos-frontend/warehouse2.jpg"
        }
    ];

    var phonenumbers = [
        {
            "_id": "23ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "typeOfPhoneNumber": "mobil",
            "country": "MagyarorszÃ¡g",
            "extension": "",
            "number": "+36 30 1234567"
        },
        {
            "_id": "23ecbdf0500000e102795d35",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "typeOfPhoneNumber": "vezetÃ©kes",
            "country": "MagyarorszÃ¡g",
            "extension": "",
            "number": "+36 52 123456"
        }
    ];

    var emails = [
        {
            "_id": "03ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "type": "privÃ¡t",
            "name": "anonym@email.com"
        },
        {
            "_id": "03ecbdf0500000e102795d35",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "type": "business",
            "name": "anonym@email.com"
        }
    ];

    var catalogs = [
        {
            "_id": "53ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "asdf",
            "year": 2014,
            "season": "asdf",
            "status": false,
            "webStatus": false
        },
        {
            "_id": "53ed0f431600005701d6d493",
            "createdAt": "2014-08-14T19:34:27+0000",
            "lastModifiedAt": "2014-08-14T19:34:27+0000",
            "active": false,
            "description": "description",
            "year": 2015,
            "season": "szezon",
            "status": false,
            "webStatus": false
        },
        {
            "_id": "53ed0f531600005601d6d494",
            "createdAt": "2014-08-14T19:34:43+0000",
            "lastModifiedAt": "2014-08-14T19:34:43+0000",
            "active": true,
            "description": "desc.",
            "year": 2013,
            "season": "season",
            "status": true,
            "webStatus": true
        }
    ];

    var orderedProduct = [
        {
            "_id": "53ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "asdf",
            "product": products[0],
            "quantity": 1,
            "size": 18
        },
        {
            "_id": "53ecbdf0500000e102795d33",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "asdf",
            "product": products[1],
            "quantity": 2,
            "size": 19
        },
        {
            "_id": "53ecbdf0500000e102795d32",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "asdf",
            "product": products[1],
            "quantity": 3,
            "size": 20
        }
    ]

    var basket = [
        orderedProduct[0],
        orderedProduct[1],
        orderedProduct[2]];

    var order = [
        {
            "_id": "53ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "asdf",
            "ordernumber": "on201403210001",
            "originatorId": "23ecbdf0500000e102795d34",
            "customerId": "23ecbdf0500000e102795d34",
            "invoiceAddress": customers[0].addresses[0],
            "shipmentAddress": customers[0].addresses[1],
            "deadlines": [
                {
                    "deadlineTypeId": "00ecbdf0500000e102795d34",
                    "date": "2014-08-14T13:51:25+0000"
                },
                {
                    "deadlineTypeId": "01ecbdf0500000e102795d34",
                    "date": "2014-08-14T13:51:25+0000"
                }
            ],
            "total": 36120,
            "orderedProducts": [
                orderedProduct[0],
                orderedProduct[1],
                orderedProduct[2]]
        }
    ];

    /*************************************** Konstansok ***********************************************/

    var typeofcompanies = [
        {
            "_id": "11ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "KFT"
        },
        {
            "_id": "12ecbdf0500000e102795d33",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "BT"
        }
    ];

    var typeofaddresses = [
        {
            "_id": "13ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "SzÃ¡llÃ­tÃ¡si cÃ­m"
        },
        {
            "_id": "23ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "SzÃ¡mlÃ¡zÃ¡si cÃ­m"
        },
        {
            "_id": "33ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "SzÃ©khely cÃ­me"
        },
        {
            "_id": "43ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "PostÃ¡zÃ¡si cÃ­m"
        }
    ];

    var groups = [
        {
            "_id": "23ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami"
        }

    ];

    var groups = [
        {
            "_id": "00ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "ZÃ¡rt cipÅ‘k szÃ¡llÃ­tÃ¡sa"
        },
        {
            "_id": "01ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:47:28+0000",
            "lastModifiedAt": "2014-08-14T14:36:49+0000",
            "active": false,
            "description": "valami",
            "name": "SzandÃ¡l, szandÃ¡l cipÅ‘"
        }
    ];

    var sizeGroups = [
        {
            "_id": "53ecbdf0500000e102795d34",
            "createdAt": "2014-08-14T13:51:25+0000",
            "lastModifiedAt": "2014-08-14T13:51:25+0000",
            "active": true,
            "description": "first",
            "from": "18",
            "to": "24"
        },
        {
            "_id": "53ecbdf0500000e102795d35",
            "createdAt": "2014-08-14T13:51:25+0000",
            "lastModifiedAt": "2014-08-14T13:51:25+0000",
            "active": true,
            "description": "first",
            "from": "25",
            "to": "30"
        },
        {
            "_id": "53ecbdf0500000e102795d36",
            "from": "31",
            "to": "35",
            "createdAt": "2014-08-14T13:51:25+0000",
            "lastModifiedAt": "2014-08-14T13:51:25+0000",
            "active": true,
            "description": "first"
        },
        {
            "_id": "53ecbdf0500000e102795d37",
            "from": "36",
            "to": "40",
            "createdAt": "2014-08-14T13:51:25+0000",
            "lastModifiedAt": "2014-08-14T13:51:25+0000",
            "active": true,
            "description": "first"
        }
    ];


    ng.module('szamoskolyok')
        .config(['$provide', function ($provide) {
            $provide.decorator('$httpBackend', ng.mock.e2e.$httpBackendDecorator);
        }]).run(['$httpBackend', function ($httpBackend) {
            /*************************************** the sor of regexp important: get all, get query, get by id ***********************************************/

            /*************************************** Customers ***********************************************/
            $httpBackend.whenGET('http://staging-shoehorn-backend.herokuapp.com/customers').respond(customers);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/customers/.*')).respond(customers[0]);
            $httpBackend.whenPUT(new RegExp('http://staging-shoehorn-backend.herokuapp.com/customers/.*')).respond(function (method, url, data, headers) {
                //console.log('Received these data for put:', method, url, data, headers);
                //TODO: save the changes
                var newCustomer = angular.fromJson(data);
                angular.forEach(customers, function (customer) {
                    if (newCustomer._id == customer._id) {
                        var index = customers.indexOf(customer);
                        customers.splice(index, 1);
                        //var newId = Math.random() * 10000 + 'abcdefg';
                        //newCustomer._id = newId;
                        customers.push(newCustomer);
                    }
                });
                return [200, {}, {}];
            });

            $httpBackend.whenPOST('http://staging-shoehorn-backend.herokuapp.com/customers').respond(function (method, url, data, headers) {
                console.log('Received these data for post:', method, url, data, headers);
                return [201, {}, {
                    "Location": "http://staging-shoehorn-backend.herokuapp.com/catalogs/53ee61c1a70000bc0078f306"
                }];
            });

            /*************************************** Products *********************************************/
            $httpBackend.whenGET('http://staging-shoehorn-backend.herokuapp.com/products').respond(products);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/products/.*')).respond(products[0]);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/products?catalogId=.*')).respond(products);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/products?.*')).respond(products);

            /*************************************** OrderedProduct *********************************************/
            $httpBackend.whenGET('http://staging-shoehorn-backend.herokuapp.com/orderedproducts').respond(basket);
            //$httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/products/.*')).respond(products[0]);
            //$httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/products?catalogId=.*')).respond(products);
            //$httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/products?.*')).respond(products);
            $httpBackend.whenPOST('http://staging-shoehorn-backend.herokuapp.com/orderedproducts').respond(function (method, url, data, headers) {
                console.log('Received these data for post:', method, url, data, headers);
                basket.push(angular.fromJson(data));
                return [201, {}, {
                    "Location": "http://staging-shoehorn-backend.herokuapp.com/orderedproducts/53ee61c1a70000bc0078f306"
                }];
            });

            /*************************************** typeOfCompany ***********************************************/
            $httpBackend.whenGET('http://staging-shoehorn-backend.herokuapp.com/typeofcompanies').respond(typeofcompanies);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/typeofcompanies/.*')).respond(typeofcompanies[0]);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/typeofcompanies?.*')).respond(typeofcompanies);

            /*************************************** typeOfAddress ***********************************************/
            $httpBackend.whenGET('http://staging-shoehorn-backend.herokuapp.com/typeofaddresses').respond(typeofaddresses);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/typeofaddresses/.*')).respond(typeofaddresses[0]);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/typeofaddresses?.*')).respond(typeofaddresses);

            /*************************************** warehouse ***********************************************/
            $httpBackend.whenGET('http://staging-shoehorn-backend.herokuapp.com/warehouses').respond(warehouses);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/warehouses/.*')).respond(warehouses[0]);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/warehouses?.*')).respond(warehouses);

            /*************************************** phonenumber ***********************************************/
            $httpBackend.whenGET('http://staging-shoehorn-backend.herokuapp.com/phonenumbers').respond(phonenumbers);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/phonenumbers/.*')).respond(phonenumbers[0]);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/phonenumbers?.*')).respond(phonenumbers);

            /*************************************** Contact ***********************************************/
            $httpBackend.whenGET('http://staging-shoehorn-backend.herokuapp.com/contacts').respond(contacts);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/contacts/.*')).respond(contacts[0]);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/contacts?.*')).respond(contacts);

            /*************************************** Agent ***********************************************/
            $httpBackend.whenGET('http://staging-shoehorn-backend.herokuapp.com/agents').respond(agents);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/agents/.*')).respond(agents[0]);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/agents?.*')).respond(agents);

            /*************************************** Shops ***********************************************/
            $httpBackend.whenGET('http://staging-shoehorn-backend.herokuapp.com/shops').respond(shops);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/shops/.*')).respond(shops[0]);
            $httpBackend.whenGET(new RegExp('http://staging-shoehorn-backend.herokuapp.com/shops?.*')).respond(shops);


            /*************************************** SizeGroups ***********************************************/
            $httpBackend.whenGET('http://staging-shoehorn-backend.herokuapp.com/sizegroups').respond(sizeGroups);

            /*************************************** Catalogs ***********************************************/
            //$httpBackend.whenGET('http://staging-shoehorn-backend.herokuapp.com/catalogs').respond(catalogs);
            /*$httpBackend.whenPOST('http://staging-shoehorn-backend.herokuapp.com/catalogs').respond(function(method, url, data, headers) {
             console.log('Received these data:', method, url, data, headers);
             catalogs.push(angular.fromJson(data));
             return [201, {}, {"Location": "http://staging-shoehorn-backend.herokuapp.com/catalogs/53ee61c1a70000bc0078f306"}];
             });*/

            /*************************************** pass through for all other requests *********************************************/
            $httpBackend.whenGET(/.*/).passThrough();
            $httpBackend.whenPOST(/.*/).passThrough();
            $httpBackend.whenDELETE(/.*/).passThrough();
            $httpBackend.whenPUT(/.*/).passThrough();
        }]);
})(angular);