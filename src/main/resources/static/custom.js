let map;
let userCoords = {lat: 33.7900, lng: -84.3856};
function initMap() {
	let busImage = {url: '/bus2.jpg', scaledSize:new google.maps.Size(60,60)};
	let personImage = {url: '/person.jpg', scaledSize:new google.maps.Size(60,60)};
	
    map = new google.maps.Map(document.getElementById('map'), {
        center: userCoords /*{ lat: parseFloat(busLocations[0].LATITUDE), lng: parseFloat(busLocations[0].LONGITUDE) }*/,
        zoom: 15,
        scrollwheel: false
    });
    
    let userMarker = new google.maps.Marker({
			position: userCoords,
			map: map,
			icon: personImage,
			animation: google.maps.Animation.BOUNCE,
			zoom: 350
			});
	
    for (i=0; i<busLocations.length; i++){
        let marker = new google.maps.Marker({
            position: { lat: parseFloat(busLocations[i].LATITUDE), 
            			lng: parseFloat(busLocations[i].LONGITUDE) },
           
            			
            map: map,
            icon: busImage,
            
        });
        
        let contentString = '<h2>' + busLocations[i].VEHICLE + '</h2>';

		let infowindow = new google.maps.InfoWindow({
   			content: contentString
 		});

	 	google.maps.event.addListener(marker, 'click', function() {
	    	infowindow.open(map,marker);
	    	map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
                Constants.MAP_ZOOM));
	 	});
	}
    
    
}