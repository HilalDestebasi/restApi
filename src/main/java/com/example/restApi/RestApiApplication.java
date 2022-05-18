package com.example.restApi;


import com.example.restApi.model.Contributor;
import com.example.restApi.model.Repository;
import com.example.restApi.processor.Processor;

import org.json.simple.parser.ParseException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;

@SpringBootApplication
public class RestApiApplication extends Processor implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RestApiApplication.class, args);
    }

    @Override
    public void run(String[] args) throws IOException, ParseException, InterruptedException {

        int mostForkedRepositoriesLength;           // argument 2 number of most forked repositories
        int numberOfTopContributors;                // argument 3 number of top contributors
        String organizationName;                    // argument 1 name of the organization, e.g., apache

        if (args.length == 3) {
            try {                                                       // assign arguments if it is true
                organizationName = args[0];
                mostForkedRepositoriesLength = Integer.valueOf(args[1]);
                if(mostForkedRepositoriesLength<1){
                    mostForkedRepositoriesLength=1;
                }
                numberOfTopContributors = Integer.valueOf(args[2]);
                if(numberOfTopContributors<1){
                    numberOfTopContributors=1;
                }
            } catch (Exception e) {                                     // assign default arguments if it is false
                organizationName = "dokuzeyluluniversity";
                mostForkedRepositoriesLength = Integer.valueOf("5");
                numberOfTopContributors = Integer.valueOf("3");
                System.out.println("You entered incorrect values. Default values assigned");
            }
        } else {                                                        // assign default arguments if it is false
            organizationName = "dokuzeyluluniversity";
            mostForkedRepositoriesLength = Integer.valueOf("5");
            numberOfTopContributors = Integer.valueOf("3");
            System.out.println("You entered incorrect values. Default values assigned");
        }

        // create url with entered organization name
        String urlRepository = "https://api.github.com/orgs/" + organizationName + "/repos?type=forks";

        try{
            // create repositories with this.organization
            Repository[] repositories = JSONtoArrayRepository(urlRepository, mostForkedRepositoriesLength);

            // create csv files
            FileWriter repositoryFile = new FileWriter(organizationName + "_repos.csv");
            FileWriter contributorFile = new FileWriter(organizationName + "_users.csv");

            // write first row to csv files
            repositoryFile.write("Name;Fork Quantity;Public URL;Description\n");
            contributorFile.write("Username;Repository Name;Contribution Quantity;Follower Count\n");

            // write to file most forked repositories
            for (int i = 0; i < mostForkedRepositoriesLength; i++) {
                // if there are fewer repositories than the input value
                if (repositories[i] != null) {
                    repositoryFile.write(repositories[i].getName() + ";" + repositories[i].getForkQuantity() + ";" + repositories[i].getPublicURL() + ";" + repositories[i].getDescription() + "\n");
                    // create contributors with most forked repositories
                    Contributor[] contributors = JSONtoArrayContributor(repositories[i].getContributorURL(), numberOfTopContributors, repositories[i].getName());
                    // writes top contributors to repositories to file
                    for (int j = 0; j < numberOfTopContributors; j++) {
                        // if there are fewer contributors than the input value
                        if (contributors[j] != null) {
                            contributorFile.write(contributors[j].getUsername() + ";" + contributors[j].getRepositoryName() + ";" + contributors[j].getContributionQuantity() + ";" + calculateFollowersCount(contributors[j].getFollowerURL()) + "\n");
                        }
                    }
                }
            }

            repositoryFile.close();
            contributorFile.close();
            System.out.println("Successfully");
        }catch (Exception e){
            System.out.println("Maximum limit exceed for git rest api, please try again 1 hours later");
            // Maximum request limit for git rest api is 60 per hour. This limit can be increased by authentication.
            // This limit not increased for this application to do not request git username and pw from user.

        }

    }


}
