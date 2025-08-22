package com.recipe.cuisine;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Document(collection = "recipes")
@Data
public class Recipe {

    @Id
    private String id;

    @JsonProperty("title")
    private String name;

    private String cuisine;

    @JsonProperty("ingredients")
    private List<String> ingredients;

    @JsonProperty("prep_time")
    private int prepTime;

    @JsonProperty("rating")
    private double rating;

    @JsonProperty("nutrients") 
    private Nutrients nutrients; 
   
    @JsonProperty("Contient")
    private String continent; 
    @JsonProperty("Country_State")
    private String countryState;
    @JsonProperty("URL")
    private String url;
    @JsonProperty("total_time")
    private int totalTime;
    @JsonProperty("description")
    private String description;
    @JsonProperty("instructions")
    private List<String> instructions;
 
    private String serves; 


    @Data 
    public static class Nutrients { 
        

      
        @JsonProperty("carbohydrateContent")
        private String carbohydrateContent;
        @JsonProperty("cholesterolContent")
        private String cholesterolContent;
        @JsonProperty("fiberContent")
        private String fiberContent;
        @JsonProperty("proteinContent")
        private String proteinContent;
        @JsonProperty("saturatedFatContent")
        private String saturatedFatContent;
        @JsonProperty("sodiumContent")
        private String sodiumContent;
        @JsonProperty("sugarContent")
        private String sugarContent;
        @JsonProperty("fatContent")
        private String fatContent;
        @JsonProperty("unsaturatedFatContent")
        private String unsaturatedFatContent;
    }
}
