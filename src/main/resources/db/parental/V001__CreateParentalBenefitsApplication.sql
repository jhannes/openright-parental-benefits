create table Parental_Applications (
  id serial primary key,
  applicant_id varchar(20) not null,
  created_at timestamp not null,
  updated_at timestamp not null,
  form json not null
);

create index applications_created_at ON Parental_Applications(created_at);
create index applications_applicant_id ON Parental_Applications(applicant_id);
