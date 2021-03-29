# Simple Registration Example
A JavaFX Application demonstrating how to implement a simplistic approach of a registration use case using the following technologies:
* [Java 15 or 16](https://www.oracle.com/java/technologies/javase-downloads.html)
* [JavaFX](https://openjfx.io/openjfx-docs/) (as GUI)
* [Maven](https://maven.apache.org/) / [Gradle](https://gradle.org/) (as build tools)
* [Nitrite Java](https://www.dizitart.org/nitrite-database.html) (as Database)

## Prerequisites
To be able to install and run this project, please make sure you have installed Java 11 or higher. Otherwise, the setup will note work!
To check your java version, please run `java -version` in the command line.

To install a newer version of Java, you can go to [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html) or [OpenJDK](https://jdk.java.net/).

It would be good if you also installed Maven and / or Gradle to your system. To check if you have Maven or Gradle installed run `mvn -version` or `gradle -version`.

If you need to install any of them, please refer to this [Maven tutorial](https://www.baeldung.com/install-maven-on-windows-linux-mac) and the [official Gradle docs](https://docs.gradle.org/current/userguide/installation.html).

Make sure you install JavaFX SDK on your machine, using the instructions provided in the [Official Documentation](https://openjfx.io/openjfx-docs/#install-javafx). Make sure to export the `PATH_TO_FX` environment variable, or to replace it in every command you will find in this documentation from now on, with the `path/to/javafx-sdk-15.0.1/lib`.

_Note: you can download version 15 of the javafx-sdk, by replacing in the download link for version 16 the `16` with `15`._

## Setup & Run
To set up and run the project locally on your machine, please follow the next steps.

### Clone the repository
Clone the repository using:
```git
git clone https://github.com/fis2021/SimpleRegistrationExample
```

### Verify that the project Builds locally
Open a command line session and `cd SimpleRegistrationExample`.
If you have installed all the prerequisites, you should be able to run any of the following commands:
```
mvn clean install
gradle clean build
```
If you prefer to run using the wrappers, you could also build the project using 
```
./mvnw clean install (for Linux or MacOS)
or 
mvnw.cmd clean install (for Windows)

./gradlew clean build (for Linux or MacOS)
or 
gradlew.bat clean build (for Windows)
```

### Open in IntelliJ IDEA
To open the project in IntelliJ idea, you have to import it as either a Maven, or a Gradle project, depending on what you prefer. 
After you import it, in order to be able to run it, you need to set up your IDE according to the [official documentation](https://openjfx.io/openjfx-docs/). Please read the section for `Non-Modular Projects from IDE`.
If you managed to follow all the steps from the tutorial, you should also be able to start the application by pressing the run key to the left of the Main class.

### Run the project with Maven / Gradle
The project has already been setup for Maven and Gradle according to the above link.
To start and run the project use one of the following commands:
* `mvn javafx:run` or `./mvnw javafx:run` (run the `run` goal of the `javafx` maven plugin)
* `gradle run` or `./gradlew run` (to start the `run` task of the `org.openjfx.javafxplugin` plugin)

To understand better how to set up a project using JavaFX 11+ and [Maven](https://openjfx.io/openjfx-docs/#maven) / [Gradle](https://openjfx.io/openjfx-docs/#gradle), please check the [official OpenJFX documentation](https://openjfx.io/).

You should see an application starting, that looks like this:

<img src="docs/Running.png" width="250"/>

Try and register a user, for example admin, with the admin password. It will tell you, the user was registered successfully.

<img src="docs/Registered.png" width="250"/>

However, if you try to register a user with the same username again, you will see an error message:

<img src="docs/AccountExists.png" width="250"/>

**Make sure to close the SimpleRegistrationExample before trying to access the database, because the database only accepts one connection at a time!**

### What happened???

Behind the scenes, the app actually saved the **admin** user in the database, encrypting the password. To see that it actually worked, we need to inspect the database that was created in the 
`$HOME/.registration-example/registration-example.db` (for Linux and MacOS) and `%USERPROFILE%/.registration-example/registration-example.db` file, using the [nitrite-explorer-3.4.3.jar](https://github.com/nitrite/nitrite-java/releases/download/v3.4.3/nitrite-explorer-3.4.3.jar), provided by Nitrite Java.
Download the jar and run `java --module-path $PATH_TO_FX --add-modules javafx.controls nitrite-explorer-3.4.3.jar
`. You should see a window like this open:

<img src="docs/NitriteExplorer.png" width="250"/>

Choose the `$HOME/.registration-example/registration-example.db` and add `test` as both the username, and the password to access the database, then click `Open`.

You should be able to see that there is a single database entry for the `Users` Collection, namely the one you just added. Also, please notice that the password is saved encrypted!

<img src="docs/NitriteExplorerAdmin.png" width="400"/>

### Technical Details

#### Encrypting Passwords
Encrypting the passwords is done via the following 2 Java functions, found in [UserService.java](https://github.com/fis2021/SimpleRegistrationExample/blob/main/src/main/java/org/loose/fis/sle/services/UserService.java):
```java
    private static String encodePassword(String salt, String password) {
        MessageDigest md = getMessageDigest();
        md.update(salt.getBytes(StandardCharsets.UTF_8));

        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

        // This is the way a password should be encoded when checking the credentials
        return new String(hashedPassword, StandardCharsets.UTF_8)
                .replace("\"", ""); //to be able to save in JSON format
    }

    private static MessageDigest getMessageDigest() {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-512 does not exist!");
        }
        return md;
    }
```

#### Nitrite Java
Nitrite Java was used in the [UserService.java](https://github.com/fis2021/SimpleRegistrationExample/blob/main/src/main/java/org/loose/fis/sle/services/UserService.java) file, where we initialized a database, and a _Repository_ of User objects:
```java
    private static ObjectRepository<User> userRepository;

    public static void initDatabase() {
        Nitrite database = Nitrite.builder()
                .filePath(getPathToFile("registration-example.db").toFile())
                .openOrCreate("test", "test");

        userRepository = database.getRepository(User.class);
    }
```

This Repository was further used to add new users, by using the **insert** method:
```java
    public static void addUser(String username, String password, String role) throws UsernameAlreadyExistsException {
        checkUserDoesNotAlreadyExist(username);
        userRepository.insert(new User(username, encodePassword(username, password), role));
    }
```
and to find all users, by using the find method:
```java
    private static void checkUserDoesNotAlreadyExist(String username) throws UsernameAlreadyExistsException {
        for (User user : userRepository.find()) {
            if (Objects.equals(username, user.getUsername()))
                throw new UsernameAlreadyExistsException(username);
        }
    }
```

## Resources
To understand and learn more about **JavaFX**, you can take a look at some of the following links:
* [Introduction to FXML](https://openjfx.io/javadoc/16/javafx.fxml/javafx/fxml/doc-files/introduction_to_fxml.html)
* [Getting Started with JavaFX](https://openjfx.io/openjfx-docs/)
* [JavaFX Tutorial](https://code.makery.ch/library/javafx-tutorial/)
* [JavaFX Java GUI Design Tutorials](https://www.youtube.com/playlist?list=PL6gx4Cwl9DGBzfXLWLSYVy8EbTdpGbUIG)

To better understand how to use **Nitrite Java**, use the following links:
* [Nitrite Java Github Repository](https://github.com/nitrite/nitrite-java) 
* [Nitrite Java Project Page](https://www.dizitart.org/nitrite-database.html)
* [Nitrite Java Documentation Page](https://www.dizitart.org/nitrite-database/)
* [Nitrite Java: Filters](https://www.dizitart.org/nitrite-database/#filter)
* [Nitrite: How to Create an Embedded Database for Java and Android](https://dzone.com/articles/nitrite-how-to-create-an-embedded-database-for-jav)
* [Nitrite: An Embedded NoSQL Database for Java and Android](https://medium.com/@anidotnet/nitrite-an-embedded-nosql-database-for-java-and-android-318bf48c7758)

