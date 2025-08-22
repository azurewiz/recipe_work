/*package com.recipe.cuisine;

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
import java.util.Optional;

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

    
    @PostMapping("/ingest")
    public ResponseEntity<String> ingestRecipes() {
        try {
            List<Recipe> recipes = objectMapper.readValue(
                this.getClass().getResourceAsStream("US_recipes_null.json"),
                new TypeReference<List<Recipe>>() {}
            );
            recipeRepository.saveAll(recipes);
            return ResponseEntity.ok("Ingested " + recipes.size() + " recipes successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to ingest recipes: " + e.getMessage());
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
        return recipeRepository.findByCuisineAndIngredientsContaining(cuisine, ingredient);
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
import java.util.ArrayList;
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
        return recipeRepository.findByCuisineAndIngredientsContaining(cuisine, ingredient);
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
}*/

package com.recipe.cuisine;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    
@Data 
class RecipeListResponse {
    private int page;
    private int limit;
    private long total; 
    private List<RecipeDto> data;

    @Data
    static class RecipeDto { 
        private String id; 
        private String title; 
        private String cuisine;
        private double rating;
        @JsonProperty("prep_time") 
        private int prepTime;
        
    }
}


    @PostMapping("/ingest")
    public ResponseEntity<String> ingestRecipes() {
        try {
            Resource resource = resourceLoader.getResource("classpath:US_recipes_null.json");

            Map<String, Recipe> recipesMap = objectMapper.readValue(
                resource.getInputStream(),
                new TypeReference<Map<String, Recipe>>() {} 
            );

           
            List<Recipe> allRecipes = new ArrayList<>(recipesMap.values());

            int batchSize = 1000;
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

    /*@GetMapping
    public Page<Recipe> getRecipesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Sort sort = Sort.by(Sort.Direction.DESC, "prepTime");
        Pageable pageable = PageRequest.of(page, size, sort);
        return recipeRepository.findAll(pageable);
    }*/
    
    
@GetMapping
public ResponseEntity<RecipeListResponse> getRecipesPaginated(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int limit) { 
    Sort sort = Sort.by(Sort.Direction.DESC, "prepTime");
    Pageable pageable = PageRequest.of(page, limit, sort); 
    Page<Recipe> recipePage = recipeRepository.findAll(pageable);

   
    RecipeListResponse response = new RecipeListResponse();
    response.setPage(recipePage.getNumber()); 
    response.setLimit(recipePage.getSize());  
    response.setTotal(recipePage.getTotalElements()); 

    
    List<RecipeListResponse.RecipeDto> recipeDtos = recipePage.getContent().stream()
            .map(recipe -> {
                RecipeListResponse.RecipeDto dto = new RecipeListResponse.RecipeDto();
                dto.setId(recipe.getId());
                dto.setTitle(recipe.getName()); 
                dto.setCuisine(recipe.getCuisine());
                dto.setRating(recipe.getRating()); 
                dto.setPrepTime(recipe.getPrepTime());
               
                return dto;
            })
            .collect(java.util.stream.Collectors.toList());

    response.setData(recipeDtos);

    return ResponseEntity.ok(response);
}

 
    @GetMapping("/search")
    public List<Recipe> searchRecipes(@RequestParam String cuisine, @RequestParam String ingredient) {
        return recipeRepository.findByCuisineIgnoreCaseAndIngredientsContainingIgnoreCase(cuisine, ingredient);
    }
    @GetMapping("/search/by-calories") 
    public List<RecipeListResponse.RecipeDto> searchRecipesByCalories(
            @RequestParam int maxCalories) { 
        
        List<Recipe> foundRecipes = recipeRepository.findByNutrients_CaloriesLessThanEqual(maxCalories);
        

        return foundRecipes.stream()
                .map(recipe -> {
                    RecipeListResponse.RecipeDto dto = new RecipeListResponse.RecipeDto();
                    dto.setId(recipe.getId());
                    dto.setTitle(recipe.getName());
                    dto.setCuisine(recipe.getCuisine());
                    dto.setRating(recipe.getRating());
                    dto.setPrepTime(recipe.getPrepTime());
                    return dto;
                })
                .collect(Collectors.toList());
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



/*// In RecipeController.java

// ... (other imports)
import java.util.stream.Collectors; // Add this import

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    // ... (constructor and existing methods)

    @GetMapping("/search/by-calories") // New endpoint for calories search
    public List<RecipeListResponse.RecipeDto> searchRecipesByCalories(
            @RequestParam int maxCalories) { // Renamed parameter for clarity
        
        List<Recipe> foundRecipes = recipeRepository.findByNutrients_CaloriesLessThanEqual(maxCalories);
        
        // Map Recipe entities to RecipeDto for the response
        return foundRecipes.stream()
                .map(recipe -> {
                    RecipeListResponse.RecipeDto dto = new RecipeListResponse.RecipeDto();
                    dto.setId(recipe.getId());
                    dto.setTitle(recipe.getName());
                    dto.setCuisine(recipe.getCuisine());
                    dto.setRating(recipe.getRating());
                    dto.setPrepTime(recipe.getPrepTime());
                    // Add other fields you want in the DTO
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ... (rest of your controller methods)
}
curl -X POST http://localhost:8080/api/recipes/ingest
curl "http://localhost:8080/api/recipes/search/by-calories?maxCalories=400"
 */
