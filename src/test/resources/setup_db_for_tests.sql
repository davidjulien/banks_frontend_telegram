TRUNCATE banks, clients, transactions, accounts;

INSERT INTO banks(id, name) VALUES('ing', 'ING');
INSERT INTO clients(bank_id, client_id, client_credential) VALUES ('ing', 'client_1', ''::BYTEA);
INSERT INTO accounts(bank_id, client_id, fetching_at, account_id, balance, number, owner, ownership, type, name) VALUES ('ing', 'client_1', '2020-09-06 8:23:34', 'account_1', 5688.10, 'NUMBER', 'OWNER', 'single', 'current', 'LIVRET A');
INSERT INTO transactions(id, bank_id, client_id, account_id, fetching_at, transaction_id, accounting_date, effective_date, amount, description, type) VALUES (1, 'ing', 'client_1', 'account_1', '2020-09-06 8:23:34', 'transaction_1', '2020-09-06', '2020-09-06', 123.45, 'VIREMENT SEPA', 'transfer');
INSERT INTO transactions(id, bank_id, client_id, account_id, fetching_at, transaction_id, accounting_date, effective_date, amount, description, type) VALUES (1, 'ing', 'client_1', 'account_1', '2020-09-06 6:23:34', 'transaction_2', '2020-09-06', '2020-09-06', 98.12, 'PAIEMENT PAR CARTE', 'card_debit');
