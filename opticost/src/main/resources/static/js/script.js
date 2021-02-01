var addedCities = [];
var addedRoads = [];
var plans = [];
var selectedPlan = {};
var s = null;
var graph = {};
var cityCnt = 0;
var roadCnt = 0;
var citiesSaved = true;

$(document).ready(function () {
    checkWidth(true);
    $(window).resize(function () {
        checkWidth(false);
    });
});

init();

function checkWidth() {

    if ($(window).width() < 1150) {
        $('#form-container').removeClass('col-md-4');
        $('#form-container').addClass('col-md-12');
        $('#draw-container').removeClass('col-md-8');
        $('#draw-container').addClass('col-md-12');
    } else {
        $('#form-container').removeClass('col-md-12');
        $('#form-container').addClass('col-md-4');
        $('#draw-container').removeClass('col-md-12');
        $('#draw-container').addClass('col-md-8');
    }
}

function init() {
    $("#verticesEdges").hide();
    $("#sigma-container").css("height", $(document).height() * 0.9);
    $("#addCityBtn").on('click', addCity);
    $("#saveCities").on('click', saveCities);
    $("#addRoad").on('click', addRoad);
    $("#saveRoad").on('click', saveRoads);
    $("#processFile").on('click', processFile);
    $("#fromCity").on('click', noCitiesDropDown);
    $("#toCity").on('click', noCitiesDropDown);
    $("#runMulticost").on('click', runMulticost);
    $("#savePlanName").on('click', savePlan);
    showPlans();
    $('#plan-name').change(onPlanChange);

    // Initialise sigma:
    s = new sigma(
        {
            renderer: {
                container: document.getElementById('sigma-container'),
                type: 'canvas'
            },
            settings: sigmaSettings()
        }
    );
}

// API connectors
function runMulticost() {
    let fromCity = $("#fromCityRun").val();
    let toCity = $("#toCityRun").val();
    let cargo = $("#cargo").val();

    if (fromCity === null ||
        toCity === null ||
        cargo === "") {
        $.notify({
            message: "Please fill all fields in order to run the algorithm"
        }, notifySettings('danger'));
    } else {

        let data = {
            selectedPlan: selectedPlan.planName,
            fromCity: fromCity,
            toCity: toCity,
            cargo: cargo
        };

        $.ajax({
            type: "POST",
            url: "/run",
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                console.log("Success", data);
                drawRoadsAndCities(data, true)
            },
            error: function (data) {
                if (data.responseJSON.trace.includes('NoFeasibleSolutionException')) {
                    $("#min-cost-result").html(data.responseJSON.message.split('.').join('</br>'));
                    $.notify({
                        message: data.responseJSON.message
                    }, notifySettings('danger'));
                } else {
                    $.notify({
                        message: "Something went wrong can't run the algorithm"
                    }, notifySettings('danger'));
                }
                console.log("Error", data);
            }
        });
    }
}

function processFile() {
    let file = $("#graphFile").prop('files')[0];

    if (file) {
        let formData = new FormData();
        formData.append('file', file);
        $.ajax({
            url: '/process-file',
            type: "POST",
            data: formData,
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            success: function (data) {
                selectedPlan = data;
                plans.push(data);
                onPlanChange(true);
                $.notify({
                    message: "Input processed successfully."
                }, notifySettings('success'));
            },
            error: function (data) {
                $.notify({
                    message: "File upload failed ..."
                }, notifySettings('danger'));
            }
        });
    }
}

function showPlans() {
    $.ajax({
        type: "GET",
        url: "/plans",
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            console.log("Success", data);
            plans = data;
            for (let i = 0; i < data.length; i++) {
                $('#plan-name').append($('<option>', {value: data[i].planName, text: data[i].planName}));
            }
        },
        error: function (data) {
            $.notify({
                message: "Something went wrong can't retrieve plans"
            }, notifySettings('danger'));
            console.log("Error", data);
        }
    });
}

