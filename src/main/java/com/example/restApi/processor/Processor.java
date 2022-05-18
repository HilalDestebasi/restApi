package com.example.restApi.processor;

import com.example.restApi.model.Contributor;
import com.example.restApi.model.Repository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class Processor {
    // Assigns the entered repository url to the array by sorting the desired number
    public Repository[] JSONtoArrayRepository(String url, int mostForkedRepositoriesLength) throws IOException, ParseException {

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
        Repository[] mostForkedRepositories = new Repository[mostForkedRepositoriesLength + 1];

        // Existing repository size
        int forkedRepositoriesLength = 0;

        // Pulls existing repositories
        for (Object object : jsonArray) {

            JSONObject repo = (JSONObject) object;

            // Create new repository and set these values
            Repository Repository = new Repository();
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
    public Contributor[] JSONtoArrayContributor(String url, int numberOfTopContributors, String repositoryName) throws IOException, ParseException, InterruptedException {

        // Convert url to JSONArray
        JSONArray jsonArray = urlToJSONArray(url);

        // Creates one more array than the entered number size
        Contributor[] contributors = new Contributor[numberOfTopContributors + 1];

        // Existing contributor size
        int topContributorsLength = 0;

        // Pulls existing contributors
        for (Object object : jsonArray) {

            JSONObject cont = (JSONObject) object;

            // Create new contributor and set these values
            Contributor Contributor = new Contributor();
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
    public void quickSortRepository(Repository arr[], int begin, int end) {
        if (begin < end) {
            int partitionIndex = partitionRepository(arr, begin, end);
            quickSortRepository(arr, begin, partitionIndex - 1);
            quickSortRepository(arr, partitionIndex + 1, end);
        }
    }

    private int partitionRepository(Repository arr[], int begin, int end) {
        int pivot = arr[end].getForkQuantity();
        int i = (begin - 1);
        for (int j = begin; j < end; j++) {
            if (arr[j].getForkQuantity() >= pivot) {
                i++;
                Repository swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        Repository swapTemp = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = swapTemp;
        return i + 1;
    }

    public void quickSortContributor(Contributor arr[], int begin, int end) {
        if (begin < end) {
            int partitionIndex = partitionContributor(arr, begin, end);
            quickSortContributor(arr, begin, partitionIndex - 1);
            quickSortContributor(arr, partitionIndex + 1, end);
        }
    }

    private int partitionContributor(Contributor arr[], int begin, int end) {
        int pivot = arr[end].getContributionQuantity();
        int i = (begin - 1);
        for (int j = begin; j < end; j++) {
            if (arr[j].getContributionQuantity() >= pivot) {
                i++;
                Contributor swapTemp = arr[i];
                arr[i] = arr[j];
                arr[j] = swapTemp;
            }
        }

        Contributor swapTemp = arr[i + 1];
        arr[i + 1] = arr[end];
        arr[end] = swapTemp;
        return i + 1;
    }
}
