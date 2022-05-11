package com.example.restApi;


import com.example.restApi.model.contributor;
import com.example.restApi.model.repository;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;

import java.net.HttpURLConnection;
import java.net.URL;

@SpringBootApplication
public class RestApiApplication implements CommandLineRunner {
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
            repository[] repositories = JSONtoArrayRepository(urlRepository, mostForkedRepositoriesLength);

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
                    contributor[] contributors = JSONtoArrayContributor(repositories[i].getContributorURL(), numberOfTopContributors, repositories[i].getName());
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

    // Assigns the entered repository url to the array by sorting the desired number
    public repository[] JSONtoArrayRepository(String url, int mostForkedRepositoriesLength) throws IOException, ParseException {

        // Convert url to JSONArray
        JSONArray jsonArray = urlToJSONArray(url);

        // Creates one more array than the entered number size
        /*
         *
         *  The reason for creating one extra array:
         *  The desired number of arrays is placed and sorted. Then, when the new element arrives,
         *  the new element is put in the last element of the array. The last element placed is sorted by the
         *  others according to the quick sort.
         *  Why aren't all elements placed in one array?
         *  Continuously shifting all elements in an entire array stretches complexity time
         *
         */
        repository[] mostForkedRepositories = new repository[mostForkedRepositoriesLength + 1];

        // Existing repository size
        int forkedRepositoriesLength = 0;

        // Pulls existing repositories
        for (Object object : jsonArray) {

            JSONObject repo = (JSONObject) object;

            // Create new repository and set these values
            repository Repository = new repository();
            Repository.setName((String) repo.get("name"));
            int forks_count = (int) ((long) repo.get("forks_count"));
            Repository.setForkQuantity(forks_count);
            Repository.setPublicURL((String) repo.get("url"));
            Repository.setDescription((String) repo.get("description"));
            Repository.setContributorURL((String) repo.get("contributors_url"));

            // If the existing repository is less than the entered number, it is placed directly into the array.
            if (forkedRepositoriesLength < mostForkedRepositoriesLength) {
                mostForkedRepositories[forkedRepositoriesLength] = Repository;
                quickSortRepository(mostForkedRepositories, 0, forkedRepositoriesLength);
                forkedRepositoriesLength++;
            } else {    // If more it is placed in one more element
                mostForkedRepositories[forkedRepositoriesLength] = Repository;
                quickSortRepository(mostForkedRepositories, 0, forkedRepositoriesLength);
            }
        }

        return mostForkedRepositories;
    }

    // Assigns the entered contributor url to the array by sorting the desired number
    public contributor[] JSONtoArrayContributor(String url, int numberOfTopContributors, String repositoryName) throws IOException, ParseException, InterruptedException {

        // Convert url to JSONArray
        JSONArray jsonArray = urlToJSONArray(url);

        // Creates one more array than the entered number size
        contributor[] contributors = new contributor[numberOfTopContributors + 1];

        // Existing contributor size
        int topContributorsLength = 0;

        // Pulls existing contributors
        for (Object object : jsonArray) {

            JSONObject cont = (JSONObject) object;

            // Create new contributor and set these values
            contributor Contributor = new contributor();
            Contributor.setContributionQuantity((int) ((long) cont.get("contributions")));
            Contributor.setUsername((String) cont.get("login"));
            Contributor.setRepositoryName(repositoryName);
            Contributor.setFollowerURL((String) cont.get("followers_url"));

            // If the existing contributor is less than the entered number, it is placed directly into the array.
            if (topContributorsLength < numberOfTopContributors) {
                contributors[topContributorsLength] = Contributor;
                quickSortContributor(contributors, 0, topContributorsLength);
                topContributorsLength++;
            } else {        // If more it is placed in one more element
                contributors[numberOfTopContributors] = Contributor;
                quickSortContributor(contributors, 0, numberOfTopContributors);
            }
        }

        return contributors;
    }

    // Calculate followers count with url
    public int calculateFollowersCount(String url) throws IOException, ParseException {
        JSONArray jsonArray = urlToJSONArray(url);
        return jsonArray.size();
    }

    // Assigns a request to the entered url. Converts the result from the request to JSONArray
    public JSONArray urlToJSONArray(String url) throws IOException, ParseException {
        String inputLine;
        StringBuffer response = new StringBuffer();
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        JSONParser parser = new JSONParser();

        while ((inputLine = input.readLine()) != null) {
            response.append(inputLine);
        }

        input.close();
        return (JSONArray) parser.parse(response.toString());
    }

    // Sorts the repository and contributor arrays with quick sort
    public void quickSortRepository(repository arr[], int begin, int end) {
        if (begin < end) {
            int partitionIndex = partitionRepository(arr, begin, end);
            quickSortRepository(arr, begin, partitionIndex - 1);
            quickSortRepository(arr, partitionIndex + 1, end);
        }
    }

    private int partitionRepository(repository arr[], int begin, int end) {
        int pivot = arr[end].getForkQuantity();
        int i = (begin - 1);
        for (int j = begin; j < end; j++) {
            if (arr[j].getForkQuantity() >= pivot) {
                i++;
                repository swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        repository swapTemp = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = swapTemp;
        return i + 1;
    }

    public void quickSortContributor(contributor arr[], int begin, int end) {
        if (begin < end) {
            int partitionIndex = partitionContributor(arr, begin, end);
            quickSortContributor(arr, begin, partitionIndex - 1);
            quickSortContributor(arr, partitionIndex + 1, end);
        }
    }

    private int partitionContributor(contributor arr[], int begin, int end) {
        int pivot = arr[end].getContributionQuantity();
        int i = (begin - 1);
        for (int j = begin; j < end; j++) {
            if (arr[j].getContributionQuantity() >= pivot) {
                i++;
                contributor swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        contributor swapTemp = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = swapTemp;
        return i + 1;
    }
}
