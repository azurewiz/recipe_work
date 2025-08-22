# recipe_work

## 1. Logic Used to Parse JSON File, Store Data, and APIs
### 1.1 JSON Parsing Logic
Goal: To ingest a large JSON file (US_recipes_null.json) containing 8451 recipe entries into a MongoDB Atlas database.
Initial Problem: The JSON file was formatted as a single JSON object at the top level, with numbered keys like "0", "1", etc., each containing a recipe's data. The RecipeController initially attempted to deserialize the entire file directly into a List<Recipe>, resulting in a Cannot deserialize value... from Object value error because a [ (array) was expected, but a { (object) was received.
Solution: The code was updated to deserialize the top-level JSON into a Map<String, Recipe>. This correctly handled the structure where keys are string representations of numbers and values are Recipe objects. The values() from this map were then extracted into a List<Recipe>.
Nested Fields: The JSON also contains nested objects like "nutrients". To correctly parse these and use them for querying was difficult and is still under work.
Field Mapping: Jackson's @JsonProperty annotation was used extensively in the Recipe and Nutrients classes to map JSON field names (e.g., "title", "prep_time", "Contient") that differ from the Java field names. 

### 1.2 Data Storage in MongoDB Atlas
Database: MongoDB Atlas is used as the document database.
Connection: The connection to the MongoDB Atlas cluster is configured in the src/main/resources/application.properties file using the spring.data.mongodb.uri property. This URI includes the username, password, cluster URL, and database name. Network access rules in MongoDB Atlas are configured to allow connections from the application's IP address.
Data Model: The Recipe.java class acts as the data model (or "Document") for MongoDB. It's annotated with @Document(collection = "recipes") to specify the MongoDB collection name. Fields like id are annotated with @Id for primary key handling.
Repository: Spring Data MongoDB's MongoRepository interface (RecipeRepository.java) provides convenient methods for database operations (CRUD - Create, Read, Update, Delete) without needing to write boilerplate code.
Batch Ingestion: To handle the large volume of recipes (8451), the ingestRecipes() method implements batch processing. It reads all recipes into a List<Recipe>, then iterates through this list, saving smaller chunks (batches) of recipes at a time using recipeRepository.saveAll(). This improves performance and memory management compared to trying to save all items in a single transaction. 

### 1.3 REST APIs
Framework: Spring Boot is used to build RESTful APIs.
Controller: The RecipeController.java class, annotated with @RestController and @RequestMapping("/api/recipes"), handles incoming HTTP requests.
Endpoints:
POST /api/recipes/ingest: Triggers the JSON file parsing and data ingestion into the database.
GET /api/recipes: Retrieves recipes with pagination (using page and limit parameters) and sorting (by prepTime in descending order). The raw Page<Recipe> output is transformed into a custom RecipeListResponse DTO for a cleaner API response structure, according to a DZone article on Spring Boot MongoDB integration.
GET /api/recipes/search: Searches recipes by cuisine and ingredient.
GET /api/recipes/search/by-calories: Searches recipes where nutrients.calories is less than or equal to a specified maxCalories.
GET /api/recipes/{id}: Retrieves a single recipe by its ID.
PUT /api/recipes/{id}: Updates an existing recipe by its ID.
DELETE /api/recipes/{id}: Deletes a recipe by its ID.
Request/Response Handling: @RequestBody is used for POST and PUT methods to bind incoming JSON to Java objects. @RequestParam is used to extract query parameters. ResponseEntity is used to construct responses, allowing control over HTTP status codes (e.g., 200 OK, 400 Bad Request, 404 Not Found). 

## 2. MongoDB Schema and Setup
### 2.1 MongoDB Atlas Setup
Create a MongoDB Atlas Account and Project: If you haven't already, sign up for MongoDB Atlas and create a new project.
Build a Cluster: Within your project, create a new shared or dedicated cluster. Choose the region closest to you.
Configure Network Access:
Go to Database Access -> Network Access.
Click "Add IP Address".
Add your current IP address (select "Add Current IP Address") or, for broad access (only for development), add "0.0.0.0/0" to allow access from anywhere.
Create a Database User:
Go to Database Access -> Database Users.
Click "Add New Database User".
Create a user with a strong password (e.g., said and azurewiz from your example).
Grant "Read and write to any database" privileges, or restrict to a specific database (recipes in this case).
Get Connection String:
Go to Database -> Clusters.
Click "Connect" for your cluster.
Choose "Connect Your Application".
Select "Java" and the latest driver version.
Copy the connection string provided. It will look like mongodb+srv://<username>:<password>@cluster0.yourclusterid.mongodb.net/?retryWrites=true&w=majority. 

### 2.2 Database Structure (Schema)

In MongoDB, collections are analogous to tables in relational databases, and documents are analogous to rows. MongoDB is schemaless, meaning documents within the same collection don't have to have the same fields. However, for your Spring Boot application, your Recipe.java class implicitly defines the expected structure or schema of documents in the recipes collection. 
Collection: recipes

