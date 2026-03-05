-- Test data for UC-01: Login
INSERT INTO access_token (email, token, used, created_at, expires_at)
VALUES ('test@example.com', '12345678', false, NOW(), NOW() + INTERVAL '10 minutes');

INSERT INTO access_token (email, token, used, created_at, expires_at)
VALUES ('test@example.com', '99999999', true, NOW(), NOW() + INTERVAL '10 minutes');

INSERT INTO access_token (email, token, used, created_at, expires_at)
VALUES ('test@example.com', '00000000', false, NOW(), NOW() - INTERVAL '1 hour');
