# restApi
Spring boot application sending a request to github


The application take three parameters:
- name of the organization, e.g., apache
- number of most forked repositories
- number of top contributors


When executed with parameters above, the application create two CSV files with columns:
- <organization_name>_repos.csv: repo,forks,url,description
- <organization_name>_users.csv: repo,username,contributions,followers



run from terminal command example

java -jar restApi-0.0.1-SNAPSHOT.jar camptocamp 3 2  
