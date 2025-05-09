-- polulate super dimension in db;
INSERT IGNORE INTO dimension (id, name,is_active,display_name) VALUES ('e7b1d516-a4d3-42a4-addb-07288b3e2535', 'super', true,'Super');

-- populate master client in db;
INSERT IGNORE INTO client (client_id,name,client_secret,grant_type,redirect_uri,dimension_id) VALUES ('203b3aba-2c62-4404-9566-0fb77f91d8a0','master','0a5f4360-ae03-4f8a-8020-189a934a4e05-9873e4ca-403b-4ee6-99b3-2c6e7b7e380d','authorization_code','http://localhost:8085/*','e7b1d516-a4d3-42a4-addb-07288b3e2535');

-- populate user for master client;
INSERT IGNORE INTO user (id, email, first_name, is_verified, last_name, username, client_id) VALUES ('9e9d251d-50fe-4e41-a22b-e332b7edbf85', 'admin@email.com', 'Admin', 1, NULL, 'admin', '203b3aba-2c62-4404-9566-0fb77f91d8a0');

-- populate credential for admin user
INSERT IGNORE INTO credential (id, created_at, hash, is_active, is_deleted, user_id) VALUES ('ba01f73f-e478-463e-bfd0-474570d61eac', NULL, '$2a$10$329T2O7xNQCR.Qv.WQIiYesMk2dRrayKK/zaeggDzsBgpD8AdSeTi', true, false, '9e9d251d-50fe-4e41-a22b-e332b7edbf85');



