// Global Variables
const searchField = document.getElementById("searchInput");
const suggestionList = document.getElementById("autocomplete");
const movieDetails = document.getElementById("movieDetails");

// When the user types in the search box, call the search function
searchField.addEventListener("input", handleSearchInput);

// ========== Handle Text Input ==========
// Function that runs when user types in the input box
async function handleSearchInput() 
{
    const userQuery = searchField.value.trim(); // remove spaces
    clearResults(); // clear any old results

    if (userQuery.length < 2) return; // don't search if too short

    try 
    {
        // Call the backend to search for movies
        const response = await fetch(`/api/movie-search?query=${encodeURIComponent(userQuery)}`);
        const results = await response.json();

        if (results.error) 
        {
            // Show error message if something goes wrong
            showMessage(results.error);
            return;
        }
        // Show the list of matching movies
        showSuggestions(results);
    } catch (err) {
        console.error(err);
        showMessage("Something went wrong.");
    }
}

// Show the list of suggested movies under the search box
function showSuggestions(movies) 
{
    movies.forEach(movie => {
        const listItem = document.createElement("li"); // create a list item
        listItem.className = "autocomplete-item";

        // Add movie image, title, and year
        listItem.innerHTML = `
            <img src="${movie.Poster !== 'N/A' ? movie.Poster : '/images/image_not_found.png'}" alt="${movie.Title}" />
            <div>
                <strong>${movie.Title}</strong>
                <div class="year">${movie.Year}</div>
            </div>
        `;
        // When the user clicks a movie, get more details
        listItem.addEventListener("click", () => {
            suggestionList.innerHTML = ""; // clear suggestion list
            fetchMovieDetails(movie.Title); // get full movie info
            searchField.value = "";  // clear the input box
        });
        suggestionList.appendChild(listItem); // add to the list
    });
}

// ========== Get Full Movie Details ==========
// Get detailed info about the selected movie
async function fetchMovieDetails(title) 
{
    movieDetails.innerHTML = "Loading..."; // show loading message
    try {
        const response = await fetch(`/api/movie?title=${encodeURIComponent(title)}`);
        const movieData = await response.json();

        if (movieData.error) 
        {
            // Show message if movie not found
            movieDetails.innerHTML = `<p style="color:red;">${movieData.error}</p>`;
        } else 
        {
            // Show movie info on the page
            displayMovieCard(movieData);
        }
    } catch (err) {
        console.error(err);
        movieDetails.innerHTML = `<p style="color:red;">Something went wrong.</p>`;
    }
}

// ========== Display Movie Card ==========
// Display the movie card with title, poster, and info
function displayMovieCard(data) 
{
    const starsRating = convertToStars(data.imdbRating);

    movieDetails.innerHTML = `
        <div class="card">
            <h2>${data.title} (${data.year})</h2>
            <img src="${data.poster !== 'N/A' ? data.poster : '/images/image_not_found.png'}" alt="${data.title}" />
            <p class="rating"><strong>Rating:</strong> ${starsRating} <span>(${data.imdbRating}/10)</span></p>
            <p><strong>Plot:</strong> ${data.plot}</p>
            <p><strong>Director:</strong> ${data.director}</p>
            <p><strong>Writer:</strong> ${data.writer}</p>
            <p><strong>Actors:</strong> ${data.actors}</p>
            <p><strong>Awards:</strong> ${data.awards}</p>
        </div>
    `;
}

// Turn a 1–10 IMDb rating into 0–5 stars (★☆☆☆☆)
function convertToStars(rating) {
    const score = parseFloat(rating) || 0;
    const stars = Math.round(score / 2); // 10 → 5 stars scale
    const maxStars = 5;

    return "★".repeat(stars) + "☆".repeat(maxStars - stars);
}

// Clear old movie details and autocomplete list
function clearResults() {
    suggestionList.innerHTML = "";
    movieDetails.innerHTML = "";
}

function showMessage(message) 
{
    // Show a message if no results found
    suggestionList.innerHTML = `<li class="no-result">${message}</li>`;
}
