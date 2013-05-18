/* 
 *  Copyright (C) 2013 Alex Chavez <alex@alexchavez.net>
 *  California State University Long Beach (CSULB) ALL RIGHTS RESERVED
 * 
 *  Use of this software is authorized for CSULB students in Dr. Monge's classes, so long
 *  as this copyright notice remains intact. Students must request permission from Dr. Monge
 *  if the code is to be used in other venues outside of Dr. Monge's classes.
 * 
 *  This program is distributed to CSULB students in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 */

$ = jQuery;
var gridster;

$(function() {
    gridster = $(".gridster ul").gridster({
        widget_margins: [5, 5],
        widget_base_dimensions: [100, 100],
        min_cols: 6,
        avoid_overlapped_widgets: true
    }).data('gridster');

    // Disable dragging and drop widgets
    gridster.disable();

    refreshGridster();
});

function addGridsterWidgets(recipeId, recipeName, recipeImagePath) {
    var randomNumberX = Math.floor(Math.random() * 3);
    var randomNumberY = Math.floor(Math.random() * 3);

    var html = '<a id=' + recipeId + ' href="/faces/recipe.xhtml?recipe=' + recipeId + '">\n\
                    <div >\n\
                        <li>' + recipeName + '</li>\n\
                    </div>\n\
                </a>'

    gridster.add_widget(html, randomNumberX, randomNumberY);
    var cssUrl = 'url(' + recipeImagePath +')';
    var cssID = '#' + recipeId + '.gs_w';
    $(cssID).css('background-image', cssUrl);
    $(cssID).css('background-size','300px 300px');
}

function refreshGridster () {
    gridster.remove_all_widgets();
    var recipeJSONString = $('#json').val();
    parseJSONAndCreateWidgets(recipeJSONString);
}

function parseJSONAndCreateWidgets(recipeJSONString) {
    var recipeJSONObject = JSON.parse(recipeJSONString);
    var firstProp;
    for(var key in recipeJSONObject) {
        if(recipeJSONObject.hasOwnProperty(key)) {
            firstProp = recipeJSONObject[key];
            break;
        }
    }

    var recipeListString = new String(firstProp);
    var recipeList = recipeListString.split('|');

    for (i in recipeList) {
        var recipeListElement = new String(recipeList[i]);
        var recipeListArray = recipeListElement.split(',');

        var recipeId = recipeListArray[0];
        var recipeName = recipeListArray[1];
        var recipeImagePath = recipeListArray[2];

        addGridsterWidgets(recipeId, recipeName, recipeImagePath);
    }
}

