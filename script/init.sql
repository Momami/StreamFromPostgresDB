CREATE DATABASE world;
\c world;
CREATE TABLE applicant (
	 id INTEGER PRIMARY KEY NOT NULL,
	 first_name VARCHAR(255) NOT NULL,
	 surname VARCHAR(255) NOT NULL,
	 other_names VARCHAR(255),
	 position VARCHAR(255) NOT NULL
);

CREATE TABLE hobbies (
	applicant_id INTEGER REFERENCES applicant (id),
    hobby varchar(255)
);

INSERT INTO applicant VALUES
	(1, 'Ivanov', 'Ivan', 'Ivanovich', 'number one'),
	(3, 'Dodova', 'Lora', '', 'two'),
	(6, 'Alien', 'Karen', '', 'position1');
	
INSERT INTO hobbies VALUES
	(1, 'Magic'),
	(3, 'Cooking'),
	(3, 'Swimming');