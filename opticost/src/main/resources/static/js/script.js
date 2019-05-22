var addedCities = [];
var addedRoads = [];
var plan = {};
var plans = [];
var selectedPlan = null;
var s = null;
var graph = {};
var cityCnt = 0;
var roadCnt = 0;

var nodeSize = 3;
var nodeColor = '#008cc2';

init();

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
            settings: {
                minEdgeSize: 0.1,
                maxEdgeSize: 2,
                minNodeSize: 1,
                maxNodeSize: 8,
                maxArrowSize: 20,
                minArrowSize: 10,
                edgeLabelSize: 'proportional',
                sideMargin: 0.1
            }
        }
    );
    //
    // // Initialise sigma:
    // var s = new sigma(
    //     {
    //         renderer: {
    //             container: document.getElementById('sigma-container'),
    //             // type: 'canvas'
    //             type: sigma.renderers.canvas
    //         },
    //         settings: {
    //             edgeLabelSize: 'proportional',
    //             minArrowSize: 10,
    //             sideMargin: 0.1
    //         }
    //     }
    // );

// Create a graph object
//     var graph = {
//         nodes: [
//             { id: "n0", label: "A node", x: 0, y: 0, size: 3, color: '#008cc2' },
//             { id: "n1", label: "Another node", x: 3, y: 1, size: 2, color: '#008cc2' },
//             { id: "n2", label: "And a last one", x: 1, y: 3, size: 1, color: '#E57821' }
//         ],
//         edges: [
//             { id: "e0", source: "n0", target: "n1", color: '#282c34', type:'curvedArrow', count:0, size:0.5 },
//             { id: "e1", source: "n1", target: "n2", color: '#282c34', type:'curvedArrow', count:0, size:1},
//             { id: "e2", source: "n2", target: "n0", color: '#FF0000', type:'curvedArrow', count:0, size:2},
//             { id: "e3", source: "n2", target: "n1", color: '#282c34', type:'curvedArrow', count:0, size:2},
//             { id: "e4", source: "n2", target: "n1", color: '#282c34', type:'curvedArrow', count:1, size:2},
//             { id: "e5", source: "n2", target: "n2", color: '#282c34', type:'curvedArrow', count:0, size:2},
//             { id: "e6", source: "n2", target: "n1", color: '#282c34', type:'curvedArrow', count:2, size:2}
//         ]
//     }
//
// // load the graph
//     s.graph.read(graph);
// // draw the graph
//     s.refresh();
//     sigma.plugins.relativeSize(s, 1);
// // launch force-atlas for 5sec
//     s.startForceAtlas2();
//     window.setTimeout(function() {s.killForceAtlas2()}, 2000);
}

