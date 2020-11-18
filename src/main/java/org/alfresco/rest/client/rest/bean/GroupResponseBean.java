package org.alfresco.rest.client.rest.bean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class GroupResponseBean
{
    private GroupEntry EntryObject;

    public GroupEntry getEntry() {
        return EntryObject;
    }

    public void setEntry(GroupEntry entryObject) {
        EntryObject = entryObject;
    }
}
