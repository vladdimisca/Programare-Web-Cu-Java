CREATE TABLE IF NOT EXISTS `addresses` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `country` VARCHAR(255) NOT NULL,
    `province` VARCHAR(255) NOT NULL,
    `city` VARCHAR(255) NOT NULL,
    `street` VARCHAR(255) NOT NULL,
    `number` VARCHAR(255) NOT NULL,
    `other` VARCHAR(255),

    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `user_infos` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `first_name` VARCHAR(255) NOT NULL,
    `last_name` VARCHAR(255) NOT NULL,
    `birth_date` DATE NOT NULL,
    `civil_status` VARCHAR(50) NOT NULL,
    `cnp` VARCHAR(20) NOT NULL,
    `sex` VARCHAR(20) NOT NULL,
    `nationality` VARCHAR(100) NOT NULL,
    `phone_number` VARCHAR(20) NOT NULL,
    `fk_address` BIGINT NOT NULL,

    PRIMARY KEY (`id`),
    FOREIGN KEY (`fk_address`) REFERENCES `addresses`(`id`)
);

CREATE TABLE IF NOT EXISTS `documents` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `identity_card` VARCHAR(255) NOT NULL,
    `medical_certificate` VARCHAR(255) NOT NULL,
    `diploma` VARCHAR(255) NOT NULL,

    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `users` (
    `id` VARCHAR(36) NOT NULL,
    `email` VARCHAR(255) UNIQUE NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(50) NOT NULL,
    `created_at` DATETIME NOT NULL,
    `fk_user_info` BIGINT,
    `fk_documents` BIGINT,

    PRIMARY KEY (`id`),
    FOREIGN KEY (`fk_user_info`) REFERENCES `user_infos`(`id`),
    FOREIGN KEY (`fk_documents`) REFERENCES `documents`(`id`)
);

CREATE TABLE IF NOT EXISTS `programs_of_study` (
    `id` VARCHAR(36) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `type` VARCHAR(255) NOT NULL,
    `number_of_years` INT NOT NULL,
    `number_of_students` INT NOT NULL,
    `financing_type` VARCHAR(100) NOT NULL,

    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `users_programs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `fk_user` VARCHAR(36) NOT NULL,
    `fk_program` VARCHAR(36) NOT NULL,
    `grade` INT,

    PRIMARY KEY (`id`),
    FOREIGN KEY (`fk_user`) REFERENCES `users`(`id`),
    FOREIGN KEY (`fk_program`) REFERENCES `programs_of_study`(`id`)
);

CREATE TABLE IF NOT EXISTS `admission_files` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `submitted_at` DATETIME NOT NULL,
    `status` VARCHAR(50) NOT NULL,
    `fk_user` VARCHAR(36) NOT NULL,

    PRIMARY KEY (`id`),
    FOREIGN KEY (`fk_user`) REFERENCES `users`(`id`)
);

