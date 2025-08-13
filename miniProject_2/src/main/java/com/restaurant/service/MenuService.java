package com.restaurant.service;

import java.util.Map;

public interface MenuService {
    
    Map<String, Object> getFullMenu();
    
    Map<String, Integer> getRecipeIngredients(String dishName);
    
    boolean isRecipeAvailable(String dishName);
}