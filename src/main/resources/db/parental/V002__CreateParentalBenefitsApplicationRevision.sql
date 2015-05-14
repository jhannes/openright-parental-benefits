create table Parental_Application_Revisions (
  id serial primary key,
  application_id integer not null,
  user_id varchar(20) not null,
  status varchar(20) not null,
  created_at timestamp not null,
  form json not null
);

create index application_revs_created_at ON Parental_Application_Revisions(created_at);
