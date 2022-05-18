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
public class Contributor {

    private String repositoryName;
    private String username;
    private int contributionQuantity;
    private String followerURL;

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getContributionQuantity() {
        return contributionQuantity;
    }

    public void setContributionQuantity(int contributionQuantity) {
        this.contributionQuantity = contributionQuantity;
    }

    public String getFollowerURL() {
        return followerURL;
    }

    public void setFollowerURL(String followerURL) {
        this.followerURL = followerURL;
    }
}
