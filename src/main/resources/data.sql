INSERT INTO users (name, last_name, username, password, is_enabled, email, intents) values ('Francisco', 'Busleiman', 'franbusleiman', '$2a$10$srp9BbckIn/jfCs8dSAwueD1.dB9NT4waf32/9ee2gsWo9r0sKqK.', true, 'franbuslieman@gmail.com',0);
INSERT INTO users (name, last_name, username, password, is_enabled, email, intents) values ('Juan', 'Perez', 'juanperez', '$2a$10$srp9BbckIn/jfCs8dSAwueD1.dB9NT4waf32/9ee2gsWo9r0sKqK.', true, 'jaunperez@gmail.com',0);

INSERT INTO roles (name) values ('ROLE_ADMIN');
INSERT INTO roles (name) values ('ROLE_USER');

INSERT INTO user_roles(role_id, user_id) values(1, 1);
INSERT INTO user_roles(role_id, user_id) values(1, 2);
INSERT INTO user_roles(role_id, user_id) values(2, 2);