function savePlan() {
    let inputPlanName = $("#planName").val();

    if (inputPlanName !== null && inputPlanName !== "") {
        plan = {
            planName: inputPlanName,
            roads: []
        };
        $.ajax({
            type: "POST",
            url: "/save-plan",
            data: JSON.stringify(plan),
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                console.log("Success", data);
                $("#verticesEdges").show();
                selectedPlan = data;
                $(".plan-input").hide();
                $("#showPlan").html(
                    "<tr id='selectedPlan'>" +
                    drawPlanRow(data.planName) +
                    "</tr>"
                );

                $('#plan-name').empty();
                $('#plan-name').append($('<option>', {value: 'new', text: 'New Plan'}));
                showPlans();
                $(".editPlan").on('click', editPlanRow);
                $(".removePlan").on('click', removePlanRow);
            },
            error: function (data) {
                $.notify({
                    message: "Something went wrong can't save the plan"
                }, notifySettings('danger'));
                console.log("Error", data);
            }
        });
    } else {
        $.notify({
            message: "There is no name for the plan"
        }, notifySettings('danger'));
    }
}

function removePlanRow() {
    $.ajax({
        type: "DELETE",
        url: "/delete-plan/" + selectedPlan.id,
        contentType: "application/json; charset=utf-8",
        success: function () {
            location.reload();
        },
        error: function (data) {
            $.notify({
                message: "Something went wrong can't delete the plan row"
            }, notifySettings('danger'));
            console.log("Error", data);
        }
    });
}

function saveCities() {
    console.log(addedCities);
    let data = addedCities.filter(x => x.deleted === false);
    $.ajax({
        type: "POST",
        url: "/save-cities",
        data: JSON.stringify(data),
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            console.log("The cities were successfully saved", data);
            citiesSaved = true;
            drawCities(data);
            if (addedRoads.length > 0) {
                saveRoads(true);
            }
            $.notify({
                message: "The cities were successfully saved"
            }, notifySettings('success'));
        },
        error: function (data) {
            if (data.responseJSON.trace.includes('CitiesWithTheSameNameException') ||
                data.responseJSON.trace.includes('CitiesWithTheSameCoordinatesException')) {
                $.notify({
                    message: data.responseJSON.message
                }, notifySettings('danger'));
            } else {
                $.notify({
                    message: "Something went wrong can't run the algorithm"
                }, notifySettings('danger'));
            }
        }
    });
}

function saveRoads(updateRoadsTable) {
    if (citiesSaved === true) {
        console.log(addedRoads);
        let data = addedRoads.filter(x => x.deleted === false);
        console.log(data)
        $.ajax({
            type: "POST",
            url: "/save-roads/" + selectedPlan.id,
            data: JSON.stringify(data),
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                console.log("The roads were successfully saved", data);
                drawRoads(data);
                if (updateRoadsTable === true) {
                    persistRoadTable(data);
                }
                $.notify({
                    message: "The roads were successfully saved"
                }, notifySettings('success'));
            },
            error: function (data) {
                console.log("There is a problem can't save the roads", data);
                $.notify({
                    message: "There is a problem can't save the roads"
                }, notifySettings('danger'));
            }
        });
    } else {
        $.notify({
            message: "Please save the changes of the cities first"
        }, notifySettings('warning'));
    }
}
// End of API connectors

