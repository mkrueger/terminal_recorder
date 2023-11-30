package org.example;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class CosmosDBBackend {
    private final static String COSMOS_URI = "https://mikkrg-e2e-cdb.documents.azure.com:443/";
    private final static String COSMOS_KEY = "RfSeWofmmUZwBcaXgjE9pwqyO9ibkKwoS7heHgZG232cXNjnWryxW7bHiuSPdvROqQcYla61nk8iACDbkH5UpA==";
    private final static String COSMOS_DATABASE_NAME = "ansi_db";
    private final static String HEADER_CONTAINER_NAME = "ansi_data";
    private final static String EVENTS_CONTAINER_NAME = "evt_data";
    private final static String USER_CONTAINER_NAME = "users";

    private CosmosDatabase database;
    private CosmosContainer headerContainer;
    private CosmosContainer eventsContainer;
    private CosmosContainer usersContainer;

    private CosmosClient client;

    protected static Logger logger = LoggerFactory.getLogger(CosmosDBBackend.class);

    public void connectDatabase() throws Exception {
        logger.info("Using Azure Cosmos DB endpoint: " + CosmosDBBackend.COSMOS_URI);

        // ArrayList<String> preferredRegions = new ArrayList<String>();
        // preferredRegions.add("West US");

        //  Setting the preferred location to Cosmos DB Account region
        //  West US is just an example. User should set preferred location to the Cosmos DB region closest to the application

        //  Create client
        client = new CosmosClientBuilder()
                .endpoint(COSMOS_URI)
                .key(COSMOS_KEY)
               // .preferredRegions(preferredRegions)
                .contentResponseOnWriteEnabled(true)
                .consistencyLevel(ConsistencyLevel.EVENTUAL)
                .buildClient();

        createDatabaseIfNotExists();
        createContainerIfNotExists();
        updateContainerThroughput();
    }

    // Database Create
    private void createDatabaseIfNotExists() throws Exception {
        logger.info("Create database " + COSMOS_DATABASE_NAME + " if not exists...");

        //  Create database if not exists
        CosmosDatabaseResponse databaseResponse = client.createDatabaseIfNotExists(COSMOS_DATABASE_NAME);
        database = client.getDatabase(databaseResponse.getProperties().getId());

        logger.info("Done.");
    }

    // Container create
    private void createContainerIfNotExists() throws Exception {
        logger.info("Create container " + HEADER_CONTAINER_NAME + " if not exists.");

        //  Create container if not exists
        CosmosContainerProperties containerProperties =
                new CosmosContainerProperties(HEADER_CONTAINER_NAME, "/id");

        // Provision throughput
        ThroughputProperties throughputProperties = ThroughputProperties.createAutoscaledThroughput(10000);

        //  Create container with 400 RU/s
        CosmosContainerResponse databaseResponse = database.createContainerIfNotExists(containerProperties, throughputProperties);
        headerContainer = database.getContainer(databaseResponse.getProperties().getId());

        //  Create container if not exists
        containerProperties =
                new CosmosContainerProperties(EVENTS_CONTAINER_NAME, "/id");

        //  Create container with 400 RU/s
        databaseResponse = database.createContainerIfNotExists(containerProperties, throughputProperties);
        eventsContainer = database.getContainer(databaseResponse.getProperties().getId());

        //  Create container if not exists
        containerProperties =
                new CosmosContainerProperties(USER_CONTAINER_NAME, "/id");

        //  Create container with 400 RU/s
        databaseResponse = database.createContainerIfNotExists(containerProperties, throughputProperties);
        usersContainer = database.getContainer(databaseResponse.getProperties().getId());

        logger.info("Done.");
    }

    // Update container throughput
    private void updateContainerThroughput() throws Exception {
        logger.info("Update throughput for container " + HEADER_CONTAINER_NAME + ".");

        // Specify new throughput value
        ThroughputProperties throughputProperties = ThroughputProperties.createAutoscaledThroughput(10000);
        headerContainer.replaceThroughput(throughputProperties);
        eventsContainer.replaceThroughput(throughputProperties);

        logger.info("Done.");
    }

    public void close() {
        client.close();
    }

    public void update(File file) {
        logger.info("Update file " + file.header.title);
        headerContainer.createItem(file.header, new PartitionKey(file.header.getId()), new CosmosItemRequestOptions());

        for (var evt : file.events) {
            eventsContainer.createItem(evt, new PartitionKey(evt.getId()), new CosmosItemRequestOptions());
        }
        logger.info("Done.");
    }

    public ArrayList<File> getEntries() {
        logger.info("Query documents in the container " + HEADER_CONTAINER_NAME + ".");
        String sql = "SELECT * FROM c";
        var headers = headerContainer.queryItems(sql, new CosmosQueryRequestOptions(), Header.class);
        var result = new ArrayList<File>();
        for (var header : headers) {
            var file = new File();
            file.setHeader(header);
            sql = "SELECT * FROM e where e.fileId = '" + header.id + "'";
            var events = eventsContainer.queryItems(sql, new CosmosQueryRequestOptions(), Event.class);
            var list = new ArrayList<>();
            for (var e : events) {
                list.add(e);
            }
            file.setEvents(list.toArray(new Event[list.size()]));
            result.add(file);
        }
        return result;
    }

    public void deleteFile(User user, String fileTitle) {
        logger.info("Delete documents in the container " + HEADER_CONTAINER_NAME + ".");
        String sql = "SELECT * FROM c WHERE c.userId ='" + user.id + "' AND c.title='" + fileTitle +"'";
        var headers = headerContainer.queryItems(sql, new CosmosQueryRequestOptions(), Header.class);
        for (var header : headers) {
            sql = "SELECT * FROM e where e.fileId = '" + header.id + "'";
            var events = eventsContainer.queryItems(sql, new CosmosQueryRequestOptions(), Event.class);
            for (var e : events) {
                eventsContainer.deleteItem(e.getId(), new PartitionKey(e.getId()), new CosmosItemRequestOptions());
            }
            headerContainer.deleteItem(header.getId(), new PartitionKey(header.getId()), new CosmosItemRequestOptions());
        }
    }

    public void createUser(String userName) {
        logger.info("Create user " + userName);
        var newUser = new User(userName);
        usersContainer.createItem(newUser, new PartitionKey(newUser.getId()), new CosmosItemRequestOptions());
        logger.info("Done.");
    }

    public User getUser(String userName) {
        logger.info("Query documents in the container " + HEADER_CONTAINER_NAME + ".");
        String sql = "SELECT * FROM c WHERE c.userName = '" + userName + "'";
        var users = usersContainer.queryItems(sql, new CosmosQueryRequestOptions(), User.class);
        var user = users.stream().findFirst();
        return user.get();
    }
}
