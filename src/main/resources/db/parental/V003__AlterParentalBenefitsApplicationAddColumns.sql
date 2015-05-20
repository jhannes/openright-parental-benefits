ALTER TABLE parental_applications ADD COLUMN application_type varchar(50) null;
ALTER TABLE parental_applications ADD COLUMN office varchar(10) null;

create index parental_applications_type ON parental_applications(application_type);
create index parental_applications_office ON parental_applications(office);

update parental_applications set office = '0000', application_type = 'invalid';

alter table parental_applications alter COLUMN application_type set NOT NULL;
alter table parental_applications alter COLUMN office set NOT NULL;
