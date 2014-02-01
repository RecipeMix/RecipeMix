/*
 *  Copyright (C) 2013 Alex Chavez <alex@alexchavez.net>, Jairo Lopez <jlopez@csulbmaes.org>,
 *  Steven Paz <steve.a.paz@gmail.com>, Gustavo Rosas <gustavoscrib@yahoo.com>
 *
 *  RecipeMix ALL RIGHTS RESERVED
 *
 *  This program is distributed to users in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package recipemix.beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import org.primefaces.json.JSONArray;
import recipemix.models.NutritionixInfo;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;

/**
 **Enterprise JavaBean belonging to nutritionix entity
 * @author Alex Chavez <alex@alexchavez.net>
 */
@Stateless
public class NutritionixEJB {

    public static final String APPLICATION_ID = "4a658548";
    public static final String APPLICATION_KEY = "2ad8de396f68950e8c61e77f03279841";
    public static final String NUTRITIONIX_SEARCH_ENDPOINT = "http://api.nutritionix.com/v1/search/";
    public static final String NUTRITIONIX_ITEM_ENDPOINT = "http://api.nutritionix.com/v1/item/";
    public static final String USDA_BRAND_ID = "513fcc648110a4cafb90ca5e";

    public NutritionixEJB() {

    }
    
    public StringBuilder createNutritionixRequest(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        String line;
        StringBuilder builder = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return builder;
    }

    public NutritionixInfo getIngredientInfo(NutritionixInfo ni, String id) throws MalformedURLException, IOException, JSONException {
        URL url = new URL(NUTRITIONIX_ITEM_ENDPOINT
                            + id
                            + "?appId=" + APPLICATION_ID
                            + "&appKey=" + APPLICATION_KEY);
        
        StringBuilder builder = createNutritionixRequest(url);
        
        // Get JSON and instantiate a Java object
        JSONObject json = new JSONObject(builder.toString());

        // Set basic Nutritionix item info
        ni.setItemId(json.getString("item_id"));
        ni.setItemName(json.getString("item_name"));
        ni.setItemDescription(json.getString("item_description"));

        // Set Nutritionix brand info -- I expecte this to be USDA only for now
        ni.setBrandId(json.getString("brand_id"));
        ni.setBrandName(json.getString("brand_name"));

        // Set nutrition fact info
        ni.setNfCalciumDv(parseJSONDouble(json, "nf_calcium_dv"));
        ni.setNfCalories(parseJSONInteger(json, "nf_calories"));
        ni.setNfCaloriesFromFat(parseJSONInteger(json, "nf_calories_from_fat"));
        ni.setNfCholesterol(parseJSONInteger(json, "nf_cholesterol"));
        ni.setNfDietaryFiber(parseJSONInteger(json, "nf_dietary_fiber"));
        ni.setNfIronDv(parseJSONDouble(json, "nf_iron_dv"));
        ni.setNfProtein(parseJSONInteger(json, "nf_protein"));
        ni.setNfSaturatedFat(parseJSONInteger(json, "nf_saturated_fat"));
        ni.setNfSodium(parseJSONInteger(json, "nf_sodium"));
        ni.setNfSugars(parseJSONInteger(json, "nf_sugars"));
        ni.setNfTotalCarbohydrate(parseJSONInteger(json, "nf_total_carbohydrate"));
        ni.setNfTotalFat(parseJSONInteger(json, "nf_total_fat"));
        //ni.setNfTransFat(0);    
        ni.setNfTransFat(parseJSONInteger(json, "nf_trans_fatty_acid"));
        ni.setNfVitaminADv(parseJSONDouble(json, "nf_vitamin_a_dv"));
        ni.setNfVitaminCDv(parseJSONDouble(json, "nf_vitamin_c_dv"));
        
        return ni;
    }
    
    public double parseJSONDouble(JSONObject json, String jsonField) throws JSONException {
        double number;
        try
        {
            double value = json.getDouble(jsonField);
            number = value;
        }
        catch (Exception e)
        {
            return 0;
        }
        return number;
    }
    
    public int parseJSONInteger(JSONObject json, String jsonField) throws JSONException {
        int number;
        try
        {
            int value = json.getInt(jsonField);
            number = value;
        }
        catch (Exception e)
        {
            return 0;
        }
        return number;
    }

    public NutritionixInfo searchForIngredient(String searchPhrase, int resultStartIndex, int resultEndIndex) throws MalformedURLException, IOException, JSONException {

        String id;
        
        // Create an instance of the Nutritionix Info model and set all the fields! ^^
        NutritionixInfo ni = new NutritionixInfo();
        
        URL url = new URL(NUTRITIONIX_SEARCH_ENDPOINT
                            + searchPhrase
                            + "?brand_id=" + USDA_BRAND_ID
                            + "&results=" + resultStartIndex + "%3A" + resultEndIndex
                            + "&fields=*"
                            + "&appId=" + APPLICATION_ID
                            + "&appKey=" + APPLICATION_KEY);

        StringBuilder builder = createNutritionixRequest(url);       
        JSONObject json = new JSONObject(builder.toString());
        
        JSONArray hitsArray = json.getJSONArray("hits");
        id = hitsArray.getJSONObject(0).getString("_id");

        if (id != null) {
            ni = getIngredientInfo(ni, id);
        } else {
            ni = null;
        }
        
        return ni;
    }
    
}
