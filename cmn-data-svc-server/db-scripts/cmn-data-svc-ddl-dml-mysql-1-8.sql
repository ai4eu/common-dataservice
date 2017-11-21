-- DDL and DML for tables managed by the Common Data Service version 1.8.x
-- No database is created or specified to allow flexible deployment;
-- also see script cmn-data-svc-base-mysql.sql.

-- DDL --

DROP TABLE IF EXISTS C_USER;
CREATE TABLE C_USER (
  USER_ID CHAR(36) NOT NULL PRIMARY KEY,
  FIRST_NAME varchar(50),
  MIDDLE_NAME varchar(50),
  LAST_NAME varchar(50),
  ORG_NAME varchar(50),
  EMAIL varchar(100) UNIQUE,
  LOGIN_NAME varchar(25) NOT NULL UNIQUE,
  LOGIN_HASH varchar(64),
  LOGIN_PASS_EXPIRE_DATE DATETIME NULL default NULL,
  -- JSON web token
  AUTH_TOKEN varchar(4096),
  ACTIVE_YN char(1) DEFAULT 'Y' NOT NULL,
  LAST_LOGIN_DATE DATETIME NULL DEFAULT NULL,
  PICTURE BLOB,
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_ROLE;
CREATE TABLE C_ROLE (
  ROLE_ID CHAR(36) NOT NULL PRIMARY KEY,
  NAME VARCHAR(100) NOT NULL,
  ACTIVE_YN CHAR(1) DEFAULT 'Y' NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Many:many mapping of user to role requires a map (join) table
DROP TABLE IF EXISTS C_USER_ROLE_MAP;
CREATE TABLE C_USER_ROLE_MAP (
  USER_ID CHAR(36) NOT NULL,
  ROLE_ID CHAR(36) NOT NULL,
  PRIMARY KEY (USER_ID, ROLE_ID),
  CONSTRAINT FK_C_USER_ROLE_MAP_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID),
  CONSTRAINT FK_C_USER_ROLE_MAP_C_ROLE FOREIGN KEY (ROLE_ID) REFERENCES C_ROLE (ROLE_ID)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_ROLE_FUNCTION;
CREATE TABLE C_ROLE_FUNCTION (
  ROLE_FUNCTION_ID CHAR(36) NOT NULL PRIMARY KEY,
  NAME VARCHAR(100) NOT NULL,
  ROLE_ID CHAR(36) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_ROLE_FUNCTION_C_ROLE FOREIGN KEY (ROLE_ID) REFERENCES C_ROLE (ROLE_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_LOGIN_PROVIDER;
CREATE TABLE C_LOGIN_PROVIDER (
  PROVIDER_CD CHAR(2) NOT NULL PRIMARY KEY,
  PROVIDER_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_USER_LOGIN_PROVIDER;
CREATE TABLE C_USER_LOGIN_PROVIDER (
  USER_ID         CHAR(36) NOT NULL,
  PROVIDER_CD     CHAR(2) NOT NULL,
  PROVIDER_USER_ID VARCHAR(255) NOT NULL,
  RANK            SMALLINT NOT NULL,
  DISPLAY_NAME    VARCHAR(256),
  PROFILE_URL     VARCHAR(512),
  IMAGE_URL       VARCHAR(512),
  SECRET          VARCHAR(256),
  ACCESS_TOKEN    VARCHAR(256) NOT NULL,
  REFRESH_TOKEN   VARCHAR(256),
  EXPIRE_TIME     TIMESTAMP,
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_USER_LOGIN_PROVIDER_PK PRIMARY KEY (USER_ID, PROVIDER_CD, PROVIDER_USER_ID),
  CONSTRAINT C_USER_LOGIN_PROVIDER_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID),
  CONSTRAINT C_USR_LOGIN_PROVIDER_C_LOGIN_PROVIDER FOREIGN KEY (PROVIDER_CD) REFERENCES C_LOGIN_PROVIDER (PROVIDER_CD)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_ACCESS_TYPE;
CREATE TABLE C_ACCESS_TYPE (
  TYPE_CD CHAR(2) NOT NULL PRIMARY KEY,
  TYPE_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_TOOLKIT_TYPE;
CREATE TABLE C_TOOLKIT_TYPE (
  TYPE_CD CHAR(2) NOT NULL PRIMARY KEY,
  TYPE_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_MODEL_TYPE;
CREATE TABLE C_MODEL_TYPE (
  TYPE_CD CHAR(2) NOT NULL PRIMARY KEY,
  TYPE_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_VALIDATION_TYPE;
CREATE TABLE C_VALIDATION_TYPE (
  TYPE_CD CHAR(2) NOT NULL PRIMARY KEY,
  TYPE_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_VALIDATION_STATUS;
CREATE TABLE C_VALIDATION_STATUS (
  STATUS_CD CHAR(2) NOT NULL PRIMARY KEY,
  STATUS_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_DEPLOYMENT_STATUS;
CREATE TABLE C_DEPLOYMENT_STATUS (
  STATUS_CD CHAR(2) NOT NULL PRIMARY KEY,
  STATUS_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_SOLUTION;
CREATE TABLE C_SOLUTION (
  SOLUTION_ID CHAR(36) NOT NULL PRIMARY KEY,
  NAME VARCHAR(100) NOT NULL,
  DESCRIPTION VARCHAR(512),
  OWNER_ID CHAR(36) NOT NULL,
  PROVIDER CHAR(64),
  ACTIVE_YN CHAR(1) DEFAULT 'Y' NOT NULL,
  ACCESS_TYPE_CD CHAR(2),
  MODEL_TYPE_CD CHAR(2),
  TOOLKIT_TYPE_CD CHAR(2),
  VALIDATION_STATUS_CD CHAR(2),
  -- MariaDB does not support JSON column type
  METADATA VARCHAR(1024) CHECK (METADATA IS NULL OR JSON_VALID(METADATA)),
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_SOLUTION_C_USER FOREIGN KEY (OWNER_ID) REFERENCES C_USER (USER_ID),
  CONSTRAINT C_SOLUTION_C_ACCESS_TYPE FOREIGN KEY (ACCESS_TYPE_CD) REFERENCES C_ACCESS_TYPE (TYPE_CD),
  CONSTRAINT C_SOLUTION_C_MODEL_TYPE FOREIGN KEY (MODEL_TYPE_CD) REFERENCES C_MODEL_TYPE (TYPE_CD),
  CONSTRAINT C_SOLUTION_C_TOOLKIT_TYPE FOREIGN KEY (TOOLKIT_TYPE_CD) REFERENCES C_TOOLKIT_TYPE (TYPE_CD),
  CONSTRAINT C_SOLUTION_C_VALIDATION_STATUS FOREIGN KEY (VALIDATION_STATUS_CD) REFERENCES C_VALIDATION_STATUS (STATUS_CD)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_SOLUTION_WEB;
CREATE TABLE C_SOLUTION_WEB (
  SOLUTION_ID CHAR(36) NOT NULL PRIMARY KEY,
  VIEW_COUNT INT,
  DOWNLOAD_COUNT INT,
  LAST_DOWNLOAD TIMESTAMP,
  RATING_COUNT INT,
  RATING_AVG_TENTHS INT,
  FEATURED_YN char(1),
  CONSTRAINT C_SOL_WEB_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Many:one mapping with solution; no need for map (join) table
DROP TABLE IF EXISTS C_SOLUTION_REV;
CREATE TABLE C_SOLUTION_REV (
  REVISION_ID CHAR(36) NOT NULL PRIMARY KEY,
  SOLUTION_ID CHAR(36) NOT NULL,
  VERSION VARCHAR(25) NOT NULL,
  DESCRIPTION VARCHAR(512),
  OWNER_ID CHAR(36) NOT NULL,
  -- MariaDB does not support JSON column type
  METADATA VARCHAR(1024) CHECK (METADATA IS NULL OR JSON_VALID(METADATA)),
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_SOLUTION_REV_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_SOLUTION_REV_C_USER     FOREIGN KEY (OWNER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_ARTIFACT_TYPE;
CREATE TABLE C_ARTIFACT_TYPE (
  TYPE_CD CHAR(2) NOT NULL PRIMARY KEY,
  TYPE_NAME VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_ARTIFACT;
CREATE TABLE C_ARTIFACT (
  ARTIFACT_ID CHAR(36) NOT NULL PRIMARY KEY,
  VERSION VARCHAR(25) NOT NULL,
  -- Value set restricted by type table
  ARTIFACT_TYPE_CD CHAR(2) NOT NULL,
  NAME VARCHAR(100) NOT NULL,
  DESCRIPTION VARCHAR(512),
  URI VARCHAR(512) NOT NULL,
  OWNER_ID CHAR(36) NOT NULL,
  SIZE INT NOT NULL,
  -- MariaDB does not support JSON column type
  METADATA VARCHAR(1024) CHECK (METADATA IS NULL OR JSON_VALID(METADATA)),
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_ARTIFACT_C_USER FOREIGN KEY (OWNER_ID) REFERENCES C_USER (USER_ID),
  CONSTRAINT C_ARTIFACT_C_ARTIFACT_TYPE FOREIGN KEY (ARTIFACT_TYPE_CD) REFERENCES C_ARTIFACT_TYPE (TYPE_CD)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Many:many mapping of solution_rev to artifact requires a map (join) table
DROP TABLE IF EXISTS C_SOL_REV_ART_MAP;
CREATE TABLE C_SOL_REV_ART_MAP (
  REVISION_ID CHAR(36) NOT NULL,
  ARTIFACT_ID CHAR(36) NOT NULL,
  PRIMARY KEY (REVISION_ID, ARTIFACT_ID),
  CONSTRAINT C_SOL_REV_ART_MAP_C_SOLUTION_REV FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID),
  CONSTRAINT C_SOL_REV_ART_MAP_C_ARTIFACT     FOREIGN KEY (ARTIFACT_ID) REFERENCES C_ARTIFACT (ARTIFACT_ID)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Many:many mapping of solution to solution (composite) requires a map (join) table
DROP TABLE IF EXISTS C_COMP_SOL_MAP;
CREATE TABLE C_COMP_SOL_MAP (
  PARENT_ID CHAR(36) NOT NULL,
  CHILD_ID CHAR(36) NOT NULL,
  PRIMARY KEY (PARENT_ID, CHILD_ID),
  CONSTRAINT C_COMP_SOL_MAP_PARENT FOREIGN KEY (PARENT_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_COMP_SOL_MAP_CHILD  FOREIGN KEY (CHILD_ID)  REFERENCES C_SOLUTION (SOLUTION_ID)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- No ID column, no created/modified columns, just a name
-- because the content is shorter than a UUID field.
DROP TABLE IF EXISTS C_SOLUTION_TAG;
CREATE TABLE C_SOLUTION_TAG (
  TAG VARCHAR(32) NOT NULL PRIMARY KEY
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Many:many mapping of solution to tag requires a map (join) table
DROP TABLE IF EXISTS C_SOL_TAG_MAP;
CREATE TABLE C_SOL_TAG_MAP (
  SOLUTION_ID CHAR(36) NOT NULL,
  TAG VARCHAR(32) NOT NULL,
  PRIMARY KEY (SOLUTION_ID, TAG),
  CONSTRAINT C_SOL_TAG_MAP_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_SOL_TAG_MAP_C_SOLUTION_TAG FOREIGN KEY (TAG) REFERENCES C_SOLUTION_TAG (TAG)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_PEER;
CREATE TABLE C_PEER (
  PEER_ID CHAR(36) NOT NULL PRIMARY KEY,
  NAME VARCHAR(50) NOT NULL,
  -- X.509 certificate subject name
  SUBJECT_NAME VARCHAR(100) NOT NULL,
  DESCRIPTION VARCHAR(512),
  API_URL VARCHAR(512) NOT NULL,
  WEB_URL VARCHAR(512) NOT NULL,
  IS_ACTIVE CHAR(1) NOT NULL DEFAULT 'Y',
  IS_SELF CHAR(1) NOT NULL DEFAULT 'N',
  CONTACT1 VARCHAR(100) NOT NULL,
  CONTACT2 VARCHAR(100) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_PEER_SUB;
CREATE TABLE C_PEER_SUB (
  SUB_ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  PEER_ID CHAR(36) NOT NULL,
  SELECTOR VARCHAR(1024) CHECK (SELECTOR IS NULL OR JSON_VALID(SELECTOR)),
  -- Seconds
  REFRESH_INTERVAL INT,
  -- Bytes
  MAX_ARTIFACT_SIZE INT,
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT `C_PEER_SUB_C_PEER` FOREIGN KEY (PEER_ID) REFERENCES C_PEER (PEER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_SOLUTION_FAVORITE;
CREATE TABLE C_SOLUTION_FAVORITE (
  SOLUTION_ID CHAR(36) NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  PRIMARY KEY (SOLUTION_ID, USER_ID),
  CONSTRAINT C_SOLUTION_FAVORITE_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_SOLUTION_FAVORITE_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_SOLUTION_DOWNLOAD;
CREATE TABLE C_SOLUTION_DOWNLOAD (
  DOWNLOAD_ID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  SOLUTION_ID CHAR(36) NOT NULL,
  ARTIFACT_ID CHAR(36) NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  DOWNLOAD_DATE TIMESTAMP NOT NULL,
  INDEX (SOLUTION_ID),
  CONSTRAINT C_SOLUTION_DOWNLOAD_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_SOLUTION_DOWNLOAD_C_ARTIFACT FOREIGN KEY (ARTIFACT_ID) REFERENCES C_ARTIFACT (ARTIFACT_ID),
  CONSTRAINT C_SOLUTION_DOWNLOAD_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_SOLUTION_RATING;
CREATE TABLE C_SOLUTION_RATING (
  SOLUTION_ID CHAR(36) NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  RATING SMALLINT,
  TEXT_REVIEW VARCHAR(1024),
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  PRIMARY KEY (SOLUTION_ID, USER_ID),
  CONSTRAINT C_SOLUTION_RATING_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_SOLUTION_RATING_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_NOTIFICATION;
CREATE TABLE C_NOTIFICATION (
  NOTIFICATION_ID CHAR(36) NOT NULL PRIMARY KEY,
  TITLE VARCHAR(100) NOT NULL,
  MESSAGE VARCHAR(2048),
  URL VARCHAR(512),
  -- disable auto-update behavior with default values
  START_DATE DATETIME NOT NULL DEFAULT 0,
  END_DATE DATETIME NOT NULL DEFAULT 0,
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Also has an attribute, not just ID columns
DROP TABLE IF EXISTS C_NOTIF_USER_MAP;
CREATE TABLE C_NOTIF_USER_MAP (
  NOTIFICATION_ID CHAR(36) NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  -- disable auto-update behavior with default value
  VIEWED_DATE DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (NOTIFICATION_ID, USER_ID),
  CONSTRAINT C_NOTIF_USER_MAP_C_NOTIFICATION FOREIGN KEY (NOTIFICATION_ID) REFERENCES C_NOTIFICATION (NOTIFICATION_ID),
  CONSTRAINT C_NOTIF_USER_MAP_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Access control list is a many:many mapping of solution to user, requires a map (join) table
DROP TABLE IF EXISTS C_SOL_USER_ACCESS_MAP;
CREATE TABLE C_SOL_USER_ACCESS_MAP (
  SOLUTION_ID CHAR(36) NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  PRIMARY KEY (SOLUTION_ID, USER_ID),
  CONSTRAINT C_SOL_USER_ACCESS_MAP_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_SOL_USER_ACCESS_MAP_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_SOLUTION_VALIDATION;
CREATE TABLE C_SOLUTION_VALIDATION (
  SOLUTION_ID CHAR(36) NOT NULL,
  REVISION_ID CHAR(36) NOT NULL,
  TASK_ID CHAR(36) NOT NULL,
  VAL_TYPE_CD CHAR(2) NOT NULL,
  VAL_STATUS_CD CHAR(2),
  -- MariaDB does not support JSON column type
  DETAIL VARCHAR(1024) CHECK (DETAIL IS NULL OR JSON_VALID(DETAIL)),
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  PRIMARY KEY (SOLUTION_ID, REVISION_ID, TASK_ID),
  CONSTRAINT C_SOL_VAL_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_SOL_VAL_C_REVISION FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID),
  CONSTRAINT C_SOL_VAL_C_VAL_TYPE FOREIGN KEY (VAL_TYPE_CD) REFERENCES C_VALIDATION_TYPE (TYPE_CD),
  CONSTRAINT C_SOL_VAL_C_VAL_STATUS FOREIGN KEY (VAL_STATUS_CD) REFERENCES C_VALIDATION_STATUS (STATUS_CD)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_SOL_VAL_SEQ;
CREATE TABLE C_SOL_VAL_SEQ (
  SEQ SMALLINT NOT NULL,
  VAL_TYPE_CD CHAR(2) NOT NULL,
  PRIMARY KEY (SEQ, VAL_TYPE_CD),
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_SOL_VAL_SEQ_C_VAL_TYPE FOREIGN KEY (VAL_TYPE_CD) REFERENCES C_VALIDATION_TYPE (TYPE_CD)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_SOLUTION_DEPLOYMENT;
CREATE TABLE C_SOLUTION_DEPLOYMENT (
  DEPLOYMENT_ID CHAR(36) NOT NULL PRIMARY KEY,
  SOLUTION_ID CHAR(36) NOT NULL,
  REVISION_ID CHAR(36) NOT NULL,
  USER_ID CHAR(36) NOT NULL,
  DEP_STATUS_CD CHAR(2) NOT NULL,
  TARGET VARCHAR(64),
  DETAIL VARCHAR(1024) CHECK (DETAIL IS NULL OR JSON_VALID(DETAIL)),
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_SOL_DEP_C_SOLUTION FOREIGN KEY (SOLUTION_ID) REFERENCES C_SOLUTION (SOLUTION_ID),
  CONSTRAINT C_SOL_DEP_C_REVISION FOREIGN KEY (REVISION_ID) REFERENCES C_SOLUTION_REV (REVISION_ID),
  CONSTRAINT C_SOL_DEP_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID),
  CONSTRAINT C_SOL_DEP_C_DEP_STATUS FOREIGN KEY (DEP_STATUS_CD) REFERENCES C_DEPLOYMENT_STATUS (STATUS_CD)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS C_SITE_CONFIG;
CREATE TABLE C_SITE_CONFIG (
  CONFIG_KEY VARCHAR(50) NOT NULL PRIMARY KEY,
  CONFIG_VAL VARCHAR(1024) NOT NULL CHECK (JSON_VALID(CONFIG_VAL)),
  USER_ID CHAR(36) NOT NULL,
  CREATED_DATE TIMESTAMP NOT NULL,
  MODIFIED_DATE TIMESTAMP NOT NULL,
  CONSTRAINT C_SITE_CONFIG_C_USER FOREIGN KEY (USER_ID) REFERENCES C_USER (USER_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- DDL--

# Also see enum AccessTypeCode
INSERT INTO C_ACCESS_TYPE (TYPE_CD, TYPE_NAME) VALUES ('OR', 'Organization');
INSERT INTO C_ACCESS_TYPE (TYPE_CD, TYPE_NAME) VALUES ('PB', 'Public');
INSERT INTO C_ACCESS_TYPE (TYPE_CD, TYPE_NAME) VALUES ('PR', 'Private');

# Also see enum ArtifactTypeCode
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('BP', 'BLUEPRINT FILE' );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('CD', 'CDUMP FILE'     );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('DI', 'DOCKER IMAGE'   );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('DS', 'DATA SOURCE'    );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('MD', 'METADATA'       );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('MH', 'MODEL-H2O'      );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('MI', 'MODEL IMAGE'    );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('MR', 'MODEL-R'        );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('MS', 'MODEL-SCIKIT'   );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('MT', 'MODEL-TENSORFLOW');
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('TE', 'TOSCA TEMPLATE' );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('TG', 'TOSCA Generator Input File');
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('TS', 'TOSCA SCHEMA'   );
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('TT', 'TOSCA TRANSLATE');
INSERT INTO C_ARTIFACT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('PJ', 'PROTOBUF FILE');

# Also see enum LoginProviderCode
INSERT INTO C_LOGIN_PROVIDER (PROVIDER_CD, PROVIDER_NAME) VALUES ('FB', 'Facebook');
INSERT INTO C_LOGIN_PROVIDER (PROVIDER_CD, PROVIDER_NAME) VALUES ('GH', 'GitHub');
INSERT INTO C_LOGIN_PROVIDER (PROVIDER_CD, PROVIDER_NAME) VALUES ('GP', 'Google Plus');
INSERT INTO C_LOGIN_PROVIDER (PROVIDER_CD, PROVIDER_NAME) VALUES ('LI', 'LinkedIn');

# Also see enum ModelTypeCode
INSERT INTO C_MODEL_TYPE (TYPE_CD, TYPE_NAME) VALUES ('CL', 'Classification');
INSERT INTO C_MODEL_TYPE (TYPE_CD, TYPE_NAME) VALUES ('DS', 'Data Sources');
INSERT INTO C_MODEL_TYPE (TYPE_CD, TYPE_NAME) VALUES ('DT', 'Data Transformer');
INSERT INTO C_MODEL_TYPE (TYPE_CD, TYPE_NAME) VALUES ('PR', 'Prediction');
INSERT INTO C_MODEL_TYPE (TYPE_CD, TYPE_NAME) VALUES ('RG', 'Regression');

# Also see enum ToolkitTypeCode
INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('CP', 'Composite Solution');
INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('DS', 'Design Studio');
INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('H2', 'H2O');
INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('RC', 'R');
INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('SK', 'Scikit-Learn');
INSERT INTO C_TOOLKIT_TYPE (TYPE_CD, TYPE_NAME) VALUES ('TF', 'TensorFlow');

# Also see enum ValidationStatusCode
INSERT INTO C_VALIDATION_STATUS (STATUS_CD, STATUS_NAME) VALUES ('FA', 'Failed');
INSERT INTO C_VALIDATION_STATUS (STATUS_CD, STATUS_NAME) VALUES ('IP', 'In Progress');
INSERT INTO C_VALIDATION_STATUS (STATUS_CD, STATUS_NAME) VALUES ('NV', 'Not Validated');
INSERT INTO C_VALIDATION_STATUS (STATUS_CD, STATUS_NAME) VALUES ('PS', 'Passed');
INSERT INTO C_VALIDATION_STATUS (STATUS_CD, STATUS_NAME) VALUES ('SB', 'Submitted');

# Also see enum ValidationTypeCode
INSERT INTO C_VALIDATION_TYPE (TYPE_CD, TYPE_NAME) VALUES ('SS', 'Security Scan');
INSERT INTO C_VALIDATION_TYPE (TYPE_CD, TYPE_NAME) VALUES ('LC', 'License Check');
INSERT INTO C_VALIDATION_TYPE (TYPE_CD, TYPE_NAME) VALUES ('OQ', 'OSS Quantification');
INSERT INTO C_VALIDATION_TYPE (TYPE_CD, TYPE_NAME) VALUES ('TA', 'Text Analysis');

# Also see enum DeploymentStatusCode
INSERT INTO C_DEPLOYMENT_STATUS (STATUS_CD, STATUS_NAME) VALUES ('DP', 'Deployed');
INSERT INTO C_DEPLOYMENT_STATUS (STATUS_CD, STATUS_NAME) VALUES ('FA', 'Failed');
INSERT INTO C_DEPLOYMENT_STATUS (STATUS_CD, STATUS_NAME) VALUES ('IP', 'In Progress');
INSERT INTO C_DEPLOYMENT_STATUS (STATUS_CD, STATUS_NAME) VALUES ('ST', 'Started');

INSERT INTO C_ROLE (ROLE_ID, NAME, ACTIVE_YN) VALUES ('12345678-abcd-90ab-cdef-1234567890ab', 'MLP System User', 'Y');
