CREATE TABLE owner (
  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name VARCHAR(128),
  surname VARCHAR(128),
  born DATE,
  phoneNumber VARCHAR(20),
  addressStreet VARCHAR(50),
  addressTown VARCHAR(50)
);

CREATE TABLE property(
  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  street VARCHAR(50),
  town VARCHAR(50),
  price INT,
  typeOf VARCHAR(50),
  square int,
  dateOfBuild DATE,
  description VARCHAR(128)
);

CREATE TABLE titledeed (
  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  ownerId BIGINT REFERENCES owner(id),
  propertyId BIGINT REFERENCES property(id),
  startDate DATE,
  endDate DATE
);