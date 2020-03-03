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