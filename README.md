# wallet-service
API for managing digital wallets, enabling **deposits**, **withdrawals**, and **transfers** between users. It supports wallet creation, **balance retrieval** (current and historical), and ensures **full traceability for auditing**. Designed as a **mission-critical service** with high reliability.

---

### Technologies

The project was built using the following technologies:

* **Language:** Java 21
* **Database:** PostgreSQL
* **Cache:** Redis
* **Framework:** Spring Boot 3
* **ORM:** Spring Data JPA with Hibernate
* **Project Manager:** Gradle Kotlin
* **Containerization:** Docker

---

### Project Architecture

The project architecture follows the principles of **Clean Architecture**, aiming for the separation of concerns and layer independence. The design is divided into logical layers:

* **Domain Layer:** Contains the **business rules** and system entities. It is the innermost layer and has no dependencies on external layers.
* **Application Layer:** Responsible for orchestrating the **data flow** and implementing application-specific business rules.
* **Adapters Layer:** Holds rules for **directing use cases**.
* **Infrastructure Layer:** Deals with external implementation details, such as database access, API communication, and frameworks.
* **Shared Layer:** Contains common components and utilities used across different layers, without dependencies on other layers.

---

### How to Run

You can run the project in two ways: using **Docker** or through an **IDE**.

#### Running with Docker

1.  **Prerequisites:** Make sure you have Docker installed and running on your machine.

2.  **Build the Docker image:** Navigate to the project root folder and execute the following command in the terminal to create the application image.

    ```bash
    docker build -t wallet-service:latest .
    ```
3.  **Run Docker Compose:** After the image is built, execute the command below to start the database, cache, and network components.

    ```bash
    docker compose -f docker/docker-compose.yml up -d
    ```

3.  **Run the container:** Once the infrastructure components are available, execute the command below to start the application container. The API server will be available on port `8080`.

    ```bash
    docker run -d -p 8080:8080 --name wallet-service-app --network docker_wallet_net --env-file ./app.env wallet-service:latest
    ```

#### Running with IDE

1.  **Prerequisites:**

    * Java 21 JDK installed.
    * An IDE with support for Java projects, such as **IntelliJ IDEA**, **Eclipse**, or **VS Code**.


2.  **Run Docker Compose:** Navigate to the project root directory and execute the command below in the terminal to start the database, cache, and network components.

    ```bash
    docker compose -f docker/docker-compose.yml up -d
    ```
    
3. **Execution via Console:**
    Navigate to the project root directory and execute the following command in the terminal. The API will be available at `http://localhost:8080`.

    ```bash
    ./gradlew bootRun
    ```

4.  **Run automated tests via Console:**
    Navigate to the project root directory and execute the following command in the terminal.

    ```bash
    ./gradlew clean build
    ```
    OR
    ```bash
    ./gradlew test
    ```

5.  **Execution in the IDE:**
    Open your IDE and **import the project as a Gradle project**. The IDE will detect the `build.gradle.kts` file and automatically configure the project and its dependencies. Then, locate the main application class (`com.vicastro.walletservice.WalletServiceApplication.main`) and run it.

---

### Endpoints and Examples

The API offers the following endpoints for digital wallet manipulation (curls assume a local environment).

#### **Create a wallet**

```bash
curl --location 'http://localhost:8080/wallet' \
--header 'Content-Type: application/json' \
--data '{
    "user_id": "32d440de-c5fb-4203-a96c-4815ea903c1d"
}'
````

Note: Remember to replace the `user_id` with the ID of the user you want to associate with the wallet.

#### **Add funds (deposit)**

```bash
curl --location 'http://localhost:8080/wallet/deposit' \
--header 'Content-Type: application/json' \
--data '{
    "wallet_id": "5007d1af-e433-41db-8435-af4d0d2c01cd",
    "amount": 50
}'
```

Notes:

1.  Remember to replace the `wallet_id` with the ID of the wallet you want to deposit funds into.
2.  The `amount` value must be positive and is treated in **cents**.

#### **Withdraw funds (withdrawal)**

```bash
curl --location 'http://localhost:8080/wallet/withdraw' \
--header 'Content-Type: application/json' \
--data '{
    "wallet_id": "5007d1af-e433-41db-8435-af4d0d2c01cd",
    "amount": 10
}'
```

Notes:

1.  Remember to replace the `wallet_id` with the ID of the wallet you want to withdraw funds from.
2.  The `amount` value must be positive and is treated in **cents**.

#### **Transfer funds between wallets**

```bash
curl --location 'http://localhost:8080/wallet/transfer' \
--header 'Content-Type: application/json' \
--data '{
    "from_wallet_id": "af963b1d-7a4f-4766-8ffb-86bdc8a0e161",
    "to_wallet_id": "5007d1af-e433-41db-8435-af4d0d2c01cd",
    "amount": 70
}'
```

Notes:

1.  Remember to replace the `from_wallet_id` and `to_wallet_id` with the IDs of the wallets you want to transfer funds between.
2.  `from_wallet_id`: ID of the source wallet (where funds will be withdrawn from).
3.  `to_wallet_id`: ID of the destination wallet (where funds will be sent to).
4.  The `amount` value must be positive and is treated in **cents**.

#### **Check wallet balance**

```bash
curl --location 'http://localhost:8080/wallet/af963b1d-7a4f-4766-8ffb-86bdc8a0e161/balance'
```

Note: Remember to replace the `wallet_id` with the ID of the wallet you want to check the balance for.

#### **Check historical wallet balance**

```bash
curl --location 'http://localhost:8080/wallet/af963b1d-7a4f-4766-8ffb-86bdc8a0e161/balance/2025-10-01'
```

Notes:

1.  Remember to replace the `wallet_id` with the ID of the wallet you want to check the historical balance for.
2.  The date must be in the format `YYYY-MM-DD`.

<!-- end list -->


---

### Scheduler

The application includes a scheduler that runs every day at midnight (00:00) to perform the following tasks:
* **Persist Wallet Balances:** It saves the current balance of each wallet to a historical record in the database. This allows for tracking balance changes over time and provides a historical view of wallet balances.
* **Cache Cleanup:** It clears the Redis cache to ensure that outdated balance information is removed.