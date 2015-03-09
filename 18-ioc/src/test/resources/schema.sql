CREATE TABLE account (
  id SERIAL PRIMARY KEY,
  full_name VARCHAR (80) NOT NULL,
  balance DOUBLE PRECISION NOT NULL,
  creation_time TIMESTAMP NOT NULL
);

CREATE TABLE transaction (
  id BIGSERIAL PRIMARY KEY,
  account_from INTEGER NOT NULL,
  account_to INTEGER NOT NULL,
  amount DOUBLE PRECISION NOT NULL,
  creation_time TIMESTAMP NOT NULL,

  CONSTRAINT account_from_fk FOREIGN KEY (account_from) REFERENCES account(id),
  CONSTRAINT account_to_fk   FOREIGN KEY (account_to)   REFERENCES account(id)
);
