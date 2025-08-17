package zeta.foods.model;

import java.util.Map;
import java.util.TreeMap;

public class Recipe {
    private Long id;
    private String name;
    private Map<String, Integer> ingredients = new TreeMap<>();
    private String instructions;

    public Recipe() {}

    public Recipe(Long id, String name, Map<String, Integer> ingredients, String instructions) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Integer> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Map<String, Integer> ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
