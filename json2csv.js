var json2csv = function convertJSON2CSV(pJSONArray)
{
	//print('Begin convertJSON2CSV');
	var jsonObj = JSON.parse(pJSONArray);
	var header = '_id,name,type,latitude,longitude'+'\r\n';
	var row = '';

	for (var i = 0; i < jsonObj.length; i++) {
		var value = '';
		for (var index in jsonObj[i]) {
			if(index == '_id' || index == 'name' || index == 'type') {
				value += jsonObj[i][index];
				if (value != '') value += ','
			}
			if(index == 'geo_position') {
				if(Object.keys(jsonObj[i][index]).length == 0) {
					value += ',,'
				}else {
					for (var geoindex in jsonObj[i][index]) {					
						value += jsonObj[i][index][geoindex];
						if (value != '') value += ','
					}
				}
			}
		}
		row += value.substring(0,value.length-1) + '\r\n';
	}
	//print('End convertJSON2CSV');
	return header + row;
};