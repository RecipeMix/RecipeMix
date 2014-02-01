/*
 *  Copyright (C) 2013 Alex Chavez <alex@alexchavez.net>, Jairo Lopez <jlopez@csulbmaes.org>,
 *  Steven Paz <steve.a.paz@gmail.com>, Gustavo Rosas <gustavoscrib@yahoo.com>
 *
 *  GroupsMix ALL RIGHTS RESERVED
 *
 *  This program is distributed to groups in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package recipemix.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import recipemix.beans.GroupEJB;
import recipemix.models.Groups;

/**
 * Reference:
 * http://www.simtay.com/part-4-lazy-loading-and-pagination-with-jsf-2-1-primefaces-3-5-datatable-and-form-validation/
 * Reference: Primefaces Showcase
 *
 * Lazy Data Model used for pagination
 *
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
public class LazyGroupDataModel extends LazyDataModel<Groups> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("LazyGroupsDataModel");
    
    private List<Groups> dataSource;
    private GroupEJB groupEJB;
    private int pageSize;
    private int rowIndex;
    private int rowCount;

    public LazyGroupDataModel(GroupEJB groupEJB) {
        this.groupEJB = groupEJB;
    }

     /**
     * Returns the group object at the specified position in dataSource.
     * @return 
     */
    @Override
    public Groups getRowData() {
        if(dataSource == null)
            return null;
        int index =  rowIndex % pageSize;
        if(index > dataSource.size()){
            return null;
        }
        return dataSource.get(index);
    }
    
    /**
     * Returns the group object that has the row key.
     * @param rowKey
     * @return 
     */
    @Override
    public Groups getRowData(String rowKey) {
        if(dataSource == null)
            return null;
       for(Groups group : dataSource) {  
           if(Integer.toString(group.getGroupID()).equals(rowKey));
           return group;  
       }  
       return null;  
    }

    /**
     * Returns the ID for the given group
     *
     * @param group - the Groups
     * @return - the Groups ID
     */
    @Override
    public Integer getRowKey(Groups group) {
        return group.getGroupID();
    }

    /**
     * Loads a "page"
     *
     * @param first - index of the first item in the page
     * @param pageSize - the number of elements to display
     * @param sortField - the field to sort by
     * @param sortOrder
     * @param filters
     * @return - the List ofGroupss to display
     */
    @Override
    public List<Groups> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        List<Groups> data = new ArrayList<Groups>();
        dataSource = groupEJB.findWithNamedQuery(Groups.FIND_ALL_GROUPS, first, first + pageSize);
        this.setRowCount(groupEJB.countTotalGroups());

        // Apply filters
        for (Groups group : dataSource) {
            boolean match = true;

            // Iterate through the given filters
            for (Iterator<String> it = filters.keySet().iterator(); it.hasNext();) {
                try {
                    String filterProperty = it.next();
                    String filterValue = filters.get(filterProperty);
                    
                    if(filterProperty.equals("groupName")){
                        match = (group.getGroupName().toLowerCase().startsWith(filterValue.toLowerCase()));
                    }
                    else if(filterProperty.equals("groupId")){
                        match = (filterValue.equals(Integer.toString(group.getGroupID())));
                    } 
                    else {
                        match = false;
                        break;
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, e.getMessage());
                    match = false;
                }
            }

            if (match) {
                data.add(group);
            }
        }

        // sort the results 
        if (sortField != null) {
            Collections.sort(data, new LazySorter(sortField, sortOrder));
        }

        //rowCount  
        int dataSize = data.size();

        //paginate  
        if (dataSize > pageSize) {
            try {
                return data.subList(first, first + pageSize);
            } catch (IndexOutOfBoundsException e) {
                return data.subList(first, first + (dataSize % pageSize));
            }
        } else {
            return data;
        }
    }
    
    @Override
    public boolean isRowAvailable() {
        if(dataSource == null)
            return false;
        int index = rowIndex % pageSize ;
        return index >= 0 && index < dataSource.size();
    }
    
    /**
     *
     * @param pageSize
     */
    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Returns page size
     * @return int
     */
    @Override
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Returns current row index
     * @return int
     */
    @Override
    public int getRowIndex() {
        return this.rowIndex;
    }
    
    /**
     * Sets row index
     * @param rowIndex
     */
    @Override
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    /**
     * Sets row count
     * @param rowCount
     */
    @Override
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }
    
    /**
     * Returns row count
     * @return int
     */
    @Override
    public int getRowCount() {
        return this.rowCount;
    }
     
    /**
     * Sets wrapped data
     * @param list
     */
    @Override
    public void setWrappedData(Object list) {
        this.dataSource = (List<Groups>) list;
    }
    
    /**
     * Returns wrapped data
     * @return
     */
    @Override
    public Object getWrappedData() {
        return dataSource;
    }
    
}
