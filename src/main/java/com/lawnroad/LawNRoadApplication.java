package com.lawnroad;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import java.util.List;

@SpringBootApplication
public class LawNRoadApplication {
    public static void main(String[] args) {
        SpringApplication.run(LawNRoadApplication.class, args);
    }
}

@Document(collection = "test_collection")
class TestData {
    @Id
    private String id;
    private String message;

    public TestData() {}
    public TestData(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
}

interface TestDataRepository extends MongoRepository<TestData, String> {}

@Component
class MongoTestRunner implements CommandLineRunner {

    private final TestDataRepository repo;

    public MongoTestRunner(TestDataRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        repo.save(new TestData("Hello MongoDB2"));

        List<TestData> dataList = repo.findAll();
        dataList.forEach(d -> System.out.println(d.getMessage()));
    }
}
