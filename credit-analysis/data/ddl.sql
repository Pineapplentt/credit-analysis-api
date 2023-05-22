CREATE TABLE IF NOT EXISTS ANALYSIS(
        id_analysis uuid NOT NULL,
        client_id uuid NOT NULL,
        approved boolean,
        approved_limit decimal,
        requested_amount decimal,
        withdraw decimal,
        annual_interest decimal,
        created_at timestamp,
        updated_at timestamp,
        PRIMARY KEY (id_analysis)
);