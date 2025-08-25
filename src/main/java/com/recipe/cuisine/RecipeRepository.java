package com.recipe.cuisine;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {
    
    // Find recipes by cuisine and a specific ingredient.
    List<Recipe> findByCuisineAndIngredientsContaining(String cuisine, String ingredient);
   

    // Custom query to search where nutrients.calories is <= a specified value.
    @Query("{ 'nutrients.calories' : { $lte : ?0 } }")
    List<Recipe> findByNutrientsCaloriesLessThanEqual(String maxCalories);
}


