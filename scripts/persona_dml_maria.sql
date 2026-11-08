USE `persona_db`;
--
INSERT INTO
	`persona_db`.`persona`(`cc`,`nombre`,`apellido`,`genero`,`edad`)
VALUES
	(123456789,'Pepe','Perez','M',30),
	(987654321,'Pepito','Perez','M',null),
	(321654987,'Pepa','Juarez','F',30),
	(147258369,'Pepita','Juarez','F',10),
	(963852741,'Fede','Perez','M',18);
--
INSERT INTO
	`persona_db`.`profesion`(`id`,`nom`,`des`)
VALUES
	(1,'Ingeniero de Sistemas','Disena, desarrolla y mantiene sistemas de software'),
	(2,'Medico','Diagnostica y trata enfermedades'),
	(3,'Abogado','Asesora y representa en asuntos legales'),
	(4,'Arquitecto','Disena edificaciones y espacios');
--
INSERT INTO
	`persona_db`.`telefono`(`num`,`oper`,`duenio`)
VALUES
	('3001234567','Claro',123456789),
	('3109876543','Movistar',123456789),
	('3204567890','Tigo',321654987),
	('3157894561','Claro',963852741);
--
INSERT INTO
	`persona_db`.`estudios`(`id_prof`,`cc_per`,`fecha`,`univer`)
VALUES
	(1,123456789,'2015-12-01','Pontificia Universidad Javeriana'),
	(4,123456789,'2020-06-15','Universidad Nacional de Colombia'),
	(2,321654987,'2018-11-30','Universidad de los Andes'),
	(3,963852741,'2024-07-20','Universidad del Rosario');
--
COMMIT;
