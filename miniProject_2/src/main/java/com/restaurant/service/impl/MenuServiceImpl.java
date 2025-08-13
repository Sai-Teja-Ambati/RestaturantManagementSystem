package com.restaurant.service.impl;

import com.restaurant.service.MenuService;
import com.restaurant.util.RecipeUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MenuServiceImpl implements MenuService {

    @Override
    public Map<String, Object> getFullMenu() {
        return RecipeUtil.getMenuWithPrices();
    }

    @Override
    public Map<String, Integer> getRecipeIngredients(String dishName) {
        return RecipeUtil.getRecipeIngredients(dishName);
    }

    @Override
    public boolean isRecipeAvailable(String dishName) {
        return RecipeUtil.hasRecipe(dishName);
    }
}