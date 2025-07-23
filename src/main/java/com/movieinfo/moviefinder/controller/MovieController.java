package com.movieinfo.moviefinder.controller;

import com.movieinfo.moviefinder.model.Movie;
import com.movieinfo.moviefinder.service.MovieService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

@RestController // Marks this class as a REST controller
@RequestMapping("/api")  // Base path for all endpoints in this controller
public class MovieController 
{
    @Autowired
    private MovieService movieService;

    @GetMapping("/movie")
    public Object getMovieInfo(@RequestParam String title) 
    {
        Movie movie = movieService.fetchMovieData(title);
        if (movie == null) 
        {
            // Return error message if movie not found
            return Map.of("error", "Movie not found. Please try another title.");
        }
        return movie;
    }

    @GetMapping("/movie-search")
    public Object searchMovies(@RequestParam String query) 
    {
        try 
        {
            // Construct API request URL with encoded query
            String url = "https://www.omdbapi.com/?s=" + URLEncoder.encode(query, "UTF-8") + "&apikey=dcb201da";
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            // Read API response into a string
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) 
            {
                result.append(line); // Append each line to result
            }
            reader.close();

            // Parse response JSON
            JsonObject response = JsonParser.parseString(result.toString()).getAsJsonObject();

            if (response.get("Response").getAsString().equals("True")) 
            {
                // If search was successful, parse the array of movie results
                JsonArray searchArray = response.getAsJsonArray("Search");
                List<Map<String, Object>> results = new ArrayList<>();

                // Extract relevant fields from each movie in the results
                for (int i = 0; i < searchArray.size(); i++) 
                {
                    JsonObject movieObj = searchArray.get(i).getAsJsonObject();
                    Map<String, Object> movieMap = new HashMap<>();
                    movieMap.put("Title", movieObj.get("Title").getAsString());
                    movieMap.put("Year", movieObj.get("Year").getAsString());
                    movieMap.put("imdbID", movieObj.get("imdbID").getAsString());
                    movieMap.put("Poster", movieObj.get("Poster").getAsString());
                    results.add(movieMap); // Add movie info to results list
                }

                return results;
            } else 
            {
                // If API response indicates no results
                return Map.of("error", "No results found.");
            }

        } catch (Exception e) 
        {
            e.printStackTrace();
            // Return error response
            return Map.of("error", "Error while searching.");
        }
    }
}
