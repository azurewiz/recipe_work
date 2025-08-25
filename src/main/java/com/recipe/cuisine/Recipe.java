package com.recipe.cuisine;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

@Document(collection = "recipes")
@Data
@NoArgsConstructor
public class Recipe {

    @Id
    private String id;
    
    // The key "0", "1", etc. from the JSON file
    // We will handle this in the ingestion logic

    @Field("Contient")
    private String continent;
    @Field("Country_State")
    private String countryState;
    private String cuisine;
    private String title;
    private String URL;
    private Double rating;
    private Integer total_time;
    private Integer prep_time;
    private Integer cook_time;
    private String description;
    private List<String> ingredients;
    private List<String> instructions;
    private Nutrients nutrients;
    private String serves;
}