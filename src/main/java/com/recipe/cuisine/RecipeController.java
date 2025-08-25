


package com.recipe.cuisine;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeRepository recipeRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public RecipeController(RecipeRepository recipeRepository, ObjectMapper objectMapper) {
        this.recipeRepository = recipeRepository;
        this.objectMapper = objectMapper;
    }

    // POST Endpoint for bulk ingestion (updated for the new JSON format)
    @PostMapping("/ingest")
    public ResponseEntity<String> ingestRecipes() {
        try {
            // Read the JSON file into a Map<String, Recipe>
            Map<String, Recipe> recipesMap = objectMapper.readValue(
                this.getClass().getResourceAsStream("/US_recipes_null.json"),
                new TypeReference<Map<String, Recipe>>() {}
            );

            // Extract the Recipe objects from the map's values
            List<Recipe> recipes = recipesMap.values().stream().collect(Collectors.toList());

            // Bulk insert into MongoDB
            recipeRepository.saveAll(recipes);
            
            return ResponseEntity.ok("Ingested " + recipes.size() + " recipes successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to ingest recipes: " + e.getMessage());
        }
    }

    // GET Endpoint for pagination and sorting
    @GetMapping
    public Page<Recipe> getRecipesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // Sorting by prep_time (the field in the JSON)
        Sort sort = Sort.by(Sort.Direction.DESC, "prep_time");
        Pageable pageable = PageRequest.of(page, size, sort);
        return recipeRepository.findAll(pageable);
    }
    
    // GET Endpoint to search by cuisine and ingredient
    @GetMapping("/search")
    public List<Recipe> searchRecipes(@RequestParam String cuisine, @RequestParam String ingredient) {
        return recipeRepository.findByCuisineAndIngredientsContaining(cuisine, ingredient);
    }

    // GET Endpoint to search by max calories
    @GetMapping("/search/by-calories")
    public List<Recipe> searchByCalories(@RequestParam String maxCalories) {
        return recipeRepository.findByNutrientsCaloriesLessThanEqual(maxCalories);
    }
    
    // GET Endpoint to retrieve a single recipe by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> findById(@PathVariable String id) {
        return recipeRepository.findById(id)
                               .map(ResponseEntity::ok)
                               .orElse(ResponseEntity.notFound().build());
    }

    // PUT Endpoint to update an existing recipe
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable String id, @RequestBody Recipe updatedRecipe) {
        return recipeRepository.findById(id).map(recipe -> {
            recipe.setTitle(updatedRecipe.getTitle());
            recipe.setCuisine(updatedRecipe.getCuisine());
            // You would need to update all fields here
            return ResponseEntity.ok(recipeRepository.save(recipe));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE Endpoint to delete a recipe by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable String id) {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}