function editCityRow() {
    let element = $(this).parent().parent();
    let index = element.get(0).id.split("-")[1];
    element.children().eq(0).html("<input type='text' id='cityName-" + index + "' class='form-control' value='" + addedCities[index].cityName + "'>");
    element.children().eq(1).html("<input type='number' id='xCoord-" + index + "' class='form-control' value='" + addedCities[index].xCoord + "'>");
    element.children().eq(2).html("<input type='number' id='yCoord-" + index + "' class='form-control' value='" + addedCities[index].yCoord + "'>");
    element.children().eq(3).html(
        "<button type='button' id='saveCity-" + index + "' class='btn btn-success btn-sm'>" +
        "<span class='glyphicon glyphicon-floppy-saved'></span> Save" +
        "</button>"
    );

    $("#saveCity-" + index).on('click', function () {
        let cityName = $("#cityName-" + index).val();
        let xCoord = $("#xCoord-" + index).val();
        let yCoord = $("#yCoord-" + index).val();

        if (addedCities.filter(x => x.cityName === cityName).length === 0 ||
            addedCities.filter(x => x.xCoord === xCoord && x.yCoord === yCoord && x.deleted === false).length === 0) {

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
    let index = element.get(0).id.split("-")[1];
    //TODO make the city names to be dropdowns
    // element.children().eq(0).html("<input type='text' id='fromCity-" + index + "' class='form-control' value='" + addedRoads[index].fromCity + "'>");
    element.children().eq(0).html("<select class='form-control' id='fromCity-'" + index + "></select>");
    updateCitiesDropDown("#fromCity-" + index);
    // element.children().eq(1).html("<input type='text' id='toCity-" + index + "' class='form-control' value='" + addedRoads[index].toCity + "'>");
    element.children().eq(0).html("<select class='form-control' id='toCity-'" + index + "></select>");
    updateCitiesDropDown("#toCity-" + index);

    element.children().eq(2).html("<input type='number' id='capacity-" + index + "' class='form-control' value='" + addedRoads[index].capacity + "'>");
    element.children().eq(3).html("<input type='number' id='price-" + index + "' class='form-control' value='" + addedRoads[index].price + "'>");
    element.children().eq(4).html(
        "<button type='button' id='saveRoad-" + index + "' class='btn btn-success btn-sm'>" +
        "<span class='glyphicon glyphicon-floppy-saved'></span> Save" +
        "</button>"
    );

    $("#saveRoad-" + index).on('click', function () {
        let fromCity = $("#fromCity-" + index).val();
        let toCity = $("#toCity-" + index).val();
        let cap = $("#capacity-" + index).val();
        let price = $("#price-" + index).val();

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
    });
}

function removeCityRow() {
    let element = $(this).parent().parent();
    let index = element.get(0).id.split("-")[1];
    addedCities[index].deleted = true;
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

function addCity(cityData) {
    let cityName = null;
    let xCoord = null;
    let yCoord = null;
    let showTable = $("#showCities");

    // Check if the function was called from event handler (UI button)
    if (cityData.target) {
        cityName = $("#inputCityName").val();
        xCoord = $("#inputX").val();
        yCoord = $("#inputY").val();
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
    }
}

function drawCityRow(cityName, xCoord, yCoord) {
    return "<td class='col-md-3'>" +
        cityName +
        "</td>" +
        "<td class='col-md-3'>" +
        xCoord +
        "</td>" +
        "<td class='col-md-3'>" +
        yCoord +
        "</td>" +
        "<td class='col-md-3'>" +
        "<button type='button' class='btn btn-info btn-sm editCity'>" +
        "<span class='glyphicon glyphicon-edit'></span> Edit" +
        "</button>" +
        "<button type='button' class='btn btn-danger btn-sm removeCity'>" +
        "<span class='glyphicon glyphicon-removeg'></span> Remove" +
        "</button>" +
        "</td>";
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
        cap = $("#inputCap").val();
        price = $("#inputPrice").val();
    } else {
        fromCity = roadData.fromCity.cityName;
        toCity = roadData.toCity.cityName;
        cap = roadData.capacity;
        price = roadData.price;
    }

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
            'planName': plan.name
        }
    );
}

function drawRoadRow(fromCity, toCity, cap, price) {
    return "<td class='col-md-3'>" +
        fromCity +
        "</td>" +
        "<td class='col-md-3'>" +
        toCity +
        "</td>" +
        "<td class='col-md-3'>" +
        cap +
        "</td>" +
        "<td class='col-md-3'>" +
        price +
        "</td>" +
        "<td class='col-md-3'>" +
        "<button type='button' class='btn btn-info btn-sm editRoad'>" +
        "<span class='glyphicon glyphicon-edit'></span> Edit" +
        "</button>" +
        "<button type='button' class='btn btn-danger btn-sm removeRoad'>" +
        "<span class='glyphicon glyphicon-removeg'></span> Remove" +
        "</button>" +
        "</td>";
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
            drawCities(data);
            $.notify({
                message: "The cities were successfully saved"
            }, notifySettings('success'));
        },
        error: function (data) {
            console.log("There is a problem can't save the cities", data);
            $.notify({
                message: "There is a problem can't save the cities"
            }, notifySettings('danger'));
        }
    });
}

