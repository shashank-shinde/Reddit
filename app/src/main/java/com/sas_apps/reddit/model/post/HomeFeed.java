package com.sas_apps.reddit.model.post;
/*
 * Created by Shashank Shinde.
 */

import com.sas_apps.reddit.model.post.entry.Entry;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.List;


@Root(name = "feed", strict = false)
public class HomeFeed implements Serializable{

    @Element(name = "icon", required = false)
    private String icon;

    @Element(name = "id", required = false)
    private String id;

    @Element(name = "logo", required = false)
    private String logo;

    @Element(name = "title", required = false)
    private String title;

    @Element(name = "updated", required = false)
    private String updated;

    @Element(name = "subtitle", required = false)
    private String subtitle;

    @ElementList(inline = true, required = false)
    private List<Entry> entrys;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
//
//    public String getLogo() {
//        return logo;
//    }
//
//    public void setLogo(String logo) {
//        this.logo = logo;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<Entry> getEntrys() {
        return entrys;
    }

    public void setEntrys(List<Entry> entrys) {
        this.entrys = entrys;
    }

    @Override
    public String toString() {
        return "Feed: \n [Entrys: \n" + entrys +"]";
    }
}