package com.movieinfo.moviefinder.service;

import com.google.gson.Gson;
import com.movieinfo.moviefinder.model.Movie;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Service
public class MovieService {

    private static final String API_KEY = "dcb201da"; // OMDb API key

    // Fetches movie data from OMDb API by title
    public Movie fetchMovieData(String title) 
    {
        try 
        {
            // Encode title for use in URL
            String encodedTitle = URLEncoder.encode(title, "UTF-8");
            
            // Construct OMDb API URL for single movie search by title
            String apiUrl = "https://www.omdbapi.com/?t=" + encodedTitle + "&apikey=" + API_KEY;

            // Open connection to the API URL
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET"); // Set request type to GET

            // Read response from input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;

            // Build full response string
            while ((line = reader.readLine()) != null) 
            {
                result.append(line);
            }
            reader.close();

            // Convert JSON response to Movie object using Gson
            Gson gson = new Gson();
            return gson.fromJson(result.toString(), Movie.class);

        } catch (Exception e) 
        {
            // Return null if any exception occurs (e.g., network error)
            return null; 
        }
    }
}