// Inline editors
function editCityRow() {
    citiesSaved = false;
    let element = $(this).parent().parent();
    let index = parseInt(element.get(0).id.split("-")[1], 10);
    element.children().eq(0).html("<input type='text' id='cityName-" + index + "' class='form-control' value='" + addedCities[index].cityName + "' readonly>");
    element.children().eq(1).html("<input type='number' id='xCoord-" + index + "' class='form-control' value='" + addedCities[index].xCoord + "'>");
    element.children().eq(2).html("<input type='number' id='yCoord-" + index + "' class='form-control' value='" + addedCities[index].yCoord + "'>");
    element.children().eq(3).html(
        "<button type='button' id='saveCity-" + index + "' class='btn btn-success btn-sm'>" +
        "<span class='glyphicon glyphicon-floppy-saved'></span>" +
        "</button>"
    );

    $("#saveCity-" + index).on('click', function () {
        let cityName = $("#cityName-" + index).val();
        let xCoord = parseInt($("#xCoord-" + index).val(), 10);
        let yCoord = parseInt($("#yCoord-" + index).val(), 10);

        if (cityName === '' || isNaN(xCoord) || isNaN(yCoord)) {
            $.notify({
                // options
                message: "Input fields for city can't be empty, please enter values"
            }, notifySettings('danger'));

            return
        }

        if (validateCity(cityName, xCoord, yCoord, index)) {

            addedCities[index].cityName = cityName;
            addedCities[index].xCoord = xCoord;
            addedCities[index].yCoord = yCoord;

            updateCitiesDropDown("#fromCity");
            updateCitiesDropDown("#toCity");
            updateCitiesDropDown("#fromCityRun");
            updateCitiesDropDown("#toCityRun");
            // Unbind the events before removing the element in order to avoid replication of event listeners
            $(".editCity").off();
            element.empty();
            element.html(drawCityRow(cityName, xCoord, yCoord));

            $(".editCity").on('click', editCityRow);
            $(".removeCity").on('click', removeCityRow);
            $(this).off();
        } else {
            $.notify({
                // options
                message: "Can't add the same city or different city with the same coordinates"
            }, notifySettings('danger'));
            citiesSaved = true;
            // Unbind the events before removing the element in order to avoid replication of event listeners
            $(".editCity").off();
            element.empty();
            element.html(drawCityRow(addedCities[index].cityName, addedCities[index].xCoord, addedCities[index].yCoord));

            $(".editCity").on('click', editCityRow);
            $(".removeCity").on('click', removeCityRow);
            $(this).off();
        }
    });

}

function editRoadRow() {
    let element = $(this).parent().parent();
    let index = parseInt(element.get(0).id.split("-")[1], 10);

    let valBeforeEditFrom = element.children().eq(0).text();
    element.children().eq(0).html("<select class='form-control' id='fromCity-" + index + "'></select>");
    updateCitiesDropDown("#fromCity-" + index, valBeforeEditFrom);

    let valBeforeEditTo = element.children().eq(1).text();
    element.children().eq(1).html("<select class='form-control' id='toCity-" + index + "'></select>");
    updateCitiesDropDown("#toCity-" + index, valBeforeEditTo);

    element.children().eq(2).html("<input type='number' min='0' id='capacity-" + index + "' class='form-control' value='" + addedRoads[index].capacity + "'>");
    element.children().eq(3).html("<input type='number' min='0' id='price-" + index + "' class='form-control' value='" + addedRoads[index].price + "'>");
    element.children().eq(4).html(
        "<button type='button' id='saveRoad-" + index + "' class='btn btn-success btn-sm'>" +
        "<span class='glyphicon glyphicon-floppy-saved'></span>" +
        "</button>"
    );

    $("#saveRoad-" + index).on('click', function () {
        let fromCity = $("#fromCity-" + index).val();
        let toCity = $("#toCity-" + index).val();
        let cap = parseInt($("#capacity-" + index).val(), 10);
        let price = parseFloat($("#price-" + index).val());

        if (fromCity === '' || toCity === '' || isNaN(cap) || isNaN(price)) {
            $.notify({
                // options
                message: "Input fields for edge can't be empty, please enter values"
            }, notifySettings('danger'));

            return
        }

        if (validateRoad(fromCity, toCity, index)) {

            addedRoads[index].fromCity = fromCity;
            addedRoads[index].toCity = toCity;
            addedRoads[index].capacity = cap;
            addedRoads[index].price = price;

            updateCitiesDropDown("#fromCityRun");
            updateCitiesDropDown("#toCityRun");

            // Unbind the events before removing the element in order to avoid replication of event listeners
            $(".editRoad").off();
            element.empty();
            element.html(drawRoadRow(fromCity, toCity, cap, price));

            $(".editRoad").on('click', editRoadRow);
            $(".removeRoad").on('click', removeRoadRow);
            $(this).off();
        } else {
            $.notify({
                // options
                message: "The road already exists. Please update the existing one."
            }, notifySettings('danger'));

            // Unbind the events before removing the element in order to avoid replication of event listeners
            $(".editRoad").off();
            element.empty();
            element.html(drawRoadRow(addedRoads[index].fromCity, addedRoads[index].toCity, addedRoads[index].capacity, addedRoads[index].price));

            $(".editRoad").on('click', editRoadRow);
            $(".removeRoad").on('click', removeRoadRow);
            $(this).off();
        }
    });
}

