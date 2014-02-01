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

function addGridsterWidgets(groupId, groupName, groupImagePath) {
    var randomNumberX = Math.floor(Math.random() * 3);
    var randomNumberY = Math.floor(Math.random() * 3);

    var html = '<a id=' + groupId + ' href="/faces/group_profile.xhtml?groupID=' + groupId + '">\n\
                    <div >\n\
                        <li>' + groupName + '</li>\n\
                    </div>\n\
                </a>'
    
    gridster.add_widget(html, randomNumberX, randomNumberY);
    var cssUrl = 'url(' + groupImagePath +')';
    var cssID = '#' + groupId + '.gs_w';
    $(cssID).css('background-image', cssUrl);
    $(cssID).css('background-size','300px 300px');
}

function refreshGridster () {
    gridster.remove_all_widgets();
    var groupJSONString = $('#json').val();
    console.log(groupJSONString);
    parseJSONAndCreateWidgets(groupJSONString);
}

function parseJSONAndCreateWidgets(groupJSONString) {
    var groupJSONObject = JSON.parse(groupJSONString);
    var firstProp;
    for(var key in groupJSONObject) {
        if(groupJSONObject.hasOwnProperty(key)) {
            firstProp = groupJSONObject[key];
            break;
        }
    }

    var groupListString = new String(firstProp);
    var groupList = groupListString.split('|');

    for (i in groupList) {
        var groupListElement = new String(groupList[i]);
        var groupListArray = groupListElement.split(',');

        var groupId = groupListArray[0];
        var groupName = groupListArray[1];
        var groupImagePath = groupListArray[2];

        addGridsterWidgets(groupId, groupName, groupImagePath);
    }
}

