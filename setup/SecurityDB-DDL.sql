-- SQL script to load data into the tables use for security


-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
-- DO THIS BEFORE RELEASING TO THE PUBLIC
-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

   INSERT INTO SECURITY_GROUP(SECURITY_GROUP_NAME, SECURITY_GROUP_DESCRIPTION) VALUES
       ('admin', 'A RecipeMix administrator'),
      ('professional', 'A RecipeMix professional who can submit reviews'),
      ('siteModerator', 'A site moderator');

-- Whoever you want to make as administrators
 INSERT INTO SECURITY_GROUPS_USERS VALUES
     ('Milkshake_MAN', 'admin'),
     ('Gordon_Ramsay', 'professional'),
     ('DancesWithDragons', 'siteModerator');
