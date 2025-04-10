CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE employees (
                           id   UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                           name VARCHAR(100) NOT NULL,
                           personal_id BIGINT NOT NULL,
                           resource_id UUID NOT NULL
);

CREATE TABLE teams (
                       id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                       name VARCHAR(100) NOT NULL,
                       team_lead_id UUID,
                       CONSTRAINT fk_team_lead
                           FOREIGN KEY(team_lead_id)
                               REFERENCES employees(id)
                               ON DELETE SET NULL
);

CREATE TABLE employee_team (
                               employee_id UUID NOT NULL,
                               team_id     UUID NOT NULL,
                               PRIMARY KEY (employee_id, team_id),
                               CONSTRAINT fk_employee
                                   FOREIGN KEY(employee_id)
                                       REFERENCES employees(id)
                                       ON DELETE CASCADE,
                               CONSTRAINT fk_team
                                   FOREIGN KEY(team_id)
                                       REFERENCES teams(id)
                                       ON DELETE CASCADE
);
