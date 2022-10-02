package com.tts.transitapp.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tts.transitapp.model.Bus;
import com.tts.transitapp.model.BusRequest;
import com.tts.transitapp.model.DistanceResponse;
import com.tts.transitapp.model.GeocodingResponse;
import com.tts.transitapp.model.Location;

@Service
public class TransitService {
    @Value("${transit_url}")
    public String transitUrl;
	
    @Value("${geocoding_url}")
    public String geocodingUrl;
	
    @Value("${distance_url}")
    public String distanceUrl;
	
    @Value("${google_api_key}")
    public String googleApiKey;


private List<Bus> getBuses(){
    RestTemplate restTemplate = new RestTemplate();
    Bus[] buses = restTemplate.getForObject(transitUrl, Bus[].class);
    return Arrays.asList(buses);
}

private Location getCoordinates(String description) {
	try {
    description = URLEncoder.encode(description, "utf-8");
	}catch(UnsupportedEncodingException e) {
    	System.out.println("Error unlencoding");
    	System.exit(1);;
    }
    String url = geocodingUrl + description + "+GA&key=" + googleApiKey;
    RestTemplate restTemplate = new RestTemplate();
    GeocodingResponse response = restTemplate.getForObject(url, GeocodingResponse.class);
    return response.results.get(0).geometry.location;
}

private double getDistance(Location origin, Location destination) {
    String url = distanceUrl + "origins=" + origin.lat + "," + origin.lng;
    url += "&destinations=" + destination.lat + "," + destination.lng; 
    url += "&key=" + googleApiKey;
    
    RestTemplate restTemplate = new RestTemplate();
    DistanceResponse response = restTemplate.getForObject(url, DistanceResponse.class);
    return response.rows.get(0).elements.get(0).distance.value * 0.000621371;
}

public List<Bus> getNearbyBuses(BusRequest request){
    List<Bus> allBuses = this.getBuses();
    
    Location personLocation = this.getCoordinates(request.address + " " + request.city);
    List<Bus> nearbyBuses = new ArrayList<>();
    
    for(Bus bus : allBuses) {
        Location busLocation = new Location();
        busLocation.lat = bus.LATITUDE;
        busLocation.lng = bus.LONGITUDE;
        
     double latDistance = Double.parseDouble(busLocation.lat) - Double.parseDouble(personLocation.lat);
     double lngDistance = Double.parseDouble(busLocation.lng) - Double.parseDouble(personLocation.lng);
     if (Math.abs(latDistance) <= 0.02 && Math.abs(lngDistance) <= 0.02) {
    	 double distance = getDistance(busLocation, personLocation);
    	 if (distance <= 1) {
    	     bus.distance = (double) Math.round(distance * 100) / 100;
    	     nearbyBuses.add(bus);
    	 }
     
     }
            
    }
    Collections.sort(nearbyBuses,
    				(bus1, bus2)->{
    					if (bus1.distance< bus2.distance) {
    						return -1;
    					}
    					if (bus1.distance> bus2.distance) {
    						return 1;
    					}
    					return 0;
    				});
    return nearbyBuses;
}
}
    