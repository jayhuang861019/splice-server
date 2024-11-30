// 是否将经纬度显示成度的形 如果为否 则为度分秒
var latlng_type_du = true;

function formatLanlng(latlng) {
    if (latlng_type_du) {
        return "经度：" + latlng.lat.toFixed(6) + "&emsp;纬度：" + latlng.lng.toFixed(6);
    } else {
        return "经度：" + toDFM(latlng.lat) + "&emsp;纬度：" + toDFM(latlng.lng);
    }
}

function toDFM(degree) {
    // body...度向度分秒转换
    degree = Math.abs(degree);
    var v1 = Math.floor(degree);
    var v2 = Math.floor((degree - v1) * 60);
    var v3 = Math.round(((degree - v1) * 3000) % 60);
    return v1 + "°" + v2 + "'" + v3 + '"';
}

var DRAWLAYERS = [];

var circleMarkers = [];

var MEASURETOOLTIP; //量距提示
var distance = 0; //测量结果

var DrawPolyline; //绘制的折线
var DRAWMOVEPOLYLINE; //绘制过程中的折线
var DrawPolylinePoints = []; //绘制的折线的节点集

var distanceView;
//线段点的样式
var measureCircleStyle = {
    radius: 4,
    weight: 2,
    stroke: true,
    color: 'white',
    fill: true,
    fillOpacity: 1,
    fillColor: '#0066cc'
};

function drawRectangle() {
    app.$data.isDrawingRectangle = true;
    let rectangle;
    let textMarker;
    let markerIcon = L.divIcon({
        html: '具体内容', //marker标注
        iconSize: [200, 35], //marker宽高
        iconAnchor: [100, 20], //文字标注相对位置
        className: 'rectangleInfo'
    });
    map.getContainer().style.cursor = "crosshair";
    let op = { color: '#ff7800', weight: 1 };
    let leftTop;
    let rightBottom;
    //设置地图的鼠标按下事件
    map.on("mousedown", e => {
        if (app.$data.isDrawingRectangle) {
            L.circleMarker(e.latlng, measureCircleStyle).addTo(rectangleLayer);
            if (leftTop) {
                rightBottom = e.latlng;
                if (rectangle) {
                    rectangle.remove();
                }
                rectangle = L.rectangle([leftTop, rightBottom], op);
                rectangle.addTo(rectangleLayer);
                finishDrawing();
                return;
            }
            leftTop = e.latlng;
        }
    });
    map.on("mousemove", e => {

        getMousePointCoord(e);
        if (app.$data.isDrawingRectangle && leftTop) {
            rightBottom = e.latlng;
            if (rectangle) {
                rectangle.remove();
            }
            rectangle = L.rectangle([leftTop, rightBottom], op);
            rectangle.addTo(rectangleLayer);

            let ds = getWidthAndLength(rectangle);

            markerIcon.options.html = formatLength(ds[0]) + '&nbsp;,&nbsp' + formatLength(ds[1]) + '<br>' + getAreaString(rectangle);
            if (!textMarker) {
                textMarker = L.marker(rectangle.getCenter(), { icon: markerIcon });
                textMarker.addTo(rectangleLayer);
            } else {
                textMarker.remove();
                textMarker.setLatLng(rectangle.getCenter());
                textMarker.addTo(rectangleLayer);
            }
        }
    });

    map.on("dblclick", e => {
        finishDrawing();
    });

    let finishDrawing = function() {
        app.$data.isDrawingRectangle = false;
        map.off("mousedown");
        map.off("mousemove");
        map.off("dblclick");
        map.getContainer().style.cursor = "";
        map.on('mousemove', getMousePointCoord);

        app.$data.spliceTileDialog.show = true;
        app.$data.spliceTileDialog.rectangleInfo = markerIcon.options.html.replace('<br>', '&emsp;');
        app.$data.spliceTileDialog.leftTop = leftTop;
        app.$data.spliceTileDialog.rightBottom = rightBottom;
        app.$data.spliceTileDialog.level = map.getZoom();
        app.$data.spliceTileDialog.leftTopTile = latlon2tile(leftTop, map.getZoom());
        app.$data.spliceTileDialog.rightBottomTile = latlon2tile(rightBottom, map.getZoom());
        app.$data.spliceTileDialog.grid = 0;

    }
}