function editPlanRow() {
    let element = $("#selectedPlan");
    let index = element.get(0).id.split("-")[1];

    element.children().eq(0).html("<input type='text' id='editPlanName' class='form-control' value='" + selectedPlan.planName + "'>");
    element.children().eq(1).html(
        "<button type='button' id='saveEditPlanName' class='btn btn-success btn-sm'>" +
        "<span class='glyphicon glyphicon-floppy-saved'></span>" +
        "</button>"
    );

    $("#saveEditPlanName").on('click', function () {
        let planName = $("#editPlanName").val();

        if (planName.length > 0) {
            selectedPlan.planName = planName;

            let requestPlan = $.extend(true, {}, selectedPlan);

            for (let i = 0; i < requestPlan.roads.length; i++) {
                requestPlan.roads[i].fromCity = selectedPlan.roads[i].fromCity.cityName;
                requestPlan.roads[i].toCity = selectedPlan.roads[i].toCity.cityName;
                delete requestPlan.roads[i].id;
            }

            $.ajax({
                type: "PUT",
                url: "/update-plan",
                data: JSON.stringify(requestPlan),
                contentType: "application/json; charset=utf-8",
                success: function (data) {
                    console.log("Success", data);
                    // Unbind the events before removing the element in order to avoid replication of event listeners
                    $(".editPlan").off();
                    element.empty();
                    element.html(drawPlanRow(planName));

                    $(".editPlan").on('click', editPlanRow);
                    $(".removePlan").on('click', removePlanRow);
                    $(this).off();
                },
                error: function (data) {
                    console.log("Error", data);
                    $.notify({
                        message: "Something went wrong, can't update plan"
                    }, notifySettings('danger'));

                    $(".editPlan").off();
                    element.empty();
                    element.html(drawPlanRow(planName));

                    $(".editPlan").on('click', editPlanRow);
                    $(".removePlan").on('click', removePlanRow);
                    $(this).off();

                }
            });
        } else {
            $.notify({
                // options
                message: "Plan name should not be empty"
            }, notifySettings('danger'));

            $(".editPlan").off();
            element.empty();
            element.html(drawPlanRow(selectedPlan.planName));

            $(".editPlan").on('click', editPlanRow);
            $(".removePlan").on('click', removePlanRow);
            $(this).off();
        }
    });
}

function removeCityRow() {
    citiesSaved = false;
    let element = $(this).parent().parent();
    let index = element.get(0).id.split("-")[1];
    addedCities[index].deleted = true;
    addedRoads.forEach(function (x) {
        if (x.fromCity === addedCities[index].cityName || x.toCity === addedCities[index].cityName) {
            x.deleted = true;
        }
    });

    // Unbind the events before removing the element in order to avoid replication of event listeners
    $(".removeCity").off();
    element.remove();
    $(".removeCity").on('click', removeCityRow);
    updateCitiesDropDown("#fromCity");
    updateCitiesDropDown("#toCity");
    updateCitiesDropDown("#fromCityRun");
    updateCitiesDropDown("#toCityRun");
}

