use grandsiter;

CREATE TABLE user(
	email VARCHAR(50) NOT NULL,
    pw VARCHAR(20) NOT NULL,
    name VARCHAR(20) NOT NULL,
    birthdate DATE NOT NULL,
    PRIMARY KEY(email)
	);
    
CREATE TABLE elder(
	elderno INT NOT NULL,
    name VARCHAR(20) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(10) NOT NULL,
    characteristic VARCHAR(100),
    useremail VARCHAR(50) NOT NULL,
    PRIMARY KEY(elderno),
    FOREIGN KEY(useremail) REFERENCES user(email)
    ON DELETE CASCADE ON UPDATE CASCADE
	);
    
CREATE TABLE schedule(
	scheno INT NOT NULL,
    name VARCHAR(45) NOT NULL,
    time TIME NOT NULL,
    des VARCHAR(50),
    elderno INT NOT NULL,
    PRIMARY KEY(scheno),
    FOREIGN KEY(elderno) REFERENCES elder(elderno)
    ON DELETE CASCADE ON UPDATE CASCADE
);
    
CREATE TABLE medicine(
	medno INT NOT NULL,
    name VARCHAR(20) NOT NULL,
    effect VARCHAR(50) NOT NULL,
    time TIME NOT NULL,
    PRIMARY KEY(medno)
    );
    
CREATE TABLE feces(
	fecesno INT NOT NULL,
    temp INT NOT NULL,
    humid INT NOT NULL,
    gas INT NOT NULL,
    weight INT NOT NULL,
    time TIME NOT NULL,
    elderno INT NOT NULL,
    PRIMARY KEY(fecesno),
    FOREIGN KEY(elderno) REFERENCES elder(elderno)
    ON DELETE CASCADE ON UPDATE CASCADE
    );
    
CREATE TABLE pee(
	peeno INT NOT NULL,
    temp INT NOT NULL,
    humid INT NOT NULL,
    ammonia INT NOT NULL,
    time TIME NOT NULL,
    elderno INT NOT NULL,
    PRIMARY KEY(peeno),
    FOREIGN KEY(elderno) REFERENCES elder(elderno)
    ON DELETE CASCADE ON UPDATE CASCADE
    );
    
CREATE TABLE takemed(
	elderno INT NOT NULL,
    medno INT NOT NULL,
    FOREIGN KEY(elderno) REFERENCES elder(elderno)
    ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY(medno) REFERENCES medicine(medno)
    ON DELETE CASCADE ON UPDATE CASCADE
    );
    