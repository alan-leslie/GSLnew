SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS gsl;
CREATE SCHEMA gsl;
USE gsl;

--
-- Table structure for table `address`
--

CREATE TABLE address (
  address_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  address VARCHAR(50) NOT NULL,
  address2 VARCHAR(50) DEFAULT NULL,
  city_id SMALLINT UNSIGNED NOT NULL,
  postal_code VARCHAR(10) DEFAULT NULL,
  
  version_no TINYINT UNSIGNED NOT NULL,
  user_id VARCHAR(50) NOT NULL,
  create_date DATETIME NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY  (address_id),
  KEY idx_fk_city_id (city_id),
  CONSTRAINT `fk_address_city` FOREIGN KEY (city_id) REFERENCES city (city_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `city`
--

CREATE TABLE city (
  city_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  city VARCHAR(50) NOT NULL,
  country_id SMALLINT UNSIGNED NOT NULL,
  
  version_no TINYINT UNSIGNED NOT NULL,
  user_id VARCHAR(50) NOT NULL,
  create_date DATETIME NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY  (city_id),
  KEY idx_fk_country_id (country_id),
  CONSTRAINT `fk_city_country` FOREIGN KEY (country_id) REFERENCES country (country_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `country`
--

CREATE TABLE country (
  country_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  country VARCHAR(50) NOT NULL,
  
  version_no TINYINT UNSIGNED NOT NULL,
  user_id VARCHAR(50) NOT NULL,
  create_date DATETIME NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 
  PRIMARY KEY  (country_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `contact`
-- TODO set constraints

CREATE TABLE contact (
  contact_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  contact_first_name VARCHAR(45) NOT NULL,
  contact_last_name VARCHAR(45) NOT NULL,
  contact_email VARCHAR(50) DEFAULT NULL,
  contact_phone_number VARCHAR(20) DEFAULT NULL,

  version_no TINYINT UNSIGNED NOT NULL,
  user_id VARCHAR(50) NOT NULL,
  create_date DATETIME NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY  (contact_id),
  
  KEY idx_last_name (contact_last_name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `customer`
-- TODO set constraints

CREATE TABLE customer_company (
  customer_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  company_name VARCHAR(40) NOT NULL,
  contact_id SMALLINT UNSIGNED  NOT NULL,
  address_id SMALLINT UNSIGNED NOT NULL,
  notes VARCHAR(255) DEFAULT NULL,

  version_no TINYINT UNSIGNED NOT NULL,
  user_id VARCHAR(50) NOT NULL,
  create_date DATETIME NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY  (customer_id),
  
  KEY idx_fk_address_id (address_id),
  KEY idx_fk_contact (contact_id),
  CONSTRAINT `fk_company_address` FOREIGN KEY (address_id) REFERENCES address (address_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_company_contact` FOREIGN KEY (contact_id) REFERENCES contact (contact_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `material`
--

CREATE TABLE material (
  material_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  material_type VARCHAR(50) DEFAULT NULL,
  description VARCHAR(50) DEFAULT NULL,
  notes VARCHAR(255) DEFAULT NULL,

  version_no TINYINT UNSIGNED NOT NULL,
  user_id VARCHAR(50) NOT NULL,
  create_date DATETIME NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY  (material_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table material sheet`
--

CREATE TABLE material_sheet (
  material_sheet_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  material_id SMALLINT UNSIGNED NOT NULL,
  price VARCHAR(50) DEFAULT NULL,
  thickness VARCHAR(50) NOT NULL,

  version_no TINYINT UNSIGNED NOT NULL,
  user_id VARCHAR(50) NOT NULL,
  create_date DATETIME NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY  (material_sheet_id),
  CONSTRAINT fk_material_sheet_material FOREIGN KEY (material_id) REFERENCES material (material_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table associating material with thickness (sheet)`
--

CREATE TABLE material_batch (
  material_batch_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  stock_item_id SMALLINT UNSIGNED NOT NULL,
  material_sheet_id SMALLINT UNSIGNED NOT NULL,
  quantity VARCHAR(50) DEFAULT NULL,

  version_no TINYINT UNSIGNED NOT NULL,
  user_id VARCHAR(50) NOT NULL,
  create_date DATETIME NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY  (material_batch_id),
  KEY idx_fkstock_item_id (`stock_item_id`),
  KEY idx_fk_material_sheet_id (`material_sheet_id`),
  CONSTRAINT fk_material_Batch_sheet FOREIGN KEY (material_sheet_id) REFERENCES material_sheet (material_sheet_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_Material_batch_stock_item FOREIGN KEY (stock_item_id) REFERENCES stock_item (stock_item_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table associating material sheet with stcok item`
--

CREATE TABLE batch_sheet (
  batch_sheet_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  material_batch_id SMALLINT UNSIGNED NOT NULL,
  bar_code VARCHAR(30),

  version_no TINYINT UNSIGNED NOT NULL,
  user_id VARCHAR(50) NOT NULL,
  create_date DATETIME NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY  (batch_sheet_id),
  KEY idx_fk_material_batch_id (material_batch_id),
  CONSTRAINT fk_material_batch_batch_sheet FOREIGN KEY (material_batch_id) REFERENCES material_batch (material_batch_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `stock item`
--

CREATE TABLE stock_item (
  stock_item_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  quantity SMALLINT UNSIGNED NOT NULL,
  ordered_stock_date DATETIME DEFAULT NULL,
  order_ref VARCHAR(50) DEFAULT NULL,
  received_stock_date DATETIME DEFAULT NULL,
  used_stock_date DATETIME DEFAULT NULL,

  version_no TINYINT UNSIGNED NOT NULL,
  user_id VARCHAR(50) DEFAULT NULL,
  create_date DATETIME NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY  (stock_item_id) 
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table xlient`
-- customer of customer company

CREATE TABLE client (
  client_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  client_name VARCHAR(40) NOT NULL,
  address_id SMALLINT UNSIGNED NOT NULL,

  version_no TINYINT UNSIGNED NOT NULL,
  user_id VARCHAR(50) NOT NULL,
  create_date DATETIME NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY  (client_id),
  
  KEY idx_fk_client_address_id (address_id),
  CONSTRAINT `fk_client_address` FOREIGN KEY (address_id) REFERENCES address (address_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `job`
--

CREATE TABLE job_item (
  job_id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  job_name VARCHAR(50) DEFAULT NULL,
  client_id SMALLINT UNSIGNED NOT NULL,
  stock_item_id SMALLINT UNSIGNED NOT NULL,
  customer_id SMALLINT UNSIGNED NOT NULL,
  template_ref VARCHAR(50) DEFAULT NULL,
  template_date DATETIME DEFAULT NULL,
  estimate_ref VARCHAR(50) DEFAULT NULL,
  estimate_date DATETIME DEFAULT NULL,
  invoice_ref VARCHAR(50) DEFAULT NULL,
  invoice_date DATETIME DEFAULT NULL,
  notes VARCHAR(255) DEFAULT NULL,

  version_no TINYINT UNSIGNED NOT NULL,
  user_id VARCHAR(50) NOT NULL,
  create_date DATETIME NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  last_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  
  PRIMARY KEY(job_id),
  KEY idx_fk_job_client_id (client_id),
  KEY idx_fk_job_stock_item_id (stock_item_id),
  KEY idx_fk_job_customaer_id (customer_id),
  CONSTRAINT `fk_job_client` FOREIGN KEY (client_id) REFERENCES client (client_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_job_stock` FOREIGN KEY (stock_item_id) REFERENCES stock_item (stock_item_id) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_job_customer` FOREIGN KEY (customer_id) REFERENCES customer_company (customer_id) ON DELETE RESTRICT ON UPDATE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