function saveRoads() {
    console.log(addedRoads);
    let data = addedRoads.filter(x => x.deleted === false);
    $.ajax({
        type: "POST",
        url: "/save-roads",
        data: JSON.stringify(data),
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            console.log("The roads were successfully saved", data);
            drawRoads(data);
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
}

function drawCities(cities) {

    let nodes = cities.map(function (x) {
        return {id: x.id, label: x.cityName, x: x.xCoord, y: x.yCoord, size: nodeSize, color: nodeColor}
    });

// Create a graph object
    graph['nodes'] = nodes;

// Load the graph in sigma
    s.graph.read(graph);
// Ask sigma to draw it
    s.refresh();

    console.log("Drawing Vertices")
}

function drawRoads(roads) {
    let edges = roads.map(function (x) {
        return {id: x.id, source: x.fromCity.id, target: x.toCity.id, color: '#282c34', type: 'curvedArrow', size: 0.5}
    });

// Create a graph object
    graph['edges'] = edges;

// Load the graph in sigma
    //this gets rid of all the ndoes and edges
    s.graph.clear();
    //this gets rid of any methods you've attached to s.
    // s.graph.kill();

    s.graph.read(graph);
// Ask sigma to draw it
    s.refresh();

    s.startForceAtlas2();
    window.setTimeout(function () {
        s.killForceAtlas2()
    }, 3000);

    console.log("Drawing Edges")
}

function drawRoadsAndCities(data) {
    // let nodes = cities.map(function (x) {
    //     return {id: x.id, label: x.cityName, x: x.xCoord, y: x.yCoord, size: nodeSize, color: nodeColor}
    // });

    let nodes = [];
    for (const x of data) {
        let fromCity = x.fromCity;
        let toCity = x.toCity;

        if (nodes.filter(x => x.id === fromCity.id).length === 0) {
            nodes.push({
                id: x.fromCity.id,
                label: x.fromCity.cityName,
                x: x.fromCity.xCoord,
                y: x.fromCity.yCoord,
                size: nodeSize,
                color: nodeColor
            });
        }

        if (nodes.filter(x => x.id === toCity.id).length === 0) {
            nodes.push({
                id: x.toCity.id,
                label: x.toCity.cityName,
                x: x.toCity.xCoord,
                y: x.toCity.yCoord,
                size: nodeSize,
                color: nodeColor
            });
        }

    }

    let edges = data.map(function (x) {
        return {id: x.id, source: x.fromCity.id, target: x.toCity.id, color: '#282c34', type: 'curvedArrow', size: 0.5}
    });

// Create a graph object
    graph['nodes'] = nodes;
    graph['edges'] = edges;

// Load the graph in sigma
    //this gets rid of all the ndoes and edges
    s.graph.clear();
    //this gets rid of any methods you've attached to s.
    // s.graph.kill();

    s.graph.read(graph);
    // Put the startForceAtlas2 (redrawing here) it wont be shown how it rearranges the nodes
// Ask sigma to draw it
    s.refresh();

    // s.startForceAtlas2();
    // window.setTimeout(function () {
    //     s.killForceAtlas2()
    // }, 3000);

    console.log("Drawing Edges")
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
                drawRoadsAndCities(data)
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

function notifySettings(type) {
    return {
        // settings
        type: type,
        placement: {
            from: "bottom",
            align: "left"
        },
    };
}

function noCitiesDropDown() {
    if (addedCities.filter(x => x.deleted === false).length === 0) {
        $.notify({
            message: "They are no entered cities. Please enter first cities"
        }, notifySettings('info'));
    }
}

function updateCitiesDropDown(elemetSelector) {
    let citiesHtml = addedCities.filter(x => x.deleted === false).map(function (value, index, array) {
        return "<option value='" + value.cityName + "'>" + value.cityName + "</option>"
    });

    // $("#fromCity").html(citiesHtml);
    // $("#toCity").html(citiesHtml);
    $(elemetSelector).html(citiesHtml);
}

function runMulticost() {
    let fromCity = $("#fromCityRun").val();
    let toCity = $("#toCityRun").val();
    let cargo = $("#cargo").val();

    if(fromCity === null ||
        toCity === null ||
        cargo === ""){
        $.notify({
            message: "Please fill all fields in order to run the algorithm"
        }, notifySettings('danger'));
    }else{

        let data = {
            selectedPlan: selectedPlan,
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
            },
            error: function (data) {
                console.log("Error", data);
            }
        });
    }
}

function savePlan() {
    let inputPlanName = $("#planName").val();

    if (inputPlanName !== null && inputPlanName !== "") {
        plan = {
            name: inputPlanName,
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
            },
            error: function (data) {
                console.log("Error", data);
            }
        });
    } else {
        $.notify({
            message: "There is no name for the plan"
        }, notifySettings('danger'));
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
            console.log("Error", data);
        }
    });
}

function onPlanChange() {
    //TODO check if the selected plan is the same because it adds same stuff
    //TODO clear the old things when other plan is selected
    //TODO handle the case when "new plan" is selected
    if (selectedPlan !== $(this).val()) {
        $('#showCities').empty();
        $('#showRoads').empty();
        if ($(this).val() === 'new') {
            $("#verticesEdges").hide();
            $(".plan-input").show();
            addedCities = [];
            addedRoads = [];
            updateCitiesDropDown("#fromCity");
            updateCitiesDropDown("#toCity");
            updateCitiesDropDown("#fromCityRun");
            updateCitiesDropDown("#toCityRun");
            return
        }

        let citiesToDraw = [];
        let roadsToDraw = [];

        selectedPlan = plans.filter(x => x.planName === $(this).val())[0];
        let cities = [];
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