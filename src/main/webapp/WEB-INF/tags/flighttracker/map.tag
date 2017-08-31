<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ft" tagdir="/WEB-INF/tags/flighttracker" %>

<div id="map" class="map" tabindex="0"></div>
<script type="text/javascript">

    var tileLayer = new ol.layer.Tile({
        source : new ol.source.OSM()
    })

	var map = new ol.Map({
		target : 'map',
		projection: "EPSG:3857",
		layers : [
			tileLayer
		],
        view: new ol.View({
            center: ol.proj.fromLonLat([0, 35]),
            zoom: 2
        })
	});

	var dataUrl = "<c:url value='/map/data' />";
	$.ajax({
        type: "POST",
        traditional: true,
        url: dataUrl,
	    data: {
	        year: <ft:jsonArray value="${overviewQuery.year}" />,
	        airlineCode: <ft:jsonArray value="${overviewQuery.airlineCode}" />,
            airportCode: <ft:jsonArray value="${overviewQuery.airportCode}" />,
            cabinClass: <ft:jsonArray value="${overviewQuery.cabinClass}" />,
            flightReason: <ft:jsonArray value="${overviewQuery.flightReason}" />,
	        "${_csrf.parameterName}": "${_csrf.token}"
	    },
	    success: function(result) {

	        var routesSource = new ol.source.Vector();
	        for (var i=0; i < result.routes.length; i++) {

	            var route = result.routes[i];
	            var startLongitude = route.startPoint.longitude;
                var startLatitude = route.startPoint.latitude;
                var endLongitude = route.endPoint.longitude;
                var endLatitude = route.endPoint.latitude;

                var arcGenerator = new arc.GreatCircle(
                    { x: startLongitude, y: startLatitude },
                    { x: endLongitude, y: endLatitude }
                );
                var arcLine = arcGenerator.Arc(100 /* number of vertices */, {offset: 10});
                if (arcLine.geometries.length === 1) {

                    var line = new ol.geom.LineString(arcLine.geometries[0].coords);
                    line.transform(ol.proj.get('EPSG:4326'), ol.proj.get('EPSG:3857'));

                    var routeStyle = new ol.style.Style({
                        stroke: new ol.style.Stroke({
                            color: '#444444',
                            width: route.weight
                        })
                    });

                    var routeFeature = new ol.Feature({
                        geometry: line,
                        width: route.width
                    });
                    routeFeature.setStyle(routeStyle);

                    routesSource.addFeature(routeFeature);

                }

	        }

            map.addLayer(new ol.layer.Vector({ source: routesSource }));

	        var airportsSource = new ol.source.Vector();
            for (var i=0; i < result.airports.length; i++) {

                var airport = result.airports[i];

                var airportFeature = new ol.Feature({
                    geometry: new ol.geom.Point(ol.proj.transform([airport.point.longitude, airport.point.latitude], 'EPSG:4326', 'EPSG:3857')),
                });

                var airportStyle = new ol.style.Style({
                    image: new ol.style.Circle({
                        radius: 2,
                        fill: new ol.style.Fill({
                            color: 'rgba(10,10,10,0.5)'
                        }),
                        stroke: new ol.style.Stroke({
                            color: 'rgba(10,10,10,0.8)',
                            width: 3
                        })
                    }),
                    text: new ol.style.Text({
                        text: airport.code,
                        fill: new ol.style.Fill({
                            color: '#000000'
                        }),
                        stroke: new ol.style.Stroke({
                            color: '#FFFFFF',
                            width: 2
                        })
                    })
                });
                airportFeature.setStyle(airportStyle);

                airportsSource.addFeature(airportFeature);

            }

            map.addLayer(new ol.layer.Vector({ source: airportsSource }));

	        map.render();

        }
	});

</script>