function removeRoadRow() {
    let element = $(this).parent().parent();
    let index = element.get(0).id.split("-")[1];
    addedRoads[index].deleted = true;
    // Unbind the events before removing the element in order to avoid replication of event listeners
    $(".removeRoad").off();
    element.remove();
    $(".removeRoad").on('click', removeRoadRow);
}
// End of Inline editors


// Dynamic Helpers
function addCity(cityData) {
    let cityName = null;
    let xCoord = null;
    let yCoord = null;
    let showTable = $("#showCities");

    // Check if the function was called from event handler (UI button)
    if (cityData.target) {
        citiesSaved = false;
        cityName = $("#inputCityName").val();
        xCoord = parseInt($("#inputX").val(), 10);
        yCoord = parseInt($("#inputY").val(), 10);

        if (cityName === '' || isNaN(xCoord) || isNaN(yCoord)) {
            $.notify({
                // options
                message: "Input fields for city can't be empty, please enter values"
            }, notifySettings('danger'));

            return
        }
    } else {
        cityName = cityData.cityName;
        xCoord = cityData.xCoord;
        yCoord = cityData.yCoord;
    }

    let value = {
        'cityName': cityName,
        'xCoord': xCoord,
        'yCoord': yCoord,
        'deleted': false
    };

    if (addedCities.filter(x => x.cityName === value.cityName && x.deleted === value.deleted).length === 0 &&
        addedCities.filter(x => x.xCoord === value.xCoord && x.yCoord === value.yCoord && x.deleted === value.deleted).length === 0) {

        let previousHtml = showTable.html();
        showTable.html(previousHtml +
            "<tr id='city-" + cityCnt + "'>" +
            drawCityRow(cityName, xCoord, yCoord) +
            "</tr>"
        );
        $(".editCity").on('click', editCityRow);
        $(".removeCity").on('click', removeCityRow);
        cityCnt = cityCnt + 1;
        addedCities.push(value);
        updateCitiesDropDown("#fromCity");
        updateCitiesDropDown("#toCity");
        updateCitiesDropDown("#fromCityRun");
        updateCitiesDropDown("#toCityRun");
    } else {
        $.notify({
            // options
            message: "Can't add the same city or different city with the same coordinates"
        }, notifySettings('danger'));
        citiesSaved = true;
    }
}

function addRoad(roadData) {
    let fromCity = null;
    let toCity = null;
    let cap = null;
    let price = null;
    let showTable = $("#showRoads");

    // Check if the function was called from event handler (UI button)
    if (roadData.target) {
        fromCity = $("#fromCity").val();
        toCity = $("#toCity").val();
        cap = parseInt($("#inputCap").val());
        price = parseFloat($("#inputPrice").val());

        if (fromCity === '' || toCity === '' || isNaN(cap) || isNaN(price)) {
            $.notify({
                // options
                message: "Input fields for edge can't be empty, please enter values"
            }, notifySettings('danger'));

            return
        }
    } else {
        fromCity = roadData.fromCity.cityName;
        toCity = roadData.toCity.cityName;
        cap = roadData.capacity;
        price = roadData.price;
    }

    if (addedRoads.filter(x => x.fromCity === fromCity && x.toCity === toCity && x.deleted === false).length === 0) {
        let previousHtml = showTable.html();
        showTable.html(
            previousHtml +
            "<tr id='road-" + roadCnt + "'>" +
            drawRoadRow(fromCity, toCity, cap, price) +
            "</tr>"
        );
        $(".editRoad").on('click', editRoadRow);
        $(".removeRoad").on('click', removeRoadRow);
        roadCnt = roadCnt + 1;
        addedRoads.push(
            {
                'fromCity': fromCity,
                'toCity': toCity,
                'capacity': cap,
                'price': price,
                'deleted': false,
                'planName': selectedPlan.planName
            }
        );
    } else {
        $.notify({
            // options
            message: "The road already exists. Please update the existing one."
        }, notifySettings('danger'));
        citiesSaved = true;
    }
}