Example Document Structure (based on Recipe.java):
json
{
  "_id": "60f0c0b3f0c0b3f0c0b3f0c0", // Auto-generated by MongoDB, mapped to 'id' field in Recipe.java
  "name": "Sweet Potato Pie", // Mapped from JSON "title"
  "cuisine": "Southern Recipes",
  "ingredients": [
    "1 (1 pound) sweet potato, with skin",
    "0.5 cup butter, softened",
    
  ],
  "prepTime": 15, // Mapped from JSON "prep_time"
  "rating": 4.8, // Mapped from JSON "rating"
  "nutrients": { // Nested Document
    "caloriesString": "389 kcal", // Stores the original string value
    "calories": 389, // Stores the parsed integer value, used for querying
    "carbohydrateContent": "48 g",
    "cholesterolContent": "78 mg",
    "fiberContent": "3 g",
    "proteinContent": "5 g",
    "saturatedFatContent": "10 g",
    "sodiumContent": "254 mg",
    "sugarContent": "28 g",
    "fatContent": "21 g",
    "unsaturatedFatContent": "0 g"
  },
  "continent": "North America", // Mapped from JSON "Contient"
  "countryState": "US", // Mapped from JSON "Country_State"
  "url": "https://www.allrecipes.com/recipe/12142/sweet-potato-pie-i/", // Mapped from JSON "URL"
  "totalTime": 115, // Mapped from JSON "total_time"
  "description": "Shared from a Southern recipe, ...",
  "instructions": [
    "Place whole sweet potato in pot...",
  ],
  "serves": "8 servings"
}

## 3. API Testing Instructions
### 3.1 Setup
Clone/Copy Project: Get the Spring Boot project files onto your machine.
Ensure MongoDB Atlas is Set Up: Follow the steps in Section 2.1 to configure your MongoDB Atlas cluster, database user, and network access.
Place US_recipes_null.json: Ensure the US_recipes_null.json file is located in your src/main/resources directory and has the correct JSON object format (keys like "0", "1" mapping to recipe objects).
Update application.properties: Edit src/main/resources/application.properties to include your specific MongoDB Atlas connection URI (with correct username, password, and database name).
Build the Project: Open a terminal in the project's root directory and run mvn clean install or ./gradlew clean build.
Run the Spring Boot Application:
Using Maven: mvn spring-boot:run
Using IDE (e.g., IntelliJ IDEA): Right-click CuisineApplication.java and select "Run".
Using executable JAR: java -jar target/cuisine-0.0.1-SNAPSHOT.jar (after running mvn clean install)
Confirm Application Startup: Look for console output indicating the application has started and is listening on server.port = 8080 and connected to MongoDB. 

### 3.2 Sample Requests (using curl)
Use the terminal or a tool like Postman/Insomnia to send these requests. Replace <ID> with an actual recipe ID retrieved from the GET /api/recipes or database. 
#### 1. Ingest Recipes (POST)
Purpose: Load the recipes from US_recipes_null.json into the MongoDB database.
Endpoint: POST http://localhost:8080/api/recipes/ingest

curl -X POST http://localhost:8080/api/recipes/ingest
output:Ingested 8451 recipes successfully.

#### 2. Get Paginated Recipes (GET)
Purpose: Retrieve a list of recipes with pagination. Pages are 0-indexed.
Endpoint: GET http://localhost:8080/api/recipes
bash: curl "http://localhost:8080/api/recipes?page=1&limit=10" 
output recieved:{"page":1,"limit":10,"total":8451,"data":[{"id":"68a81ee602503769cc144f71","title":null,"cuisine":"Southern Recipes","rating":0.0,"prep_time":0},{"id":"68a81ee602503769cc144f6f","title":null,"cuisine":"Southern Recipes","rating":0.0,"prep_time":0},{"id":"68a81ee602503769cc144f69","title":null,"cuisine":"Southern Recipes","rating":0.0,"prep_time":0},{"id":"68a81ee602503769cc144f73","title":null,"cuisine":"Southern Recipes","rating":0.0,"prep_time":0},{"id":"68a81ee602503769cc144f65","title":null,"cuisine":"Southern Recipes","rating":0.0,"prep_time":0},{"id":"68a81ee602503769cc144f6b","title":null,"cuisine":"Southern Recipes","rating":0.0,"prep_time":0},{"id":"68a81ee602503769cc144f62","title":null,"cuisine":"Southern Recipes","rating":0.0,"prep_time":0},{"id":"68a81ee602503769cc144f64","title":null,"cuisine":"Southern Recipes","rating":0.0,"prep_time":0},{"id":"68a81ee602503769cc144f68","title":null,"cuisine":"Southern Recipes","rating":0.0,"prep_time":0},{"id":"68a81ee602503769cc144f70","title":null,"cuisine":"Southern Recipes","rating":0.0,"prep_time":0}]}

#### 3. Search Recipes by Cuisine and Ingredient (GET)
Purpose: Find recipes matching specific cuisine and ingredient criteria. Remember to handle case sensitivity based on your data and whether you implemented case-insensitive search.
Endpoint: GET http://localhost:8080/api/recipes/search

bash:curl "http://localhost:8080/api/recipes/search?cuisine=American&ingredient=Chicken”    
output :[]



