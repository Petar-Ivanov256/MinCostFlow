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

function sigmaSettings() {
    return  {
        minEdgeSize: 2,
        maxEdgeSize: 4,
        minNodeSize: 20,
        maxNodeSize: 25,
        maxArrowSize: 6,
        minArrowSize: 8,
        defaultLabelSize: 17,
        sideMargin: 50,
        edgeLabelSize: 'proportional',
        edgeLabelSizePowRatio: 1.5
    }
}