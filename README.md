# Inmobix

Inmobix is a backend application for a real estate platform. It allows users to manage properties for sale or rent.

## Entities

### User

The `User` entity represents a user of the platform.

| Attribute  | Type      | Description                                       | Constraints                |
|------------|-----------|---------------------------------------------------|----------------------------|
| id         | Long      | The unique identifier for the user.               | Primary Key, Auto-generated|
| name       | String    | The full name of the user.                        | Not Blank                  |
| email      | String    | The email address of the user.                    | Not Blank, Valid Email, Unique |
| username   | String    | The username for logging into the platform.       | Not Blank, Unique          |
| password   | String    | The user's password.                              | Not Blank                  |
| phone      | String    | The user's phone number.                          | Optional                   |
| birthDate  | LocalDate | The user's date of birth.                         | Optional                   |

### Property

The `Property` entity represents a real estate property in the system.

| Attribute       | Type          | Description                                         | Constraints                               |
|-----------------|---------------|-----------------------------------------------------|-------------------------------------------|
| id              | Long          | The unique identifier for the property.             | Primary Key, Auto-generated               |
| title           | String        | The title of the property listing.                  | Not Blank                                 |
| description     | String        | A detailed description of the property.             | Not Blank                                 |
| address         | String        | The physical address of the property.               | Not Blank                                 |
| city            | String        | The city where the property is located.             | Not Blank                                 |
| state           | String        | The state or department where the property is located.| Not Blank                                 |
| price           | BigDecimal    | The price of the property.                          | Not Null, Positive                        |
| area            | BigDecimal    | The total area of the property.                     | Positive                                  |
| bedrooms        | Integer       | The number of bedrooms.                             | Not Null, Defaults to 0                   |
| bathrooms       | Integer       | The number of bathrooms.                            | Not Null, Defaults to 0                   |
| garages         | Integer       | The number of garage spaces.                        | Not Null, Defaults to 0                   |
| propertyType    | String        | The type of property (e.g., house, apartment).      | Not Blank                                 |
| transactionType | String        | The type of transaction (e.g., sale, rent).         | Not Blank                                 |
| available       | Boolean       | Indicates if the property is available.             | Not Null, Defaults to true                |
| imageUrl        | String        | URL of an image of the property.                    | Optional                                  |
| createdAt       | LocalDateTime | Timestamp of when the property was created.         | Automatically set on creation             |
| updatedAt       | LocalDateTime | Timestamp of the last update.                       | Automatically set on update               |
| user            | User          | The user who owns or listed the property.           | Many-to-One relationship with User        |

## API Endpoints

This section provides details on the available API endpoints. You can use a tool like Postman to interact with them.

### User Endpoints

Base Path: `/api`

| Method | Path                | Description                                       |
|--------|---------------------|---------------------------------------------------|
| POST   | `/register`         | Registers a new user.                             |
| POST   | `/login`            | Authenticates a user and returns a token.         |
| POST   | `/forgot-password`  | Initiates the password recovery process for a user.|
| GET    | `/user/{id}`        | Retrieves a specific user by their ID.            |
| GET    | `/users`            | Retrieves a list of all users.                    |

### Property Endpoints

Base Path: `/api/properties`

| Method | Path                        | Description                                          |
|--------|-----------------------------|------------------------------------------------------|
| POST   | `/`                         | Creates a new property.                              |
| GET    | `/`                         | Retrieves a list of all properties.                  |
| GET    | `/{id}`                     | Retrieves a specific property by its ID.             |
| PUT    | `/{id}`                     | Updates an existing property.                        |
| DELETE | `/{id}`                     | Deletes a property.                                  |
| GET    | `/available`                | Retrieves a list of all available properties.        |
| GET    | `/city/{city}`              | Finds all properties in a specific city.             |
| GET    | `/type/{propertyType}`      | Finds all properties of a specific type.             |
| GET    | `/transaction/{transactionType}` | Finds all properties for a specific transaction type.|
| GET    | `/price-range`              | Finds properties within a given price range.         |
| GET    | `/user/{userId}`            | Finds all properties belonging to a specific user.   |