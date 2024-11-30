// 用于地理计算
function getWidthAndLength(rectangle) {

    let latlons = rectangle.getLatLngs()[0];
    let p1 = L.latLng(latlons[0]);
    let p2 = L.latLng(latlons[1]);
    let p3 = L.latLng(latlons[2]);

    let d1 = p1.distanceTo(p2);
    let d2 = p1.distanceTo(p3);

    return [d1, d2];
}

function getArea(rectangle) {

    let ds = getWidthAndLength(rectangle);
    let area = ds[0] * ds[1];
    return area;
}

function getAreaString(rectangle) {
    return formatArea(getArea(rectangle));
}

function formatLength(length) {

    if (length > 100) {
        output = (Math.round(length / 1000 * 100) / 100) + ' ' + ' km';

    } else {
        output = (Math.round(length * 100) / 100) + ' ' + 'm';
    }
    return output;
}


function formatArea(area) {

    if (area > 10000) {
        output = (Math.round(area / 1000000 * 100) / 100) + ' ' + 'km<sup>2</sup>';
    } else
        output = (Math.round(area * 100) / 100) + ' ' + 'm<sup>2</sup>';

    var mu = area / 666.67;
    var gq = area / 10000;
    return output + '( ' + mu.toFixed(2) + '亩 / ' + gq.toFixed(2) + "公顷 )";
}


// 经纬度转瓦片编号
function lon2tile(lon, zoom) {
    return (Math.floor((lon + 180) / 360 * Math.pow(2, zoom)));
}

function lat2tile(lat, zoom) {
    return (Math.floor((1 - Math.log(Math.tan(lat * Math.PI / 180) + 1 / Math.cos(lat * Math.PI / 180)) / Math.PI) / 2 * Math.pow(2, zoom)));
}

//计算当前点所在的瓦片号
function latlon2tile(latlon, zoom) {
    let t1 = lat2tile(latlon.lat, zoom);
    let t2 = lon2tile(latlon.lng, zoom);
    return [t1, t2];
}

//计算两个点这间的瓦片数量
function calcTotalTileSize(latlon1, latlon2, zoom) {
    let tile1 = latlon2tile(latlon1, zoom);
    let tile2 = latlon2tile(latlon2, zoom);
    let x = Math.abs(tile2[0] - tile1[0]) + 1;
    let y = Math.abs(tile2[1] - tile1[1]) + 1;
    return x * y;
}

// 瓦片编号转经纬度
function tile2long(x, z) {
    return (x / Math.pow(2, z) * 360 - 180);
}

function tile2lat(y, z) {
    var n = Math.PI - 2 * Math.PI * y / Math.pow(2, z);
    return (180 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n))));
}


function timeStampFormatter(mss) {
    /* eslint-disable */
    const hours = parseInt((mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = parseInt((mss % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = parseInt((mss % (1000 * 60)) / 1000);
    return hours + ':' + minutes + ':' + seconds;
};

/**
 * [fileLengthFormat 格式化文件大小]
 * @param  {[int]} total [文件大小]
 * @param  {[int]} n     [total参数的原始单位如果为Byte，则n设为1，如果为kb，则n设为2，如果为mb，则n设为3，以此类推]
 * @return {[string]}       [带单位的文件大小的字符串]
 */
function fileLengthFormat(total, n) {
    var format;
    var len = total / (1024.0);
    if (len > 1000) {
        return arguments.callee(len, ++n);
    } else {
        switch (n) {
            case 1:
                format = len.toFixed(2) + "KB";
                break;
            case 2:
                format = len.toFixed(2) + "MB";
                break;
            case 3:
                format = len.toFixed(2) + "GB";
                break;
            case 4:
                format = len.toFixed(2) + "TB";
                break;
        }
        return format;
    }
}