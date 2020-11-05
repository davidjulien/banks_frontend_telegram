CREATE TABLE banks(id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL);
INSERT INTO banks(id, name) VALUES('ing','ING');
CREATE TABLE clients(id SERIAL, bank_id TEXT NOT NULL REFERENCES banks(id), client_id TEXT NOT NULL, client_credential BYTEA NOT NULL, PRIMARY KEY (bank_id, client_id));
CREATE TYPE e_account_ownership AS ENUM ('single', 'joint');
CREATE TYPE e_account_type AS ENUM ('current', 'savings', 'home_loan');
CREATE TABLE accounts(id SERIAL, bank_id TEXT NOT NULL, client_id TEXT NOT NULL, fetching_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, account_id TEXT NOT NULL, balance FLOAT NOT NULL, number TEXT NOT NULL, owner TEXT, ownership e_account_ownership NOT NULL, type e_account_type NOT NULL, name TEXT NOT NULL, FOREIGN KEY (bank_id, client_id) REFERENCES clients(bank_id, client_id));
CREATE TYPE e_transaction_type AS ENUM ('other','card_debit', 'card_withdrawal', 'check', 'sepa_debit','transfer','interests','bank_fees');
CREATE TYPE e_period AS ENUM('month','bimester','quarter','semester','annual');
CREATE TABLE transactions(id BIGSERIAL, bank_id TEXT NOT NULL, client_id TEXT NOT NULL, account_id TEXT NOT NULL, fetching_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, fetching_position INTEGER NOT NULL, transaction_id TEXT NOT NULL, accounting_date DATE NOT NULL, effective_date DATE NOT NULL, amount FLOAT NOT NULL, description TEXT NOT NULL, type e_transaction_type NOT NULL, ext_mapping_id INTEGER, ext_date DATE, ext_period e_period, ext_budget_id INTEGER, ext_categories_id INTEGER[], ext_store_id INTEGER, FOREIGN KEY (bank_id, client_id) REFERENCES clients(bank_id, client_id));
CREATE UNIQUE INDEX transactions_bank_client_transaction_ids ON transactions(bank_id,client_id,transaction_id);

CREATE TABLE apps(name TEXT UNIQUE NOT NULL, version TEXT NOT NULL);
INSERT INTO apps(name, version) VALUES('banks_fetch', '0.2.1');

CREATE TABLE budgets(id SERIAL PRIMARY KEY, name TEXT NOT NULL);
CREATE TABLE categories(id SERIAL PRIMARY KEY, up_category_id INTEGER, name TEXT NOT NULL);
CREATE OR REPLACE FUNCTION compute_real_date(effective_date DATE, description TEXT) RETURNS DATE AS $$ SELECT CASE WHEN description ~* '^(VIREMENT|VIR|PRLV|AVOIR CARTE) ' THEN effective_date WHEN description ~* '^(CARTE|PAIEMENT PAR CARTE) '  THEN to_date(substring(description from '^(?:CARTE|PAIEMENT PAR CARTE) ([^ ]*) '),'DD/MM/YYYY') WHEN description ~* '^RETRAIT DAB( | .* )../../....' THEN to_date(substring(description FROM '^RETRAIT DAB(?: | .* )(../../....*) '),'DD/MM/YYYY') WHEN description ~* 'RETRAIT DAB [^0-9]' THEN effective_date else null END $$ LANGUAGE sql;
CREATE TYPE e_fix_date AS ENUM ('previous2','previous','previous_if_begin','none','next','next_if_end');
CREATE TABLE mappings(id SERIAL PRIMARY KEY, pattern TEXT NOT NULL, fix_date e_fix_date NOT NULL, period e_period NOT NULL, budget_id INTEGER, categories_id INTEGER[], store_id INTEGER);
CREATE UNIQUE INDEX mappings_pattern_idx ON mappings(pattern);
CREATE TABLE stores(id SERIAL PRIMARY KEY, name TEXT NOT NULL);

CREATE OR REPLACE FUNCTION analyze_transaction() RETURNS trigger AS $analyze_transaction$ DECLARE selected_mapping mappings%rowtype; BEGIN IF NEW.ext_mapping_id >= 1000000 THEN RETURN NEW; ELSE SELECT * INTO selected_mapping FROM mappings WHERE NEW.description ~* mappings.pattern order by length(mappings.pattern) desc, mappings.id limit 1; IF NOT FOUND THEN NEW.ext_date = compute_real_date(NEW.effective_date, NEW.description); NEW.ext_period = 'month'; RETURN NEW; END IF; NEW.ext_date = CASE WHEN selected_mapping.fix_date = 'none' THEN compute_real_date(NEW.effective_date, NEW.description) WHEN selected_mapping.fix_date = 'previous2' THEN date_trunc('month', NEW.effective_date) - INTERVAL '1 month' - INTERVAL '1 day' WHEN selected_mapping.fix_date = 'previous' THEN date_trunc('month', NEW.effective_date) - interval '1 day' WHEN selected_mapping.fix_date = 'previous_if_begin' AND date_part('day', NEW.effective_date) < 15 THEN date_trunc('month', NEW.effective_date) - interval '1 day' WHEN selected_mapping.fix_date = 'previous_if_begin' AND date_part('day', NEW.effective_date) >= 15 THEN NEW.effective_date WHEN selected_mapping.fix_date = 'next' THEN date_trunc('month', NEW.effective_date) + INTERVAL '1 month' WHEN selected_mapping.fix_date = 'next_if_end' AND date_part('day', NEW.effective_date) >= 15 THEN date_trunc('month', NEW.effective_date) + INTERVAL '1 month' WHEN selected_mapping.fix_date = 'next_if_end' AND date_part('day', NEW.effective_date) < 15 THEN NEW.effective_date END; NEW.ext_mapping_id = selected_mapping.id; NEW.ext_period = selected_mapping.period; NEW.ext_budget_id = selected_mapping.budget_id; NEW.ext_categories_id = selected_mapping.categories_id; NEW.ext_store_id = selected_mapping.store_id; RETURN NEW; END IF; END; $analyze_transaction$ LANGUAGE plpgsql;
CREATE TRIGGER analyze_transaction BEFORE INSERT ON transactions FOR EACH ROW EXECUTE PROCEDURE analyze_transaction();
CREATE TRIGGER update_transaction BEFORE UPDATE OF description ON transactions FOR EACH ROW EXECUTE PROCEDURE analyze_transaction();
ALTER sequence mappings_id_seq restart with 1000000;