function onPlanChange(fromFile) {
    let selectedPlanName = null;
    if (fromFile.target) {
        selectedPlanName = $('#plan-name').val();
        $("#showPlan").html(
            "<tr id='selectedPlan'>" +
            drawPlanRow(selectedPlanName) +
            "</tr>"
        );

        $(".editPlan").on('click', editPlanRow);
        $(".removePlan").on('click', removePlanRow);
    } else {
        selectedPlanName = '';
    }

    if (selectedPlan.planName !== selectedPlanName) {
        s.graph.clear();
        s.refresh();
        $('#showCities').empty();
        $('#showRoads').empty();
        $("#min-cost-result").empty();
        addedRoads = [];
        addedCities = [];
        cityCnt = 0;
        roadCnt = 0;
        if (selectedPlanName === 'new') {
            $("#verticesEdges").hide();
            $(".plan-input").show();
            $('#showCities').empty();
            $('#showRoads').empty();
            $('#showPlan').empty();
            $('#planName').val("");
            addedRoads = [];
            addedCities = [];
            cityCnt = 0;
            roadCnt = 0;
            updateCitiesDropDown("#fromCity");
            updateCitiesDropDown("#toCity");
            updateCitiesDropDown("#fromCityRun");
            updateCitiesDropDown("#toCityRun");
            return
        }

        let cities = [];
        let roadsToDraw = [];

        if (selectedPlanName !== '') {
            selectedPlan = plans.filter(x => x.planName === selectedPlanName)[0];
        }

        for (let i = 0; i < selectedPlan.roads.length; i++) {
            if (cities.filter(x => x.cityName === selectedPlan.roads[i].toCity.cityName).length === 0) {
                cities.push(selectedPlan.roads[i].toCity);
                addCity(selectedPlan.roads[i].toCity)
            }

            if (cities.filter(x => x.cityName === selectedPlan.roads[i].fromCity.cityName).length === 0) {
                cities.push(selectedPlan.roads[i].fromCity);
                addCity(selectedPlan.roads[i].fromCity)
            }

            roadsToDraw.push(selectedPlan.roads[i]);
            addRoad(selectedPlan.roads[i])
        }

        drawCities(cities);
        drawRoads(roadsToDraw);
        $("#verticesEdges").show();
        $(".plan-input").hide();
    } else {
        $.notify({
            message: "Same plan selected"
        }, notifySettings('info'));
    }
}

function persistRoadTable(roads) {
    addedRoads = [];
    roadCnt = 0;
    $('#showRoads').empty();
    for (let i = 0; i < roads.length; i++) {
        let showTable = $("#showRoads");
        let previousHtml = showTable.html();
        showTable.html(
            previousHtml +
            "<tr id='road-" + roadCnt + "'>" +
            drawRoadRow(roads[i].fromCity.cityName, roads[i].toCity.cityName, roads[i].capacity, roads[i].price) +
            "</tr>"
        );
        $(".editRoad").on('click', editRoadRow);
        $(".removeRoad").on('click', removeRoadRow);
        roadCnt = roadCnt + 1;
        addedRoads.push(
            {
                'fromCity': roads[i].fromCity.cityName,
                'toCity': roads[i].toCity.cityName,
                'capacity': roads[i].capacity,
                'price': roads[i].price,
                'deleted': false,
                'planName': selectedPlan.planName
            }
        );
    }
}

function updateCitiesDropDown(elemetSelector, selectedElement) {
    let citiesHtml = addedCities.filter(x => x.deleted === false).map(function (value, index, array) {
        if (selectedElement === value.cityName) {
            return "<option value='" + value.cityName + "' selected>" + value.cityName + "</option>"
        } else {
            return "<option value='" + value.cityName + "'>" + value.cityName + "</option>"
        }
    });

    $(elemetSelector).html(citiesHtml);
}
// End of Dynamic helpers


