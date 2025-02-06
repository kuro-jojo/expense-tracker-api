# Expense Tracker

Backend API for the Expense Tracker application written in Java Spring Boot.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Introduction

The Expense Tracker application allows users to manage their expenses, incomes, and subscriptions. It helps users keep track of their expenses through budgeting, alerting, and visualizations.

## Features

- User authentication and authorization
- Expense and income management
- Subscription tracking
- Email confirmation for user registration
- Budgeting and alerts (TODO)
- Cool graphs and visualizations (TODO)

## Installation

To install and run the project locally, follow these steps:

1. Clone the repository:
    ```sh
    git clone https://github.com/yourusername/expense-tracker.git
    cd expense-tracker
    ```

2. Install dependencies:
    ```sh
    ./mvnw install
    ```

3. Build the project:
    ```sh
    ./mvnw package
    ```

## Configuration

1. Create a `.env` file in the root directory and add the necessary environment variables:
    ```env
    SPRING_GMAIL_USERNAME=
    SPRING_GMAIL_PASSWORD=
    SPRING_MYSQL_HOST=localhost
    SPRING_MYSQL_DB_NAME=expense_tracker
    SPRING_MYSQL_USERNAME=user
    SPRING_MYSQL_PASSWORD=password
    SPRING_JWT_SECRET_KEY=
    ```

2. Update the [application.yaml](src/main/resources/application.yaml) file with your configuration settings:
    ```yaml
    server:
        port: 8080

    bert-cat-api:
        url: 
    ```

The bert API is used to classify the user's expenses into categories. You can find more information about the API here (TODO).
## Usage

To run the application, use the following command:
```sh
./mvnw spring-boot:run
```
The application will be available at http://localhost:8080.

## Testing
To run the tests, use the following command:

```sh
./mvnw test
```

## Contributing
Contributions are welcome! Please fork the repository and create a pull request with your changes.

## License
This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.