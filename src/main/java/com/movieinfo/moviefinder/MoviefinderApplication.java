// Example URLs to test endpoints:

// http://localhost:8080/movie-form.html -> Frontend form (if created)
// http://localhost:8080/api/movie?title=Interstellar  -> API endpoint to fetch movie details by title


// Movie Info Finder
// Name: Scarlett Medina
// Date: 05/02/2025

package com.movieinfo.moviefinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MoviefinderApplication 
{
	public static void main(String[] args) 
	{
		SpringApplication.run(MoviefinderApplication.class, args);
	}

}
