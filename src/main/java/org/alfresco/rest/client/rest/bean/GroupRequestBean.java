package org.alfresco.rest.client.rest.bean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;

public class GroupRequestBean
{
    private String id;
    private String displayName;
    private List<String> parentIds;

    public List<String> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<String> parentIds) {
        this.parentIds = parentIds;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}