var addedCities = [];
var addedRoads = [];
// Probably obsolete
// var plan = {};
var plans = [];
var selectedPlan = {};
var s = null;
var graph = {};
var cityCnt = 0;
var roadCnt = 0;

var nodeSize = 3;
var nodeColor = '#008cc2';

function checkWidth(init) {

    if ($(window).width() < 1150) {
        $('#form-container').removeClass('col-md-4');
        $('#form-container').addClass('col-md-12');
        $('#draw-container').removeClass('col-md-8');
        $('#draw-container').addClass('col-md-12');
    } else {
        // if (!init) {
        $('#form-container').removeClass('col-md-12');
        $('#form-container').addClass('col-md-4');
        $('#draw-container').removeClass('col-md-12');
        $('#draw-container').addClass('col-md-8');
        // }
    }
}

$(document).ready(function () {
    checkWidth(true);

    $(window).resize(function () {
        checkWidth(false);
    });
});

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
                minNodeSize: 10,
                maxNodeSize: 20,
                maxArrowSize: 20,
                minArrowSize: 10,
                edgeLabelSize: 'proportional',
                defaultEdgeLabelSize: 15,
                sideMargin: 0.1,
                // autoRescale: false
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
    // TODO by editing the City name, it is not saved
    let element = $(this).parent().parent();
    let index = element.get(0).id.split("-")[1];
    element.children().eq(0).html("<input type='text' id='cityName-" + index + "' class='form-control' value='" + addedCities[index].cityName + "'>");
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

    let valBeforeEditFrom = element.children().eq(0).text();
    element.children().eq(0).html("<select class='form-control' id='fromCity-" + index + "'></select>");
    updateCitiesDropDown("#fromCity-" + index, valBeforeEditFrom);

    let valBeforeEditTo = element.children().eq(1).text();
    element.children().eq(1).html("<select class='form-control' id='toCity-" + index + "'></select>");
    updateCitiesDropDown("#toCity-" + index, valBeforeEditTo);

    element.children().eq(2).html("<input type='number' id='capacity-" + index + "' class='form-control' value='" + addedRoads[index].capacity + "'>");
    element.children().eq(3).html("<input type='number' id='price-" + index + "' class='form-control' value='" + addedRoads[index].price + "'>");
    element.children().eq(4).html(
        "<button type='button' id='saveRoad-" + index + "' class='btn btn-success btn-sm'>" +
        "<span class='glyphicon glyphicon-floppy-saved'></span>" +
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

        if (cityName === '' || xCoord === '' || yCoord === '') {
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
        "<span class='glyphicon glyphicon-edit'></span>" +
        "</button>" +
        "<button type='button' class='btn btn-danger btn-sm removeCity'>" +
        "<span class='glyphicon glyphicon-remove'></span>" +
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

        if (fromCity === '' || toCity === '' || cap === '' || price === '') {
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

function drawRoadRow(fromCity, toCity, cap, price) {
    return "<td class='col-md-3'>" +
        fromCity +
        "</td>" +
        "<td class='col-md-3'>" +
        toCity +
        "</td>" +
        "<td class='col-md-2'>" +
        cap +
        "</td>" +
        "<td class='col-md-2'>" +
        price +
        "</td>" +
        "<td class='col-md-6'>" +
        "<button type='button' class='btn btn-info btn-sm editRoad'>" +
        "<span class='glyphicon glyphicon-edit'></span>" +
        "</button>" +
        "<button type='button' class='btn btn-danger btn-sm removeRoad'>" +
        "<span class='glyphicon glyphicon-remove'></span>" +
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
            if (addedRoads.length > 0) {
                saveRoads(true);
            }
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

function saveRoads(updateRoadsTable) {
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
            if(updateRoadsTable === true){
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
}

function drawCities(cities) {

    let nodes = cities.map(function (x) {
        return {id: x.id, label: x.cityName, x: x.xCoord, y: x.yCoord, size: nodeSize, color: nodeColor}
    });


// Create a graph object
    graph['nodes'] = nodes;
    if ('edges' in graph) {
        delete graph['edges']
        // TODO clear edges table because it is wrong any more
    }

    s.graph.clear();
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
    s.graph.clear();
    graph['edges'] = edges;

// Load the graph in sigma
    //this gets rid of all the ndoes and edges
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

function drawRoadsAndCities(data, isResult) {
    // let nodes = cities.map(function (x) {
    //     return {id: x.id, label: x.cityName, x: x.xCoord, y: x.yCoord, size: nodeSize, color: nodeColor}
    // });

    let nodes = [];
    for (const x of data) {
        let fromCity = null;
        let toCity = null;
        if (isResult) {
            fromCity = x.edge.fromCity;
            toCity = x.edge.toCity;
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
        if (isResult) {
            return {
                id: x.edge.id,
                source: x.edge.fromCity.id,
                target: x.edge.toCity.id,
                color: '#282c34',
                type: 'curvedArrow',
                size: 0.5,
                label: x.flow + " flow/ " + x.price + " price"
            }
        } else {
            return {
                id: x.id,
                source: x.fromCity.id,
                target: x.toCity.id,
                color: '#282c34',
                type: 'curvedArrow',
                size: 0.5
            }
        }
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
                // drawRoadsAndCities(data, false)
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

function updateCitiesDropDown(elemetSelector, selectedElement) {
    let citiesHtml = addedCities.filter(x => x.deleted === false).map(function (value, index, array) {
        if (selectedElement === value.cityName) {
            return "<option value='" + value.cityName + "' selected>" + value.cityName + "</option>"
        } else {
            return "<option value='" + value.cityName + "'>" + value.cityName + "</option>"
        }

    });

    // $("#fromCity").html(citiesHtml);
    // $("#toCity").html(citiesHtml);
    $(elemetSelector).html(citiesHtml);
}

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
                console.log("Error", data);
            }
        });
    }
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

                $(".editPlan").on('click', editPlanRow);
                $(".removePlan").on('click', removePlanRow);
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

    // TODO have bugs by changing the plan clear them
    if (selectedPlan.planName !== selectedPlanName) {
        s.graph.clear();
        $('#showCities').empty();
        $('#showRoads').empty();
        addedRoads = [];
        addedCities = [];
        cityCnt = 0;
        roadCnt = 0;
        if (selectedPlanName === 'new') {
            $("#verticesEdges").hide();
            $(".plan-input").show();
            s.graph.clear();
            $('#showCities').empty();
            $('#showRoads').empty();
            $('#showPlan').empty();
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

function drawPlanRow(cityName) {
    return "<td class='col-md-6'>" +
        cityName +
        "</td>" +
        "<td class='col-md-6'>" +
        "<button type='button' class='btn btn-info btn-sm editPlan'>" +
        "<span class='glyphicon glyphicon-edit'></span>" +
        "</button>" +
        "<button type='button' class='btn btn-danger btn-sm removePlan'>" +
        "<span class='glyphicon glyphicon-remove'></span>" +
        "</button>" +
        "</td>";
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
                        // options
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

function removePlanRow() {
    $.ajax({
        type: "DELETE",
        url: "/delete-plan/" + selectedPlan.id,
        contentType: "application/json; charset=utf-8",
        success: function () {
            location.reload();
        },
        error: function (data) {
            console.log("Error", data);
        }
    });
}