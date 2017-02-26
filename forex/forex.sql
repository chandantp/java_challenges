PRAGMA foreign_keys=ON;
BEGIN TRANSACTION;
CREATE TABLE currencies (
code char(3) primary key,
display_name char(30) not null);

INSERT INTO "currencies" VALUES('USD','US Dollar');
INSERT INTO "currencies" VALUES('INR','Indian Rupee');
INSERT INTO "currencies" VALUES('GBP','British Pound');
INSERT INTO "currencies" VALUES('EUR','Euro');
INSERT INTO "currencies" VALUES('AUD','Australian Dollar');
INSERT INTO "currencies" VALUES('CAD','Canadian Dollar');
INSERT INTO "currencies" VALUES('SGD','Singapore Dollar');
INSERT INTO "currencies" VALUES('CNY','Chinese Yuan Renminbi');
INSERT INTO "currencies" VALUES('JPY','Japanese Yen');
INSERT INTO "currencies" VALUES('CHF','Swiss Franc');
INSERT INTO "currencies" VALUES('NZD','New Zealand Dollar');
COMMIT;

PRAGMA foreign_keys=ON;
BEGIN TRANSACTION;
CREATE TABLE xrates (
code CHAR(3),
per_unit_usd_rate REAL NOT NULL,
FOREIGN KEY (code) REFERENCES currencies(code));

INSERT INTO "xrates" VALUES('INR',0.015333);
INSERT INTO "xrates" VALUES('AUD',0.7051);
INSERT INTO "xrates" VALUES('GBP',1.5181);
INSERT INTO "xrates" VALUES('CAD',0.759965);
INSERT INTO "xrates" VALUES('CNY',0.157351);
INSERT INTO "xrates" VALUES('EUR',1.121252);
INSERT INTO "xrates" VALUES('JPY',0.008339);
INSERT INTO "xrates" VALUES('NZD',0.64442);
INSERT INTO "xrates" VALUES('SGD',0.697885);
INSERT INTO "xrates" VALUES('CHF',1.029405);
INSERT INTO "xrates" VALUES('USD',1.0);
COMMIT;