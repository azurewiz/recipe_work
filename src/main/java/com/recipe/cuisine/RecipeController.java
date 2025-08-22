


package com.recipe.cuisine;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

import java.util.Optional;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeRepository recipeRepository;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    @Autowired
    public RecipeController(RecipeRepository recipeRepository, ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.recipeRepository = recipeRepository;
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }


    

@PostMapping("/ingest")
public ResponseEntity<String> ingestRecipes() {
    try {
        Resource resource = resourceLoader.getResource("classpath:US_recipes_null.json");
        List<Recipe> allRecipes = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<Recipe>>() {});

        int batchSize = 1500; 
        int totalIngested = 0;

        for (int i = 0; i < allRecipes.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, allRecipes.size());
            List<Recipe> batch = allRecipes.subList(i, endIndex);
            recipeRepository.saveAll(batch);
            totalIngested += batch.size();
        }

        return ResponseEntity.ok("Ingested " + totalIngested + " recipes successfully.");
    } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.badRequest().body("Failed to ingest recipes: " + e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body("An unexpected error occurred: " + e.getMessage());
    }
}


    @GetMapping
    public Page<Recipe> getRecipesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Sort sort = Sort.by(Sort.Direction.DESC, "prepTime");
        Pageable pageable = PageRequest.of(page, size, sort);
        return recipeRepository.findAll(pageable);
    }


    @GetMapping("/search")
    public List<Recipe> searchRecipes(@RequestParam String cuisine, @RequestParam String ingredient) {
        return recipeRepository.findByCuisineIgnoreCaseAndIngredientsContainingIgnoreCase(cuisine, ingredient);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Recipe> findById(@PathVariable String id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        return recipe.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable String id, @RequestBody Recipe updatedRecipe) {
        return recipeRepository.findById(id).map(recipe -> {
            recipe.setName(updatedRecipe.getName());
            recipe.setCuisine(updatedRecipe.getCuisine());
            recipe.setIngredients(updatedRecipe.getIngredients());
            recipe.setPrepTime(updatedRecipe.getPrepTime());
            return ResponseEntity.ok(recipeRepository.save(recipe));
        }).orElse(ResponseEntity.notFound().build());
    }


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
