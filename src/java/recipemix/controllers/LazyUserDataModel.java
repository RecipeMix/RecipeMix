/*
 *  Copyright (C) 2013 Alex Chavez <alex@alexchavez.net>, Jairo Lopez <jlopez@csulbmaes.org>,
 *  Steven Paz <steve.a.paz@gmail.com>, Gustavo Rosas <gustavoscrib@yahoo.com>
 *
 *  CommentMix ALL RIGHTS RESERVED
 *
 *  This program is distributed to comments in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package recipemix.controllers;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import recipemix.beans.GroupEJB;
import recipemix.beans.RecipeEJB;
import recipemix.models.Groups;
import recipemix.models.GroupsMembers;
import recipemix.models.Users;
import recipemix.models.Recipe;

/**
 * Reference:
 * http://www.simtay.com/part-4-lazy-loading-and-pagination-with-jsf-2-1-primefaces-3-5-datatable-and-form-validation/
 * Reference: Primefaces Showcase
 *
 * Lazy Data Model used for pagination
 *
 * @author Steven Paz <steve.a.paz@gmail.com>
 */
public class LazyUserDataModel extends LazyDataModel<GroupsMembers> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger("LazyCommentDataModel");
    private List<GroupsMembers> dataSource;
    private Groups group;
    private GroupEJB groupEJB;
    private int pageSize;
    private int rowIndex;
    private int rowCount;

    public LazyUserDataModel(Groups group, GroupEJB groupEJB) {
        this.group = group;
        this.groupEJB = groupEJB;
    }

    /**
     * Returns the comment object at the specified position in dataSource.
     *
     * @return
     */
    @Override
    public GroupsMembers getRowData() {
        if (dataSource == null) {
            return null;
        }
        int index = rowIndex % pageSize;
        if (index > dataSource.size()) {
            return null;
        }
        return dataSource.get(index);
    }

    /**
     * Returns the comment object that has the row key.
     *
     * @param rowKey
     * @return
     */
    @Override
    public GroupsMembers getRowData(String rowKey) {
        if (dataSource == null) {
            return null;
        }
        for (GroupsMembers gm : dataSource) {
            if (gm.getMember().getUserName().equals(rowKey)) {
                return gm;
            }
        }
        return null;
    }

    /**
     * Returns the ID for the given comment
     *
     * @param comment - the Users
     * @return - the Users ID
     */
    @Override
    public String getRowKey(GroupsMembers gm) {
        return gm.getMember().getUserName();
    }

    /**
     * Loads a "page"
     *
     * @param first - index of the first item in the page
     * @param pageSize - the number of elements to display
     * @param sortField - the field to sort by
     * @param sortOrder
     * @param filters
     * @return - the List ofComments to display
     */
    @Override
    public List<GroupsMembers> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
        dataSource = groupEJB.findWithNamedQuery(Groups.FIND_PENDING_MEMBERS, group, first, first + pageSize);
        this.setRowCount(groupEJB.countPendingMembers(group));

        // sort the results 
        if (sortField != null) {
            Collections.sort(dataSource, new LazySorter(sortField, sortOrder));
        }

        //rowCount  
        int dataSize = dataSource.size();

        //paginate  
        if (dataSize > pageSize) {
            try {
                return dataSource.subList(first, first + pageSize);
            } catch (IndexOutOfBoundsException e) {
                return dataSource.subList(first, first + (dataSize % pageSize));
            }
        } else {
            return dataSource;
        }
    }

    @Override
    public boolean isRowAvailable() {
        if (dataSource == null) {
            return false;
        }
        int index = rowIndex % pageSize;
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
     *
     * @return int
     */
    @Override
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Returns current row index
     *
     * @return int
     */
    @Override
    public int getRowIndex() {
        return this.rowIndex;
    }

    /**
     * Sets row index
     *
     * @param rowIndex
     */
    @Override
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    /**
     * Sets row count
     *
     * @param rowCount
     */
    @Override
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * Returns row count
     *
     * @return int
     */
    @Override
    public int getRowCount() {
        return this.rowCount;
    }

    /**
     * Sets wrapped data
     *
     * @param list
     */
    @Override
    public void setWrappedData(Object list) {
        this.dataSource = (List<GroupsMembers>) list;
    }

    /**
     * Returns wrapped data
     *
     * @return
     */
    @Override
    public Object getWrappedData() {
        return dataSource;
    }
}
