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
package recipemix.models;

/**
 * This class stores the information required to display the nutritionix information
 * for the recipes
 * @author Alex Chavez <alex@alexchavez.net>
 */
public class NutritionixInfo {
    
    // Basic item description fields
    private String itemId;
    private String itemDescription;
    private String itemName;
    
    // Fields related to an ingredient's brand -- I expect this to be USDA type of stuff
    private String brandId;
    private String brandName;

    // Nutritional facts
    private int nfCalories;
    private int nfCaloriesFromFat;
    private int nfCholesterol;
    private int nfDietaryFiber;
    private int nfProtein;
    private int nfSaturatedFat;
    private int nfSodium;
    private int nfSugars;
    private int nfTotalCarbohydrate;
    private int nfTotalFat;
    private int nfTransFat;
    
    private double nfCalciumDv;
    private double nfIronDv;
    private double nfVitaminADv;
    private double nfVitaminCDv;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }


    public double getNfCalciumDv() {
        return nfCalciumDv;
    }

    public void setNfCalciumDv(double nfCalciumDv) {
        this.nfCalciumDv = nfCalciumDv;
    }

    public int getNfCalories() {
        return nfCalories;
    }

    public void setNfCalories(int nfCalories) {
        this.nfCalories = nfCalories;
    }

    public int getNfCaloriesFromFat() {
        return nfCaloriesFromFat;
    }

    public void setNfCaloriesFromFat(int nfCaloriesFromFat) {
        this.nfCaloriesFromFat = nfCaloriesFromFat;
    }

    public int getNfCholesterol() {
        return nfCholesterol;
    }

    public void setNfCholesterol(int nfCholesterol) {
        this.nfCholesterol = nfCholesterol;
    }

    public int getNfDietaryFiber() {
        return nfDietaryFiber;
    }

    public void setNfDietaryFiber(int nfDietaryFiber) {
        this.nfDietaryFiber = nfDietaryFiber;
    }

    public double getNfIronDv() {
        return nfIronDv;
    }

    public void setNfIronDv(double nfIronDv) {
        this.nfIronDv = nfIronDv;
    }

    public int getNfProtein() {
        return nfProtein;
    }

    public void setNfProtein(int nfProtein) {
        this.nfProtein = nfProtein;
    }

    public int getNfSaturatedFat() {
        return nfSaturatedFat;
    }

    public void setNfSaturatedFat(int nfSaturatedFat) {
        this.nfSaturatedFat = nfSaturatedFat;
    }

    public int getNfSodium() {
        return nfSodium;
    }

    public void setNfSodium(int nfSodium) {
        this.nfSodium = nfSodium;
    }

    public int getNfSugars() {
        return nfSugars;
    }

    public void setNfSugars(int nfSugars) {
        this.nfSugars = nfSugars;
    }

    public int getNfTotalCarbohydrate() {
        return nfTotalCarbohydrate;
    }

    public void setNfTotalCarbohydrate(int nfTotalCarbohydrate) {
        this.nfTotalCarbohydrate = nfTotalCarbohydrate;
    }

    public int getNfTotalFat() {
        return nfTotalFat;
    }

    public void setNfTotalFat(int nfTotalFat) {
        this.nfTotalFat = nfTotalFat;
    }

    public double getNfVitaminADv() {
        return nfVitaminADv;
    }

    public void setNfVitaminADv(double nfVitaminADv) {
        this.nfVitaminADv = nfVitaminADv;
    }

    public double getNfVitaminCDv() {
        return nfVitaminCDv;
    }

    public void setNfVitaminCDv(double nfVitaminCDv) {
        this.nfVitaminCDv = nfVitaminCDv;
    }

    public int getNfTransFat() {
        return nfTransFat;
    }

    public void setNfTransFat(int nfTransFat) {
        this.nfTransFat = nfTransFat;
    }
    
}
