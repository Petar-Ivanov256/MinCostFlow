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
    graph['nodes'] = nodes;

// Load the graph in sigma
    s.graph.read(graph);
// Ask sigma to draw it
    s.refresh();

    console.log("Drawing Vertices")
}

function drawRoads(roads) {
    let edges = roads.map(function (x) {
        return {id: x.id, source: x.fromCity.id, target: x.toCity.id, color: '#282c34', type:'curvedArrow', size:0.5}
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
    window.setTimeout(function() {s.killForceAtlas2()}, 3000);

    console.log("Drawing Edges")
}

