$("#addCityBtn").on('click', addCity);
$("#saveCities").on('click', saveCities);
var addedCities = [];

function addCity() {
    var cityName = $("#inputCityName").val();
    var xCoord = $("#inputX").val();
    var yCoord = $("#inputY").val();
    var showTable = $("#showCities");

    var previousHtml = showTable.html();
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

function saveCities() {
    console.log(addedCities)
}