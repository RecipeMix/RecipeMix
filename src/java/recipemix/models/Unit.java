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
 * Enumerated values for the different units of measurement
 *
 * @author Alex Chavez <alex@alexchavez.net>
 */
public enum Unit {

    none,
    cup,
    dozen,
    fluid_ounce,
    gallon,
    gram,
    liter,
    ounce,
    pinch,
    pint,
    pound,
    quart,
    square,
    tablespoon,
    teaspoon;

    @Override
    public String toString() {
        String unit = this.name();

        if (unit.equals("fluid_ounce")) {
            unit = "fl oz";
        } else if (unit.equals("gallon")) {
            unit = "gal";
        } else if (unit.equals("liter")) {
            unit = "l";
        } else if (unit.equals("ounce")) {
            unit = "oz";
        } else if (unit.equals("pound")) {
            unit = "lb";
        } else if (unit.equals("tablespoon")) {
            unit = "tbsp";
        } else if (unit.equals("teaspoon")) {
            unit = "tsp";
        }
        
        return unit;
    }
}