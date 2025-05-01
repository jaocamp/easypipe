CREATE TABLE IF NOT EXISTS pipeline_execution (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL,
    context_data JSONB NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS step_execution (
    id UUID PRIMARY KEY,
    pipeline_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL,
    context_data JSONB NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT step_execution_pipeline_id_fkey FOREIGN KEY (pipeline_id)
        REFERENCES pipeline_execution (id) ON DELETE CASCADE
);