// Dynamic drawers
function drawRoads(roads) {
    s.graph.clear();
    graph['edges'] = roads.map(function (x) {
        return {id: x.id, source: x.fromCity.id, target: x.toCity.id, color: '#282c34', type: 'curvedArrow', size: 0.5}
    });

    s.graph.read(graph);
    s.refresh();

    console.log("Drawing Edges")
}

function drawCities(cities) {
    graph['nodes'] = cities.map(function (x) {
        return {id: x.id, label: x.cityName, x: x.xCoord, y: x.yCoord, size: nodeSize, color: nodeColor}
    });

    if ('edges' in graph) {
        delete graph['edges']
    }

    s.graph.clear();
    s.graph.read(graph);
    s.refresh();

    console.log("Drawing Vertices")
}

function drawRoadsAndCities(data, isResult) {
    let nodes = [];
    let minCost = 0;
    for (const x of data) {
        let fromCity = null;
        let toCity = null;

        if (isResult) {
            fromCity = x.edge.fromCity;
            toCity = x.edge.toCity;
            minCost = minCost + x.price;
        } else {
            fromCity = x.fromCity;
            toCity = x.toCity;
        }


        if (nodes.filter(x => x.id === fromCity.id).length === 0) {
            nodes.push({
                id: fromCity.id,
                label: fromCity.cityName,
                x: fromCity.xCoord,
                y: fromCity.yCoord,
                size: nodeSize,
                color: nodeColor
            });
        }

        if (nodes.filter(x => x.id === toCity.id).length === 0) {
            nodes.push({
                id: toCity.id,
                label: toCity.cityName,
                x: toCity.xCoord,
                y: toCity.yCoord,
                size: nodeSize,
                color: nodeColor
            });
        }

    }

    let edges = data.map(function (x) {
        let edgeColor = '#282c34';
        if (isResult) {
            if (x.flow > 0) {
                edgeColor = '#33cc33';
            } else if (x.flow === 0) {
                edgeColor = '#ff0000';
            }

            return {
                id: x.edge.id,
                source: x.edge.fromCity.id,
                target: x.edge.toCity.id,
                color: edgeColor,
                type: 'curvedArrow',
                size: 0.5,
                label: x.flow + " flow/ " + x.price + " price"
            }
        } else {
            return {
                id: x.id,
                source: x.fromCity.id,
                target: x.toCity.id,
                color: edgeColor,
                type: 'curvedArrow',
                size: 0.5
            }
        }
    });

    graph['nodes'] = nodes;
    graph['edges'] = edges;
    s.graph.clear();
    s.graph.read(graph);
    s.refresh();

    if (minCost !== 0) {
        $("#min-cost-result").text("The cost is: " + minCost)
    }
    console.log("Drawing Edges")
}
// End of Dynamic drawers

// Validators
function validateRoad(fromName, toName, index) {
    for(let i = 0; i < addedRoads.length; i++){
        if(i === index || addedRoads[i].deleted === true){
            continue;
        }

        if(addedRoads[i].fromCity === fromName && addedRoads[i].toCity === toName){
            return false
        }
    }
    return true;
}

function validateCity(cityName, xCoord, yCoord, index) {
    for(let i = 0; i < addedCities.length; i++){
        if(i === index || addedCities[i].deleted === true){
            continue;
        }

        if((addedCities[i].xCoord === xCoord && addedCities[i].yCoord === yCoord) ||
            addedCities[i].cityName === cityName){
            return false
        }
    }
    return true;
}

function noCitiesDropDown() {
    if (addedCities.filter(x => x.deleted === false).length === 0) {
        $.notify({
            message: "They are no entered cities. Please enter first cities"
        }, notifySettings('info'));
    }
}
// End of Validators