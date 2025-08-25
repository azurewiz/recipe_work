document.addEventListener('DOMContentLoaded', () => {
    let currentPage = 0;
    const pageSize = 10;
    const API_URL = 'http://localhost:8080/api/d2';

    const recipeList = document.getElementById('recipe-list');
    const ingestBtn = document.getElementById('ingestBtn');
    const searchBtn = document.getElementById('searchBtn');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');
    const cuisineInput = document.getElementById('cuisine');
    const ingredientInput = document.getElementById('ingredient');
    const pageInfoSpan = document.getElementById('page-info');
    const ingestMessageSpan = document.getElementById('ingest-message');

    // Function to fetch and display recipes
    async function fetchRecipes() {
        const url = `${API_URL}?page=${currentPage}&size=${pageSize}`;
        try {
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const data = await response.json();
            
            renderRecipes(data.content);
            pageInfoSpan.textContent = `Page ${data.number + 1} of ${data.totalPages}`;
            
        } catch (error) {
            console.error("Failed to fetch recipes:", error);
            recipeList.innerHTML = '<p>Failed to load recipes. Please check the server.</p>';
        }
    }
    
    // Renders the recipes to the UI
    function renderRecipes(recipes) {
        recipeList.innerHTML = '';
        if (recipes.length === 0) {
            recipeList.innerHTML = '<p>No recipes found.</p>';
            return;
        }
        recipes.forEach(recipe => {
            const div = document.createElement('div');
            div.className = 'recipe-item';
            div.innerHTML = `
                <h4>${recipe.title}</h4>
                <p><strong>Cuisine:</strong> ${recipe.cuisine}</p>
                <p><strong>Prep Time:</strong> ${recipe.prep_time}</p>
                <p><strong>Ingredients:</strong> ${Array.isArray(recipe.ingredients) ? recipe.ingredients.join(', ') : 'N/A'}</p>
            `;
            recipeList.appendChild(div);
        });
    }

    // Event listeners
    ingestBtn.addEventListener('click', async () => {
        ingestMessageSpan.textContent = 'Ingesting...';
        try {
            const response = await fetch(`${API_URL}/ingest`, { method: 'POST' });
            const text = await response.text();
            ingestMessageSpan.textContent = text;
            await fetchRecipes();
        } catch (error) {
            ingestMessageSpan.textContent = 'Failed to ingest data.';
            console.error(error);
        }
    });

    searchBtn.addEventListener('click', async () => {
        const cuisine = cuisineInput.value;
        const ingredient = ingredientInput.value;
        const url = `${API_URL}/search?cuisine=${cuisine}&ingredient=${ingredient}`;
        try {
            const response = await fetch(url);
            const data = await response.json();
            renderRecipes(data);
        } catch (error) {
            console.error("Failed to search recipes:", error);
        }
    });

    prevBtn.addEventListener('click', () => {
        if (currentPage > 0) {
            currentPage--;
            fetchRecipes();
        }
    });

    nextBtn.addEventListener('click', () => {
        currentPage++;
        fetchRecipes();
    });

    // Initial fetch when the page loads
    fetchRecipes();
});