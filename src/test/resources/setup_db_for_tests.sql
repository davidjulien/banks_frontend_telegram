DROP TABLE IF EXISTS banks,clients,accounts,transactions,apps;
DROP TYPE IF EXISTS e_account_ownership,e_account_type,e_transaction_type;

CREATE TABLE banks(id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL);
INSERT INTO banks(id, name) VALUES('ing','ING');
CREATE TABLE clients(id SERIAL, bank_id TEXT NOT NULL REFERENCES banks(id), client_id TEXT NOT NULL, client_credential BYTEA NOT NULL, PRIMARY KEY (bank_id, client_id));
CREATE TYPE e_account_ownership AS ENUM ('single', 'joint');
CREATE TYPE e_account_type AS ENUM ('current', 'savings', 'home_loan');
CREATE TABLE accounts(id SERIAL, bank_id TEXT NOT NULL, client_id TEXT NOT NULL, fetching_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, account_id TEXT NOT NULL, balance FLOAT NOT NULL, number TEXT NOT NULL, owner TEXT, ownership e_account_ownership NOT NULL, type e_account_type NOT NULL, name TEXT NOT NULL, FOREIGN KEY (bank_id, client_id) REFERENCES clients(bank_id, client_id));
CREATE TYPE e_transaction_type AS ENUM ('card_debit', 'card_withdrawal', 'check', 'sepa_debit','transfer','interests');
CREATE TABLE transactions(id BIGSERIAL, bank_id TEXT NOT NULL, client_id TEXT NOT NULL, account_id TEXT NOT NULL, fetching_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, fetching_position INTEGER NOT NULL, transaction_id TEXT NOT NULL, accounting_date DATE NOT NULL, effective_date DATE NOT NULL, amount FLOAT NOT NULL, description TEXT NOT NULL, type e_transaction_type NOT NULL, FOREIGN KEY (bank_id, client_id) REFERENCES clients(bank_id, client_id));
CREATE UNIQUE INDEX ON transactions(bank_id,client_id,transaction_id);

CREATE TABLE apps(name TEXT UNIQUE NOT NULL, version TEXT NOT NULL);
INSERT INTO apps(name, version) VALUES('banks_fetch', '0.2.1');


INSERT INTO clients(bank_id, client_id, client_credential) VALUES ('ing', 'client_1', ''::BYTEA);
INSERT INTO accounts(bank_id, client_id, fetching_at, account_id, balance, number, owner, ownership, type, name) VALUES ('ing', 'client_1', '2020-09-06 8:23:34', 'account_1', 5688.10, 'NUMBER', 'OWNER', 'single', 'current', 'LIVRET A');
INSERT INTO transactions(id, bank_id, client_id, account_id, fetching_at, fetching_position, transaction_id, accounting_date, effective_date, amount, description, type) VALUES (1, 'ing', 'client_1', 'account_1', '2020-09-06 8:23:34', 0, 'transaction_1', '2020-09-06', '2020-09-06', 123.45, 'VIREMENT SEPA', 'transfer');
INSERT INTO transactions(id, bank_id, client_id, account_id, fetching_at, fetching_position, transaction_id, accounting_date, effective_date, amount, description, type) VALUES (1, 'ing', 'client_1', 'account_1', '2020-09-06 6:23:34', 1, 'transaction_2', '2020-09-06', '2020-09-06', 98.12, 'PAIEMENT PAR CARTE', 'card_debit');
