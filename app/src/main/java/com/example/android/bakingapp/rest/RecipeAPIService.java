package com.example.android.bakingapp.rest;

import com.example.android.bakingapp.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeAPIService
{

    @GET("2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> listRecipes();

}