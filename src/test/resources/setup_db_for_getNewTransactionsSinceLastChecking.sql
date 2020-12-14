INSERT INTO clients(bank_id, client_id, client_credential) VALUES ('ing', 'client_1', ''::BYTEA);
INSERT INTO accounts(bank_id, client_id, fetching_at, account_id, balance, number, owner, ownership, type, name) VALUES ('ing', 'client_1', '2020-09-06 8:23:34', 'account_1', 5688.10, 'NUMBER', 'OWNER', 'single', 'current', 'LIVRET A');
INSERT INTO budgets(id, name) VALUES(1,'Courant');
INSERT INTO stores(id, name) VALUES(1,'Supermarché');
INSERT INTO categories(id, name, up_category_id) VALUES(1, 'Alimentation', null);
INSERT INTO categories(id, name, up_category_id) VALUES(2, 'Supermarché', 1);
INSERT INTO transactions(id, bank_id, client_id, account_id, fetching_at, fetching_position, transaction_id, accounting_date, effective_date, amount, description, type) VALUES (1, 'ing', 'client_1', 'account_1', '2020-09-06 8:23:34', 0, 'transaction_1', '2020-09-06', '2020-09-06', 123.45, 'VIREMENT SEPA', 'transfer');
INSERT INTO transactions(id, bank_id, client_id, account_id, fetching_at, fetching_position, transaction_id, accounting_date, effective_date, amount, description, type, ext_mapping_id, ext_date, ext_period, ext_budget_id, ext_categories_ids, ext_store_id) VALUES (1, 'ing', 'client_1', 'account_1', '2020-09-06 6:23:34', 1, 'transaction_2', '2020-09-06', '2020-09-06', 98.12, 'PAIEMENT PAR CARTE', 'card_debit', 1, '2020-09-04', 'month', 1, ARRAY[1,2], 1);