function startDrawLine() {
    if (distanceView == undefined || distanceView == null) {
        distanceView = L.DomUtil.create('span', 'measure_tip', mapDom);
    }
    distance = 0; //测量结果
    map.getContainer().style.cursor = "crosshair";
    var shapeOptions = {
        color: "#0066cc",
        weight: 3,
        opacity: 0.8,
        fill: false,
        clickable: true
    }
    app.$data.isDrawingPolyline = true; //是否正在绘制
    //绘制的折线
    DrawPolyline = new L.Polyline([], shapeOptions);
    //将当前的折线加入到图层中
    drawPolylineLayerGroup.addLayer(DrawPolyline);
    //实例化量距提示
    MEASURETOOLTIP = new L.Tooltip(map);
    //设置地图的鼠标按下事件
    map.on("mousedown", onClick);
    //设置地图的双击事件
    map.on("dblclick", onDoubleClick);

    //鼠标按下事件
    function onClick(e) {

        DrawPolylinePoints.push(e.latlng); //绘制的折线的节点集
        //测量结果加上距离上个点的距离
        if (DrawPolylinePoints.length > 1) {
            distance += e.latlng.distanceTo(
                DrawPolylinePoints[DrawPolylinePoints.length - 2]
            );
        }
        //绘制的折线添加进集合
        DrawPolyline.addLatLng(e.latlng);
        //地图添加鼠标移动事件
        map.on("mousemove", onMove);

        //第一点
        if (DrawPolylinePoints.length == 1) {
            measureCircleStyle.color = "orange";
        } else
            measureCircleStyle.color = "white";
        L.circleMarker(e.latlng, measureCircleStyle).addTo(drawPolylineLayerGroup);
    }

    //鼠标移动事件
    function onMove(e) {
        if (app.$data.isDrawingPolyline) {
            //更新鼠标位置坐标
            getMousePointCoord(e);
            //是否正在绘制
            //将上次的移除
            if (DRAWMOVEPOLYLINE != undefined && DRAWMOVEPOLYLINE != null) {
                //绘制过程中的折线
                map.removeLayer(DRAWMOVEPOLYLINE);
            }
            //获取上个点坐标
            var prevPoint = DrawPolylinePoints[DrawPolylinePoints.length - 1];
            //绘制最后一次的折线
            DRAWMOVEPOLYLINE = new L.Polyline([prevPoint, e.latlng], shapeOptions);
            //添加到地图
            map.addLayer(DRAWMOVEPOLYLINE);
            if (DrawPolylinePoints.length != 0) {
                let preDistance = distance + e.latlng.distanceTo(DrawPolylinePoints[DrawPolylinePoints.length - 1]);
                if (distanceView != undefined) {
                    distanceView.innerText = (preDistance / 1000).toFixed(2) + "公里";
                    distanceView.style.top = (e.containerPoint.y - 20) + 'px';
                    distanceView.style.left = e.containerPoint.x + 'px';
                }
            }
        }
    }

    //鼠标双击事件
    function onDoubleClick(e) {
        map.getContainer().style.cursor = "";
        if (distanceView != undefined) {
            mapDom.removeChild(distanceView);
            distanceView = null;
        }
        /*显示两点距离*/
        //之前的距离加上最后一次的距离
        var finalDistance =
            distance +
            e.latlng.distanceTo(DrawPolylinePoints[DrawPolylinePoints.length - 1]);


        let markerIcon = L.divIcon({
            html: (finalDistance / 1000).toFixed(2) + "公里",
            iconSize: [100, 30],
            iconAnchor: [0, 30],
            className: 'distance_div_marker'
        });
        //将文字标注添加到线段图层当中
        L.marker(e.latlng, { icon: markerIcon }).addTo(drawPolylineLayerGroup);

        if (app.$data.isDrawingPolyline) {
            //清除上次的
            if (DRAWMOVEPOLYLINE != undefined && DRAWMOVEPOLYLINE != null) {
                map.removeLayer(DRAWMOVEPOLYLINE);
                DRAWMOVEPOLYLINE = null;
            }
            DrawPolylinePoints = [];
            app.$data.isDrawingPolyline = false;
            //移除事件
            map.off("mousedown");
            map.off("mousemove");
            map.on("mousemove", getMousePointCoord);
            map.off("dblclick");
        }
    }
}

//执行清除方法
function clearMeasure() {
    drawPolylineLayerGroup.clearLayers();
    tempPositionLayer.clearLayers();
    rectangleLayer.clearLayers();
    circleMarkers = [];
}


function addMarker(latlon) {
    let icon = L.icon({
        iconUrl: 'image/zhs.png',
        iconSize: [50, 50],
        //锚点
        iconAnchor: [0, 50]
    });
    let centerMarker = L.marker(latlon, {
        icon: icon
    });
    centerMarker.addTo(tempPositionLayer);
}