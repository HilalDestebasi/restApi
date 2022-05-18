package com.example.restApi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.Table;


@Table
@Data
@Getter
@Setter

@JsonIgnoreProperties       // Used at class level to mark a property or list of properties to be ignored.
public class Repository {

    private String name;
    private int forkQuantity;
    private String publicURL;
    private String contributorURL;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getForkQuantity() {
        return forkQuantity;
    }

    public void setForkQuantity(int forkQuantity) {
        this.forkQuantity = forkQuantity;
    }

    public String getPublicURL() {
        return publicURL;
    }

    public void setPublicURL(String publicURL) {
        this.publicURL = publicURL;
    }

    public String getContributorURL() {
        return contributorURL;
    }

    public void setContributorURL(String contributorURL) {
        this.contributorURL = contributorURL;
    }
}
