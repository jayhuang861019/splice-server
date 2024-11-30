let options = {
    zoomControl: true,
    attributionControl: false,
};
var map;
var mapDom;

// 创建图层
var latlonGridLayer;

var baseTileLayer = {};
//测量的图层组
var drawPolylineLayerGroup = L.layerGroup([]);

//临时点图层用于来存放找点时候的标绘
var tempPositionLayer = L.layerGroup([]);
//画定下载区域
var rectangleLayer = L.layerGroup([]);

// 获取当前鼠标所在点的坐标
var getMousePointCoord = event => {
    let latlng = event.latlng;
    let latlonStr = formatLanlng(latlng);
    let dom = document.getElementById("coord");
    dom.innerHTML = latlonStr;
};

function initMap() {
    options.layers = [drawPolylineLayerGroup, tempPositionLayer, rectangleLayer];
    map = L.map("map", options).setView([39.9158, 116.3958], 13);
    //设置缩放组件的位置
    map.zoomControl.setPosition('bottomright');
    document.getElementById("level").innerHTML = "层级：" + map.getZoom() + "&emsp;";
    map.on("mousemove", getMousePointCoord);
    map.on('zoom', e => {
        document.getElementById("level").innerHTML = "层级：" + map.getZoom() + "&emsp;";
    })
    mapDom = document.getElementById('map');

    //经纬度网添加
    let latlonGridOps = {
        showLabel: true,
        dashArray: [5, 5],
        zoomInterval: {
            latitude: [{
                    start: 1,
                    end: 4,
                    interval: 10,
                },
                {
                    start: 4,
                    end: 5,
                    interval: 5,
                },
                {
                    start: 5,
                    end: 7,
                    interval: 3,
                },
                {
                    start: 7,
                    end: 8,
                    interval: 1,
                },
                {
                    start: 8,
                    end: 20,
                    interval: 0.5,
                },
            ],
            longitude: [{
                    start: 1,
                    end: 4,
                    interval: 10,
                },
                {
                    start: 4,
                    end: 5,
                    interval: 5,
                },
                {
                    start: 5,
                    end: 7,
                    interval: 3,
                },
                {
                    start: 7,
                    end: 8,
                    interval: 1,
                },
                {
                    start: 8,
                    end: 20,
                    interval: 0.5,
                },
            ],
        },
        color: "#fff",
    };
    latlonGridLayer = L.latlngGraticule(latlonGridOps);
    latlonGridLayer.addTo(map);

}

/**
 * 切换经纬度网格
 * @returns 添加是否成功
 */
function switchLatlngGridLayer() {
    if (map.hasLayer(latlonGridLayer)) {
        latlonGridLayer.remove();
        app.$data.showLatlonGridLayer = false;
    } else {
        latlonGridLayer.addTo(map);
        app.$data.showLatlonGridLayer = true;
    }
}
// 切换底图
function changeTileLayer() {
    map.removeLayer(baseTileLayer);
    baseTileLayer = baseTileLayer == wmtsLayer1 ? wmtsLayer2 : wmtsLayer1;
    map.addLayer(baseTileLayer);
}


// 显示整个中国区域
function zoomToChina() {
    let corner1 = [48.5, 73.2];
    let corner2 = [13.0, 132.8];
    let bounds = L.latLngBounds(corner1, corner2);
    map.flyToBounds(bounds, {
        duration: 0.5
    });
}