# NTUA Software Engineering Project 2018-2019
Price observatory webapp where users can add and search for the prices of products (in this case medicine).
Implemented using Java Spring Boot for the backend and Javascript for the frontend.

## Git Gud Team
### Members
- Κώτσης Στάθης
- Μπούας Νικόλαος
- Παπαδόπουλος Λεωνίδας
- Σεϊμένης Αιμίλιος
- Σταθόπουλος Παύλος
- Φιλίνης Νικόλαος

## Overview
The app has been tested on Arch Linux with the following software versions:
- Java 11
- Gradle 7.4.2
- Spring boot 2.6.4
- Mariadb 10.7.3

To install the necessary dependencies in Arch linux run:

`sudo pacman -S gradle mariadb jdk11-openjdk`

## Setup
The webapp uses an SQL database called `medicine`. Assuming we have a fresh install of mysql/mariadb,
we can login as root and create the database in the mysql terminal along with a new user:
```bash
sudo mysql -u root
<Enter>
> create database medicine;
> create user 'elli'@'%' identified by 'Orpheus2019';
> grant all on medicine.* to 'elli'@'%';
> exit;
```

To populate the database we can navigate to `src/main/sql` and run the `load_db_file.sh` script. For example:
```bash
bash load_db_file.sh Database.sql
````
This will populate the database with some mock data.

## Build, test and run instructions
In order to build and test the webapp we can use the gradle wrapper as follows:
```bash
./gradlew build -DIGNORE_SSL_ERRORS=true
```

To run the application we can run the command:
```bash
./gradlew bootRun
```
This launches the application which is hosted locally and uses the port 8765.
The API is available using the endpoint `/observatory/api`.
To access the frontend of our webapp we can simply access the url `https://localhost:8765`.

---

To run additional tests we can clone the following rest api client repo by running:
```bash
git clone https://github.com/saikos/softeng18b-rest-api-client.git
```
Afterwards we can navigate to `src/main/sql` and load the `Database_test.sql` file by running:
```bash
bash load_db_file.sh Database_test.sql
```
Then we launch our application by running:
```bash
./gradlew bootRun
```
in our root project directory.

Finally in a separate terminal we cd to the cloned repo and run:
```bash
./gradlew test --tests gr.ntua.ece.softeng18b.client.ObservatoryAPIFunctionalTest -Dusername=admin -Dpassword=admin123 -Dprotocol=https -DIGNORE_SSL_ERRORS=true -Dhost=localhost -Dport=8765 -Dtest.json=test-data.json
```
