var addedCities = [];
var addedRoads = [];
var s = null;
var graph = {};

var nodeSize = 3;
var nodeColor = '#008cc2';

init();

function init() {
    $("#sigma-container").css("height", $(document).height() * 0.9);
    $("#addCityBtn").on('click', addCity);
    $("#saveCities").on('click', saveCities);
    $("#addRoad").on('click', addRoad);
    $("#saveRoad").on('click', saveRoads);

    // Initialise sigma:
    s = new sigma(
        {
            renderer: {
                container: document.getElementById('sigma-container'),
                type: 'svg'
            },
            settings: {
                minEdgeSize: 0.1,
                maxEdgeSize: 2,
                minNodeSize: 1,
                maxNodeSize: 8,
            }
        }
    );
}

function addCity() {
    let cityName = $("#inputCityName").val();
    let xCoord = $("#inputX").val();
    let yCoord = $("#inputY").val();
    let showTable = $("#showCities");

    let previousHtml = showTable.html();
    showTable.html(
        previousHtml +
        "<tr>" +
        "<td class='col-md-5'>" +
        cityName +
        "</td>" +
        "<td class='col-md-3'>" +
        xCoord +
        "</td>" +
        "<td class='col-md-3'>" +
        yCoord +
        "</td>" +
        "</tr>"
    );

    addedCities.push(
        {
            'cityName': cityName,
            'xCoord': xCoord,
            'yCoord': yCoord
        }
    );
}

function addRoad() {
    let fromCity = $("#fromCity").val();
    let toCity = $("#toCity").val();
    let cap = $("#inputCap").val();
    let price = $("#inputPrice").val();
    let showTable = $("#showRoads");

    let previousHtml = showTable.html();
    showTable.html(
        previousHtml +
        "<tr>" +
        "<td class='col-md-3'>" +
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
        "</tr>"
    );

    addedRoads.push(
        {
            'fromCity': fromCity,
            'toCity': toCity,
            'capacity': cap,
            'price': price
        }
    );
}

function saveCities() {
    console.log(addedCities);
    $.ajax({
        type: "POST",
        url: "/save-cities",
        data: JSON.stringify(addedCities),
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            console.log("Success", data);
            drawCities(data);
        },
        error: function (data) {
            console.log("Error", data);
        }
    });
}

function saveRoads() {
    console.log(addedRoads);
    $.ajax({
        type: "POST",
        url: "/save-roads",
        data: JSON.stringify(addedRoads),
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            console.log("Success", data);
            drawRoads(data);
        },
        error: function (data) {
            console.log("Error", data);
        }
    });
}

function drawCities(cities) {

    let nodes = cities.map(function (x) {
        return {id: x.id, label: x.cityName, x: x.xCoord, y: x.yCoord, size: nodeSize, color: nodeColor}
    });

// Create a graph object
    graph = {
        nodes: nodes
    };

// Load the graph in sigma
    s.graph.read(graph);
// Ask sigma to draw it
    s.refresh();

    console.log("Drawing graph")
}

function drawRoads(cities) {

    let edges = cities.map(function (x) {
        return {id: x.id, label: x.cityName, x: x.xCoord, y: x.yCoord, size: nodeSize, color: nodeColor}
    });

// Create a graph object
    graph = {
        edges: edges
    };

// Load the graph in sigma
    s.graph.read(graph);
// Ask sigma to draw it
    s.refresh();

    console.log("Drawing graph")
}